package br.com.crmHdmSamBackend.security.service;


import br.com.crmHdmSamBackend.exception.*;
import br.com.crmHdmSamBackend.model.TokenRenovacao;
import br.com.crmHdmSamBackend.model.TokenBlacklist;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.model.dto.AuthResponse;
import br.com.crmHdmSamBackend.repository.TokenRenovacaoRepository;
import br.com.crmHdmSamBackend.repository.TokenBlacklistRepository;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.security.config.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRenovacaoRepository tokenRenovacaoRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Autowired
    public AuthService(
            UsuarioRepository usuarioRepository,
            TokenRenovacaoRepository tokenRenovacaoRepository,
            TokenBlacklistRepository tokenBlacklistRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtConfig jwtConfig) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRenovacaoRepository = tokenRenovacaoRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
    }

    public AuthResponse login(String login, String senha) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais inválidas"));

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException("Usuário inativo");
        }

        if (usuario.isBloqueado()) {
            throw new UsuarioBloqueadoException(
                    "Usuário bloqueado até " + usuario.getBloqueadoAte()
            );
        }

        if (usuario.getSenha() == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            usuario.incrementarTentativasLogin();
            usuarioRepository.save(usuario);
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }

        usuario.resetarTentativasLogin();
        usuario.atualizarUltimoLogin();
        usuarioRepository.save(usuario);

        String accessToken = jwtService.gerarAccessToken(usuario);
        String refreshToken = criarRefreshToken(usuario);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtConfig.getAccessTokenExpiration() / 1000
        );
    }

    public AuthResponse refresh(String refreshToken) {
        TokenRenovacao token = tokenRenovacaoRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido"));

        if (token.isRevogado()) {
            throw new InvalidRefreshTokenException("Refresh token revogado");
        }

        if (token.isExpirado()) {
            throw new InvalidRefreshTokenException("Refresh token expirado");
        }

        Usuario usuario = token.getUsuario();

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException("Usuário inativo");
        }

        String novoAccessToken = jwtService.gerarAccessToken(usuario);

        return new AuthResponse(
                novoAccessToken,
                refreshToken,
                "Bearer",
                jwtConfig.getAccessTokenExpiration() / 1000
        );
    }

    public void logout(String accessToken) {
        try {
            String jti = jwtService.extrairJti(accessToken);
            OffsetDateTime expiracao = jwtService.extrairExpiracao(accessToken);

            TokenBlacklist blacklist = new TokenBlacklist(jti, expiracao);
            tokenBlacklistRepository.save(blacklist);
        } catch (Exception e) {
            throw new InvalidJwtException("Token inválido para logout");
        }
    }

    public void revogarTodosTokensUsuario(UUID usuarioId) {
        tokenRenovacaoRepository.revogarTodosDoUsuario(usuarioId);
    }

    public boolean isTokenNaBlacklist(String jti) {
        return tokenBlacklistRepository.existsByTokenJti(jti);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void limparTokensExpirados() {
        OffsetDateTime agora = OffsetDateTime.now();
        tokenRenovacaoRepository.limparExpiradosERevogados(agora);
        tokenBlacklistRepository.limparExpirados(agora);
    }

    private String criarRefreshToken(Usuario usuario) {
        String tokenValue = jwtService.gerarRefreshToken();
        OffsetDateTime expiracao = OffsetDateTime.now()
                .plusSeconds(jwtConfig.getRefreshTokenExpiration() / 1000);

        TokenRenovacao tokenRenovacao = new TokenRenovacao(tokenValue, usuario, expiracao);
        tokenRenovacaoRepository.save(tokenRenovacao);

        return tokenValue;
    }
}