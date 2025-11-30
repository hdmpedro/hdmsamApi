//package br.com.crmHdmSamBackend.security.service;
//import br.com.crmHdmSamBackend.exception.RateLimitExceededException;
//import br.com.crmHdmSamBackend.model.Usuario;
//import br.com.crmHdmSamBackend.repository.UsuarioRepository;
//import br.com.crmHdmSamBackend.security.service.ApiAuthService;
//import br.com.crmHdmSamBackend.security.service.JwtService;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.*;
//
//@Component
//public class RateLimitFilter extends OncePerRequestFilter {
//
//    private final RateLimitService rateLimitService;
//    private final JwtService jwtService;
//
//    private static final List<String> ENDPOINTS_PUBLICOS = Arrays.asList(
//            "/api/auth/login",
//            "/api/auth/refresh",
//            "/api/public"
//    );
//
//    private static final Map<String, RateLimitConfig> ENDPOINT_LIMITS = new HashMap<>();
//
//    static {
//        ENDPOINT_LIMITS.put("/api/auth/login", new RateLimitConfig(3, 60));
//        ENDPOINT_LIMITS.put("/api/auth/refresh", new RateLimitConfig(10, 15));
//        ENDPOINT_LIMITS.put("/api/auth/logout", new RateLimitConfig(5, 10));
//        ENDPOINT_LIMITS.put("/api/auth/revoke-all", new RateLimitConfig(3, 60));
//        ENDPOINT_LIMITS.put("/api/auth/tokens", new RateLimitConfig(20, 10));
//    }
//
//    @Autowired
//    public RateLimitFilter(RateLimitService rateLimitService, JwtService jwtService) {
//        this.rateLimitService = rateLimitService;
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        String method = request.getMethod();
//
//        // Ignora métodos não relevantes
//        if (!"POST".equals(method) && !"GET".equals(method) && !"DELETE".equals(method)) {
//            return true;
//        }
//
//        // Ignora endpoints públicos
//        return ENDPOINTS_PUBLICOS.stream().anyMatch(path::startsWith);
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestUri = request.getRequestURI();
//        RateLimitConfig config = ENDPOINT_LIMITS.get(requestUri);
//        if (config == null) {
//            config = new RateLimitConfig(100, 10);
//        }
//
//        String identificador = obterIdentificador(request);
//
//        try {
//            rateLimitService.validarRateLimit(identificador, requestUri, config.limite, config.janelaMinutos);
//            filterChain.doFilter(request, response);
//        } catch (RateLimitExceededException e) {
//            response.setStatus(429);
//            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().write(
//                    "{\"error\":\"Rate limit excedido\",\"message\":\"" + e.getMessage() + "\"}"
//            );
//        }
//    }
//
//    private String obterIdentificador(HttpServletRequest request) {
//        // Usa o contexto de segurança para identificar o usuário autenticado
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated() &&
//                !(authentication instanceof AnonymousAuthenticationToken)) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof Usuario) {
//                return "user:" + ((Usuario) principal).getId();
//            } else if (principal instanceof String) {
//                return "user:" + principal;
//            }
//        }
//
//        // Fallback para IP se não estiver autenticado
//        String ipAddress = obterIpAddress(request);
//        return "ip:" + ipAddress;
//    }
//
//    private String obterIpAddress(HttpServletRequest request) {
//        String xForwardedFor = request.getHeader("X-Forwarded-For");
//        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
//            return xForwardedFor.split(",")[0].trim();
//        }
//        return request.getRemoteAddr();
//    }
//
//    private static class RateLimitConfig {
//        int limite;
//        long janelaMinutos;
//
//        RateLimitConfig(int limite, long janelaMinutos) {
//            this.limite = limite;
//            this.janelaMinutos = janelaMinutos;
//        }
//    }
//}