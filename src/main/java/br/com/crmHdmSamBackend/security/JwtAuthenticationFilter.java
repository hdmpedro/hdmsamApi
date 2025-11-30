package br.com.crmHdmSamBackend.security;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.security.service.ApiAuthService;
import br.com.crmHdmSamBackend.security.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApiAuthService authService;
    private final UsuarioRepository usuarioRepository;

    private static final List<String> ENDPOINTS_PUBLICOS = Arrays.asList(
            "/login",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/public"
    );

    @Autowired
    public JwtAuthenticationFilter(
            JwtService jwtService,
            ApiAuthService authService,
            UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.startsWith("/VAADIN/") ||
                path.startsWith("/vaadinServlet/") ||
                path.contains("v-r=uidl") ||
                path.contains("v-r=init")) {
            return true;
        }

        return ENDPOINTS_PUBLICOS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String jti = jwtService.extrairJti(token);

            if (authService.isTokenNaBlacklist(jti)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token revogado\"}");
                return;
            }

            String usuarioId = jwtService.extrairUsuarioId(token);
            Usuario usuario = usuarioRepository.findById(UUID.fromString(usuarioId))
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            if (!usuario.isAtivo()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Usuário inativo\"}");
                return;
            }

            if (!jwtService.validarToken(token, usuario.getId())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido\"}");
                return;
            }

            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(usuario, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Falha na autenticação: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}