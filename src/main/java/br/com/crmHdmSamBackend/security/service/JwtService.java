package br.com.crmHdmSamBackend.security.service;

import br.com.crmHdmSamBackend.exception.InvalidJwtException;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.security.GerenciadorSessoes;
import br.com.crmHdmSamBackend.security.config.JwtConfig;
import br.com.crmHdmSamBackend.util.IpUtils;
import com.vaadin.flow.server.VaadinSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;


@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Autowired
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String gerarAccessToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + jwtConfig.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("login", usuario.getLogin())
                .claim("email", usuario.getEmail())
                .claim("role", usuario.getRole().name())
                .claim("tipo", "ACCESS")
                .issuer(jwtConfig.getIssuer())
                .issuedAt(agora)
                .expiration(expiracao)
                .id(UUID.randomUUID().toString())
                .signWith(secretKey)
                .compact();
    }

    public String gerarRefreshToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    public Claims extrairClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException("Token JWT inválido: " + e.getMessage());
        }
    }

    public String extrairUsuarioId(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairJti(String token) {
        return extrairClaims(token).getId();
    }

    public boolean validarToken(String token, UUID usuarioId) {
        try {
            Claims claims = extrairClaims(token);
            String tokenUsuarioId = claims.getSubject();
            Date expiracao = claims.getExpiration();
            String tipo = claims.get("tipo", String.class);

            return tokenUsuarioId.equals(usuarioId.toString())
                    && expiracao.after(new Date())
                    && "ACCESS".equals(tipo);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpirado(String token) {
        try {
            Date expiracao = extrairClaims(token).getExpiration();
            return expiracao.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public OffsetDateTime extrairExpiracao(String token) {
        Date expiracao = extrairClaims(token).getExpiration();
        return OffsetDateTime.ofInstant(expiracao.toInstant(), ZoneOffset.UTC);
    }
}