package br.com.crmHdmSamBackend.security;


import br.com.crmHdmSamBackend.model.Usuario;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
public class SessionManager implements Serializable {

    private static final String USUARIO_SESSION_KEY = "USUARIO_AUTENTICADO";

    public void setUsuarioAutenticado(Usuario usuario) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(USUARIO_SESSION_KEY, usuario);
        }
    }

    public Optional<Usuario> getUsuarioAutenticado() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute(USUARIO_SESSION_KEY);
            return Optional.ofNullable(usuario);
        }
        return Optional.empty();
    }

    public boolean isUsuarioAutenticado() {
        return getUsuarioAutenticado().isPresent();
    }

    public boolean isUsuarioAdmin() {
        return getUsuarioAutenticado()
                .map(Usuario::isAdmin)
                .orElse(false);
    }

    public void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(USUARIO_SESSION_KEY, null);
            session.close();
        }
    }
}