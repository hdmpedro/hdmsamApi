package br.com.crmHdmSamBackend.security;

import br.com.crmHdmSamBackend.security.service.TentativaLoginService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Order(1)
public class RequestThrottleFiltro implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestThrottleFiltro.class);

    private final TentativaLoginService tentativaLoginService;

    public RequestThrottleFiltro(TentativaLoginService tentativaLoginService) {
        this.tentativaLoginService = tentativaLoginService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = obterIpReal(httpRequest);
        String uri = httpRequest.getRequestURI();

        if (isRotaProtegida(uri) && tentativaLoginService.isIpBloqueado(ip)) {
            LocalDateTime tempoDesbloqueio = tentativaLoginService.getTempoDesbloqueio(ip);
            String mensagem = String.format(
                    "Muitas tentativas de login. IP bloqueado até %s",
                    tempoDesbloqueio != null ? tempoDesbloqueio.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "indeterminado"
            );

            log.warn("Requisição BLOQUEADA por throttle - IP: {} - URI: {}", ip, uri);
// codigo too many requests 429
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write(String.format(
                    "{\"error\": \"Too Many Requests\", \"message\": \"%s\"}",
                    mensagem
            ));
            return;
        }

        chain.doFilter(request, response);
    }

    private String obterIpReal(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int idx = ip.indexOf(',');
            if (idx > 0) {
                ip = ip.substring(0, idx);
            }
            return ip.trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        return request.getRemoteAddr();
    }

    private boolean isRotaProtegida(String uri) {
        return uri != null && (
                uri.contains("login") ||
                        uri.contains("authenticate") ||
                        uri.contains("auth")
        );
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestThrottleFilter inicializado - Proteção contra ataques de força bruta ativa");
    }

    @Override
    public void destroy() {
        log.info("RequestThrottleFilter destruído");
    }
}