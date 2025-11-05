package br.com.crmHdmSamBackend.views;

import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.service.AuthenticationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("home")
@PageTitle("Home")
@RolesAllowed("USER")

public class HomeView extends VerticalLayout {

    private final AuthenticationService authenticationService;

    @Autowired
    public HomeView(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        Usuario usuario = authenticationService.getUsuarioAutenticado().orElse(null);

        if (usuario != null) {
            H1 welcome = new H1("Bem-vindo, " + usuario.getNome());
            Paragraph info = new Paragraph("Email: " + usuario.getEmail());
            Paragraph role = new Paragraph("Tipo: " + (usuario.isAdmin() ? "Administrador" : "Usuário"));

            Button logoutButton = new Button("Sair", e -> {
                authenticationService.logout();
                getUI().ifPresent(ui -> ui.navigate(""));
            });

            add(welcome, info, role, logoutButton);
        }
    }
}