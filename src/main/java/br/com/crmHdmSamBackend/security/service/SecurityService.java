package br.com.crmHdmSamBackend.security.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.stereotype.Component;
@Component
public class SecurityService {

    private final AutenticacaoService autenticacaoService;

    public SecurityService(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    public void logout() {
        autenticacaoService.logout();
        UI.getCurrent().getPage().setLocation("/");
        VaadinServletRequest.getCurrent().getHttpServletRequest().getSession().invalidate();
    }

    public boolean isAuthenticated() {
        return autenticacaoService.isUsuarioAutenticado();
    }
}