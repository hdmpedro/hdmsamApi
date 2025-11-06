package br.com.crmHdmSamBackend.security.config;


import br.com.crmHdmSamBackend.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/line-awesome/**"),
                        new AntPathRequestMatcher("/themes/**"),
                        new AntPathRequestMatcher("/icons/**"),
                        new AntPathRequestMatcher("/VAADIN/**")
                ).permitAll()
        );

        super.configure(http);
       // setLoginView(http, "/login", "/");
        setLoginView(http, LoginView.class);

    }
}