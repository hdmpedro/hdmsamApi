package br.com.crmHdmSamBackend.security;


import br.com.crmHdmSamBackend.exception.RateLimitExceededException;
import br.com.crmHdmSamBackend.security.service.AutenticacaoService;
import br.com.crmHdmSamBackend.security.service.JwtService;
import br.com.crmHdmSamBackend.security.service.RateLimitService;
import br.com.crmHdmSamBackend.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final JwtService jwtService;

    private static final Map<String, RateLimitConfig> ENDPOINT_LIMITS = new HashMap<>();

    static {
        ENDPOINT_LIMITS.put("/api/auth/login", new RateLimitConfig(3, 60));
        ENDPOINT_LIMITS.put("/api/auth/renovar", new RateLimitConfig(10, 15));
        ENDPOINT_LIMITS.put("/api/auth/logout", new RateLimitConfig(5, 10));
        ENDPOINT_LIMITS.put("/api/auth/revogar-todos", new RateLimitConfig(3, 60));
        ENDPOINT_LIMITS.put("/api/auth/tokens", new RateLimitConfig(20, 10));
    }

    @Autowired
    public RateLimitInterceptor(RateLimitService rateLimitService, JwtService jwtService) {
        this.rateLimitService = rateLimitService;
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        if (!"POST".equals(method) && !"GET".equals(method) && !"DELETE".equals(method)) {
            return true;
        }

        RateLimitConfig config = ENDPOINT_LIMITS.get(requestUri);
        if (config == null) {
            config = new RateLimitConfig(100, 10);
        }

        String identificador = obterIdentificador(request);

        try {
            rateLimitService.validarRateLimit(identificador, requestUri, config.limite, config.janelaMinutos);
            return true;
        } catch (RateLimitExceededException e) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"error\":\"Rate limit excedido\",\"message\":\"" + e.getMessage() + "\"}"
            );
            return false;
        }
    }

    private String obterIdentificador(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String usuarioId = jwtService.extrairUsuarioId(token);
                return "user:" + usuarioId;
            } catch (Exception e) {
            }
        }

        String ipAddress = obterIpAddress(request);
        return "ip:" + ipAddress;
    }

    private String obterIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private static class RateLimitConfig {
        int limite;
        long janelaMinutos;

        RateLimitConfig(int limite, long janelaMinutos) {
            this.limite = limite;
            this.janelaMinutos = janelaMinutos;
        }
    }
}