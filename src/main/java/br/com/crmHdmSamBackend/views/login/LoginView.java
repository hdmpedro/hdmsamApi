package br.com.crmHdmSamBackend.views.login;


import br.com.crmHdmSamBackend.security.service.AutenticacaoService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.H2;

@Route("login")
//@Route("")
@PageTitle("Login - HDM SAM")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final AutenticacaoService autenticacaoService;
    private final TextField loginField;
    private final PasswordField passwordField;
    private final Button loginButton;

    @Autowired
    public LoginView(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");

        VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setWidth("400px");
        loginPanel.setPadding(true);
        loginPanel.setSpacing(true);
        loginPanel.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)");

        H1 title = new H1("HDM SAM");
        title.getStyle().set("margin", "0").set("color", "#667eea");

        H2 subtitle = new H2("Login");
        subtitle.getStyle().set("margin", "0 0 20px 0").set("font-size", "1.2em").set("color", "#666");

        loginField = new TextField("Login");
        loginField.setWidth("100%");
        loginField.setRequired(true);
        loginField.setAutofocus(true);
        loginField.setClearButtonVisible(true);

        passwordField = new PasswordField("Senha");
        passwordField.setWidth("100%");
        passwordField.setRequired(true);
        passwordField.setClearButtonVisible(true);

        loginButton = new Button("Entrar");
        loginButton.setWidth("100%");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.addClickListener(e -> realizarLogin());

        loginPanel.add(title, subtitle, loginField, passwordField, loginButton);
        loginPanel.setAlignItems(Alignment.CENTER);

        add(loginPanel);
    }

    private void realizarLogin() {
        String login = loginField.getValue();
        String senha = passwordField.getValue();

        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            showNotification("Por favor, preencha login e senha", NotificationVariant.LUMO_ERROR);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Entrando...");

        AutenticacaoService.AuthenticationResult resultado =
                autenticacaoService.autenticar(login, senha);
        System.err.println("mensagem:"+resultado.getMensagem());
        System.err.println("usuario"+resultado.getUsuario());
        System.err.println("isSucesso"+resultado.isSucesso());


        if (resultado.isSucesso()) {
            showNotification("Login realizado com sucesso!", NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate("dashboard"));
        } else {
            passwordField.clear();
            loginButton.setEnabled(true);
            loginButton.setText("Entrar");
            showNotification(resultado.getMensagem(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }
}