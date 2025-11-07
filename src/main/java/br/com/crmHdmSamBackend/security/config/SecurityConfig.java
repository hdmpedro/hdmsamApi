package br.com.crmHdmSamBackend.security.config;


import br.com.crmHdmSamBackend.security.JwtAuthenticationFilter;
import br.com.crmHdmSamBackend.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
//estaticos
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/VAADIN/**"),
                                new AntPathRequestMatcher("/vaadinServlet/**"),
                                new AntPathRequestMatcher("/frontend/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/line-awesome/**"),
                                new AntPathRequestMatcher("/themes/**"),
                                new AntPathRequestMatcher("/icons/**"),
                                new AntPathRequestMatcher("/sw.js"),
                                new AntPathRequestMatcher("/offline.html"),
                                new AntPathRequestMatcher("/manifest.webmanifest")
                        ).permitAll()
//recursos publicos
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/api/public/**")
                        ).permitAll()
//admin
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
//autenticadass
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
//anyRequest
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String requestUri = request.getRequestURI();
                            if (requestUri.startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\":\"Não autenticado\",\"message\":\"" + authException.getMessage() + "\"}");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String requestUri = request.getRequestURI();
                            if (requestUri.startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\":\"Acesso negado\",\"message\":\"" + accessDeniedException.getMessage() + "\"}");
                            } else {
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
                            }
                        })
                );

        setLoginView(http, LoginView.class);
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}