package br.com.crmHdmSamBackend.service;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.security.SessionManager;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.security.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;

    @Autowired
    public AuthenticationService(UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder,
                                 SessionManager sessionManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionManager = sessionManager;
        log.info("AuthenticationService inicializado com PasswordEncoder: {}", passwordEncoder.getClass().getName());
    }

    @Transactional
    public AuthenticationResult autenticar(String login, String senha) {
        log.info("=== INICIANDO AUTENTICAÇÃO ===");
        log.info("Login recebido: '{}'", login);
        log.info("Senha recebida (length): {}", senha != null ? senha.length() : 0);

        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            log.warn("Login ou senha vazios");
            return AuthenticationResult.failure("Login e senha são obrigatórios");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login.trim());

        if (usuarioOpt.isEmpty()) {
            log.warn("Usuário não encontrado: '{}'", login);
            return AuthenticationResult.failure("Credenciais inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        log.info("Usuário encontrado: {} (ID: {})", usuario.getNome(), usuario.getId());
        log.info("Ativo: {} | Admin: {} | Tentativas: {}", usuario.isAtivo(), usuario.isAdmin(), usuario.getTentativasLogin());

        if (!usuario.isAtivo()) {
            log.warn("Usuário inativo: {}", login);
            return AuthenticationResult.failure("Usuário inativo");
        }

        if (usuario.isBloqueado()) {
            log.warn("Usuário bloqueado: {} até {}", login, usuario.getBloqueadoAte());
            return AuthenticationResult.failure("Usuário bloqueado temporariamente. Tente novamente mais tarde.");
        }

        if (usuario.getSenha() == null) {
            log.error("Usuário sem senha definida: {}", login);
            return AuthenticationResult.failure("Credenciais inválidas");
        }

        log.info("Hash da senha no banco: {}", usuario.getSenha());
        log.info("PasswordEncoder usado: {}", passwordEncoder.getClass().getName());
        log.info("Senha digitada: '{}'", senha);

        String testeHash = passwordEncoder.encode(senha);
        log.info("Hash da senha digitada (teste): {}", testeHash);

        boolean senhaCorreta = passwordEncoder.matches(senha, usuario.getSenha());
        log.info("Resultado do matches: {}", senhaCorreta);

        if (!senhaCorreta) {
            usuario.incrementarTentativasLogin();
            usuarioRepository.save(usuario);
            log.warn("Senha incorreta para usuário: {} - Tentativas: {}/{}", login, usuario.getTentativasLogin(), 5);

            if (usuario.isBloqueado()) {
                log.warn("Usuário bloqueado após {} tentativas: {}", usuario.getTentativasLogin(), login);
                return AuthenticationResult.failure("Muitas tentativas incorretas. Usuário bloqueado por 15 minutos.");
            }

            return AuthenticationResult.failure("Credenciais inválidas");
        }

        log.info("✓ Senha correta! Autenticando usuário: {}", login);

        usuario.resetarTentativasLogin();
        usuario.atualizarUltimoLogin();
        usuarioRepository.save(usuario);
        log.info("✓ Tentativas resetadas e último login atualizado");

        sessionManager.setUsuarioAutenticado(usuario);
        log.info("✓ Sessão criada para usuário: {}", login);
        log.info("=== AUTENTICAÇÃO CONCLUÍDA COM SUCESSO ===");
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (usuario.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getLogin(),
                null,
                authorities
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession != null && vaadinSession.getSession() != null) {
            vaadinSession.getSession().setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );
        }

        log.info("✓ Spring Security Context configurado com roles: {}", authorities);

        log.info("=== AUTENTICAÇÃO CONCLUÍDA COM SUCESSO ===");

        return AuthenticationResult.success(usuario);
    }



    public void logout() {
        Optional<Usuario> usuario = sessionManager.getUsuarioAutenticado();
        usuario.ifPresent(u -> log.info("Logout do usuário: {}", u.getLogin()));
        sessionManager.logout();
    }

    public Optional<Usuario> getUsuarioAutenticado() {
        return sessionManager.getUsuarioAutenticado();
    }

    public boolean isUsuarioAutenticado() {
        return sessionManager.isUsuarioAutenticado();
    }

    public boolean isUsuarioAdmin() {
        return sessionManager.isUsuarioAdmin();
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario, String senhaPlana) {
        log.info("Criando novo usuário: {}", usuario.getLogin());
        if (senhaPlana != null && !senhaPlana.isEmpty()) {
            String senhaHash = passwordEncoder.encode(senhaPlana);
            usuario.setSenha(senhaHash);
            log.info("Senha hasheada para usuário: {} - Hash: {}", usuario.getLogin(), senhaHash);
        }
        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso: {} (ID: {})", salvo.getLogin(), salvo.getId());
        return salvo;
    }

    @Transactional
    public void alterarSenha(Usuario usuario, String novaSenha) {
        log.info("Alterando senha do usuário: {}", usuario.getLogin());
        String novoHash = passwordEncoder.encode(novaSenha);
        usuario.setSenha(novoHash);
        usuarioRepository.save(usuario);
        log.info("Senha alterada com sucesso para usuário: {} - Novo hash: {}", usuario.getLogin(), novoHash);
    }

    public static class AuthenticationResult {
        private final boolean sucesso;
        private final String mensagem;
        private final Usuario usuario;

        private AuthenticationResult(boolean sucesso, String mensagem, Usuario usuario) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.usuario = usuario;
        }

        public static AuthenticationResult success(Usuario usuario) {
            return new AuthenticationResult(true, "Autenticação realizada com sucesso", usuario);
        }

        public static AuthenticationResult failure(String mensagem) {
            return new AuthenticationResult(false, mensagem, null);
        }

        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public Optional<Usuario> getUsuario() { return Optional.ofNullable(usuario); }
    }
}