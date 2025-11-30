package br.com.crmHdmSamBackend.security.service;


import br.com.crmHdmSamBackend.exception.*;
import br.com.crmHdmSamBackend.model.LoginAttempt;
import br.com.crmHdmSamBackend.model.TokenRenovacao;
import br.com.crmHdmSamBackend.model.TokenBlacklist;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.model.dto.api.AuthResponse;
import br.com.crmHdmSamBackend.model.dto.api.TokenInfoDTO;
import br.com.crmHdmSamBackend.repository.LoginAttemptRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApiAuthService {

    private static final int MAX_REFRESH_TOKENS_POR_USUARIO = 5;
    private static final int MAX_TENTATIVAS_LOGIN_POR_HORA = 10;
    private static final int MAX_LOGINS_SUCESSO_POR_HORA = 3;

    private final UsuarioRepository usuarioRepository;
    private final TokenRenovacaoRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Autowired
    public ApiAuthService(
            UsuarioRepository usuarioRepository,
            TokenRenovacaoRepository refreshTokenRepository,
            TokenBlacklistRepository tokenBlacklistRepository,
            LoginAttemptRepository loginAttemptRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtConfig jwtConfig) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
    }

    public AuthResponse login(String login, String senha, String ipAddress) {
        validarTentativasLogin(login, ipAddress);
        validarLoginsSucessoPorHora(login, ipAddress);

        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> {
                    registrarTentativa(login, ipAddress, false);
                    return new CredenciaisInvalidasException("Credenciais inválidas");
                });

        if (!usuario.isAtivo()) {
            registrarTentativa(login, ipAddress, false);
            throw new UsuarioInativoException("Usuário inativo");
        }

        if (usuario.isBloqueado()) {
            registrarTentativa(login, ipAddress, false);
            throw new UsuarioBloqueadoException(
                    "Usuário bloqueado até " + usuario.getBloqueadoAte()
            );
        }

        if (usuario.getSenha() == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            usuario.incrementarTentativasLogin();
            usuarioRepository.save(usuario);
            registrarTentativa(login, ipAddress, false);
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }

        usuario.resetarTentativasLogin();
        usuario.atualizarUltimoLogin();
        usuarioRepository.save(usuario);

        registrarTentativa(login, ipAddress, true);
        limparTokensExcedentes(usuario.getId());

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
        TokenRenovacao token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido"));

        if (token.isRevogado()) {
            throw new InvalidRefreshTokenException("Refresh token revogado");
        }

        if (token.isExpirado()) {
            refreshTokenRepository.delete(token);
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
            String usuarioId = jwtService.extrairUsuarioId(accessToken);

            TokenBlacklist blacklist = new TokenBlacklist(jti, expiracao);
            tokenBlacklistRepository.save(blacklist);

            Usuario usuario = usuarioRepository.findById(UUID.fromString(usuarioId)).orElse(null);
            if (usuario != null) {
                revogarRefreshTokensAtivos(usuario.getId());
            }
        } catch (Exception e) {
            throw new InvalidJwtException("Token inválido para logout");
        }
    }

    public void revogarTodosTokensUsuario(UUID usuarioId) {
        refreshTokenRepository.revogarTodosDoUsuario(usuarioId);

        List<TokenRenovacao> tokens = refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuarioId);
        for (TokenRenovacao token : tokens) {
            token.setRevogado(true);
        }
        refreshTokenRepository.saveAll(tokens);
    }

    public boolean isTokenNaBlacklist(String jti) {
        return tokenBlacklistRepository.existsByTokenJti(jti);
    }

    public List<TokenInfoDTO> listarTokensAtivos(UUID usuarioId) {
        List<TokenRenovacao> tokens = refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuarioId);
        return tokens.stream()
                .filter(t -> !t.isExpirado())
                .map(t -> new TokenInfoDTO(
                        t.getId(),
                        t.getCriadoEm(),
                        t.getExpiraEm(),
                        t.isRevogado()
                ))
                .collect(Collectors.toList());
    }

    public void revogarTokenEspecifico(UUID usuarioId, UUID tokenId) {
        TokenRenovacao token = refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new InvalidRefreshTokenException("Token não encontrado"));

        if (!token.getUsuario().getId().equals(usuarioId)) {
            throw new InvalidRefreshTokenException("Token não pertence ao usuário");
        }

        token.setRevogado(true);
        refreshTokenRepository.save(token);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void limparTokensExpirados() {
        OffsetDateTime agora = OffsetDateTime.now();
        refreshTokenRepository.limparExpiradosERevogados(agora);
        tokenBlacklistRepository.limparExpirados(agora);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void limparTentativasLoginAntigas() {
        OffsetDateTime umaHoraAtras = OffsetDateTime.now().minusHours(1);
        loginAttemptRepository.deleteByTentativaEmBefore(umaHoraAtras);
    }

    private void limparTokensExcedentes(UUID usuarioId) {
        List<TokenRenovacao> tokensAtivos = refreshTokenRepository
                .findByUsuarioIdAndRevogadoFalseOrderByCriadoEmDesc(usuarioId);

        if (tokensAtivos.size() >= MAX_REFRESH_TOKENS_POR_USUARIO) {
            List<TokenRenovacao> tokensParaRevogar = tokensAtivos.subList(
                    MAX_REFRESH_TOKENS_POR_USUARIO - 1,
                    tokensAtivos.size()
            );

            for (TokenRenovacao token : tokensParaRevogar) {
                token.setRevogado(true);
            }
            refreshTokenRepository.saveAll(tokensParaRevogar);
        }
    }

    private void revogarRefreshTokensAtivos(UUID usuarioId) {
        List<TokenRenovacao> tokens = refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuarioId);
        for (TokenRenovacao token : tokens) {
            token.setRevogado(true);
        }
        if (!tokens.isEmpty()) {
            refreshTokenRepository.saveAll(tokens);
        }
    }

    private String criarRefreshToken(Usuario usuario) {
        String tokenValue = jwtService.gerarRefreshToken();
        OffsetDateTime expiracao = OffsetDateTime.now()
                .plusSeconds(jwtConfig.getRefreshTokenExpiration() / 1000);

        TokenRenovacao refreshToken = new TokenRenovacao(tokenValue, usuario, expiracao);
        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    private void validarTentativasLogin(String login, String ipAddress) {
        OffsetDateTime umaHoraAtras = OffsetDateTime.now().minusHours(1);

        long tentativasFalhasPorLogin = loginAttemptRepository
                .countByLoginAndTentativaEmAfterAndSucesso(login, umaHoraAtras, false);

        long tentativasFalhasPorIp = loginAttemptRepository
                .countByIpAddressAndTentativaEmAfterAndSucesso(ipAddress, umaHoraAtras, false);

        if (tentativasFalhasPorLogin >= MAX_TENTATIVAS_LOGIN_POR_HORA) {
            throw new TentativasExcedidasException(
                    "Limite de tentativas excedido para este login. Tente novamente em 1 hora."
            );
        }

        if (tentativasFalhasPorIp >= MAX_TENTATIVAS_LOGIN_POR_HORA * 2) {
            throw new TentativasExcedidasException(
                    "Limite de tentativas excedido para este IP. Tente novamente em 1 hora."
            );
        }
    }

    private void validarLoginsSucessoPorHora(String login, String ipAddress) {
        OffsetDateTime umaHoraAtras = OffsetDateTime.now().minusHours(1);

        long loginsSucessoPorLogin = loginAttemptRepository
                .countByLoginAndTentativaEmAfterAndSucesso(login, umaHoraAtras, true);

        long loginsSucessoPorIp = loginAttemptRepository
                .countByIpAddressAndTentativaEmAfterAndSucesso(ipAddress, umaHoraAtras, true);

        if (loginsSucessoPorLogin >= MAX_LOGINS_SUCESSO_POR_HORA) {
            throw new TentativasExcedidasException(
                    "Limite de logins bem-sucedidos excedido. Aguarde 1 hora ou use o refresh token para renovar seu acesso."
            );
        }

        if (loginsSucessoPorIp >= MAX_LOGINS_SUCESSO_POR_HORA * 3) {
            throw new TentativasExcedidasException(
                    "Limite de logins bem-sucedidos excedido para este IP. Aguarde 1 hora."
            );
        }
    }

    private void registrarTentativa(String login, String ipAddress, boolean sucesso) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setLogin(login);
        attempt.setIpAddress(ipAddress);
        attempt.setSucesso(sucesso);
        attempt.setTentativaEm(OffsetDateTime.now());
        loginAttemptRepository.save(attempt);
    }
}