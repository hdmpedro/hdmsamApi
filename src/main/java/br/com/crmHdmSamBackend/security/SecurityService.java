package br.com.crmHdmSamBackend.security;

import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

import br.com.crmHdmSamBackend.service.AuthenticationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.stereotype.Component;
@Component
public class SecurityService {

    private final AuthenticationService authenticationService;

    public SecurityService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void logout() {
        authenticationService.logout();
        UI.getCurrent().getPage().setLocation("/");
        VaadinServletRequest.getCurrent().getHttpServletRequest().getSession().invalidate();
    }

    public boolean isAuthenticated() {
        return authenticationService.isUsuarioAutenticado();
    }
}