package br.com.crmHdmSamBackend.security.service;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.security.GerenciadorSessoes;
import br.com.crmHdmSamBackend.util.IpUtils;
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


@Service
public class AutenticacaoService {

    private static final Logger log = LoggerFactory.getLogger(AutenticacaoService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GerenciadorSessoes gerenciadorSessoes;
    private final TentativaLoginService tentativaLoginService;

    @Autowired
    public AutenticacaoService(UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder,
                               GerenciadorSessoes gerenciadorSessoes,
                               TentativaLoginService tentativaLoginService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.gerenciadorSessoes = gerenciadorSessoes;
        this.tentativaLoginService = tentativaLoginService;
        log.info("AuthenticationService inicializado com PasswordEncoder: {}", passwordEncoder.getClass().getName());
    }

    @Transactional
    public AuthenticationResult autenticar(String login, String senha) {
        log.info("=== INICIANDO AUTENTICAÇÃO ===");

        String ipCliente = IpUtils.obterIpCliente();
        log.info("IP do cliente: {}", ipCliente);
        log.info("Login recebido: '{}'", login);
        log.info("Senha recebida (length): {}", senha != null ? senha.length() : 0);

        if (tentativaLoginService.isIpBloqueado(ipCliente)) {
            log.warn("Tentativa de autenticação de IP BLOQUEADO: {}", ipCliente);
            return AuthenticationResult.failure("Muitas tentativas de login. Aguarde alguns minutos.");
        }

        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            log.warn("Login ou senha vazios - IP: {}", ipCliente);
            tentativaLoginService.registrarTentativaFalha(ipCliente);
            return AuthenticationResult.failure("Login e senha são obrigatórios");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login.trim());
        if (usuarioOpt.isEmpty()) {
            log.warn("Usuário não encontrado: '{}' - IP: {}", login, ipCliente);
            tentativaLoginService.registrarTentativaFalha(ipCliente);
            return AuthenticationResult.failure("Credenciais inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        log.info("Usuário encontrado: {} (ID: {})", usuario.getNome(), usuario.getId());
        log.info("Ativo: {} | Admin: {} | Tentativas usuário: {}", usuario.isAtivo(), usuario.isAdmin(), usuario.getTentativasLogin());

        if (!usuario.isAtivo()) {
            log.warn("Usuário inativo: {} - IP: {}", login, ipCliente);
            tentativaLoginService.registrarTentativaFalha(ipCliente);
            return AuthenticationResult.failure("Usuário inativo");
        }

        if (usuario.isBloqueado()) {
            log.warn("Usuário bloqueado: {} até {} - IP: {}", login, usuario.getBloqueadoAte(), ipCliente);
            tentativaLoginService.registrarTentativaFalha(ipCliente);
            return AuthenticationResult.failure("Usuário bloqueado..");
        }

        if (usuario.getSenha() == null) {
            log.error("Usuário sem senha definida: {} - IP: {}", login, ipCliente);
            tentativaLoginService.registrarTentativaFalha(ipCliente);
            return AuthenticationResult.failure("Credenciais inválidas");
        }

        log.info("Hash da senha no banco: {}", usuario.getSenha());
        log.info("PasswordEncoder usado: {}", passwordEncoder.getClass().getName());

        boolean senhaCorreta = passwordEncoder.matches(senha, usuario.getSenha());
        log.info("Resultado do matches: {}", senhaCorreta);

        if (!senhaCorreta) {
            usuario.incrementarTentativasLogin();
            usuarioRepository.save(usuario);
            tentativaLoginService.registrarTentativaFalha(ipCliente);

            log.warn("Senha incorreta para usuário: {} - Tentativas usuário: {}/{} - IP: {}",
                    login, usuario.getTentativasLogin(), 5, ipCliente);

            if (usuario.isBloqueado()) {
                log.warn("Usuário bloqueado após {} tentativas: {} - IP: {}",
                        usuario.getTentativasLogin(), login, ipCliente);
                return AuthenticationResult.failure("Muitas tentativas incorretas. Usuário bloqueado por 15 minutos.");
            }

            return AuthenticationResult.failure("Credenciais inválidas");
        }

        log.info("✓ Senha correta! Autenticando usuário: {} - IP: {}", login, ipCliente);

        usuario.resetarTentativasLogin();
        usuario.atualizarUltimoLogin();
        usuarioRepository.save(usuario);
        log.info("✓ Tentativas resetadas e último login atualizado");

        tentativaLoginService.registrarTentativaSucesso(ipCliente);
        log.info("✓ Tentativas de IP resetadas para: {}", ipCliente);

        gerenciadorSessoes.setUsuarioAutenticado(usuario);
        log.info("✓ Sessão criada para usuário: {}", login);

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
        Optional<Usuario> usuario = gerenciadorSessoes.getUsuarioAutenticado();
        usuario.ifPresent(u -> log.info("Logout do usuário: {}", u.getLogin()));
        gerenciadorSessoes.logout();
    }

    public Optional<Usuario> getUsuarioAutenticado() {
        return gerenciadorSessoes.getUsuarioAutenticado();
    }

    public boolean isUsuarioAutenticado() {
        return gerenciadorSessoes.isUsuarioAutenticado();
    }

    public boolean isUsuarioAdmin() {
        return gerenciadorSessoes.isUsuarioAdmin();
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