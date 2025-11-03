package br.com.crmHdmSamBackend.views.form;

import br.com.crmHdmSamBackend.model.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.Locale;

public class ClienteFormView extends FormLayout {

    private Usuario cliente;

    // Campos do formulário
    TextField nome = new TextField("Nome");
    EmailField email = new EmailField("Email");
    TextField telefone = new TextField("Telefone");
    DatePicker dataNascimento = new DatePicker("Data de Nascimento");
    TextArea observacoes = new TextArea("Observações");
    Checkbox ativo = new Checkbox("Ativo");

    // Botões
    Button save = new Button("Salvar");
    Button delete = new Button("Excluir");
    Button close = new Button("Cancelar");

    // Binder para validação
    Binder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);

    public ClienteFormView() {
        addClassName("cliente-form");

        // Configurar Binder
        binder.bindInstanceFields(this);

        // Configurar campos
        configurarCampos();

        // Layout
        add(
                nome,
                email,
                telefone,
                dataNascimento,
                observacoes,
                ativo,
                createButtonsLayout()
        );
    }

    private void configurarCampos() {
        // Nome
        nome.setRequiredIndicatorVisible(true);
        nome.setPlaceholder("Digite o nome completo");

        // Email
        email.setRequiredIndicatorVisible(true);
        email.setPlaceholder("exemplo@email.com");
        email.setErrorMessage("Digite um email válido");

        // Telefone
        telefone.setPlaceholder("(00) 00000-0000");
        telefone.setPattern("\\([0-9]{2}\\) [0-9]{4,5}-[0-9]{4}");
        telefone.setHelperText("Formato: (00) 00000-0000");

        // Data de Nascimento
        dataNascimento.setRequiredIndicatorVisible(true);
        dataNascimento.setLocale(new Locale("pt", "BR"));
        dataNascimento.setPlaceholder("dd/MM/yyyy");

        // Observações
        observacoes.setPlaceholder("Informações adicionais...");
        observacoes.setMaxLength(500);
        observacoes.setHelperText("Máximo 500 caracteres");

        // Ativo
        ativo.setValue(true);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, cliente)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
        binder.readBean(cliente);

        // Esconder botão delete para novos registros
        delete.setVisible(cliente != null && cliente.getId() != null);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(cliente);
            fireEvent(new SaveEvent(this, cliente));
        } catch (ValidationException e) {
            showError("Verifique os campos obrigatórios");
        }
    }

    public void showError(String message) {
        Notification notification = Notification.show(message, 3000,
                Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    // Eventos customizados
    public static abstract class ClienteFormEvent extends ComponentEvent<ClienteFormView> {
        private Usuario cliente;

        protected ClienteFormEvent(ClienteFormView source, Usuario cliente) {
            super(source, false);
            this.cliente = cliente;
        }

        public Usuario getCliente() {
            return cliente;
        }
    }

    public static class SaveEvent extends ClienteFormEvent {
        SaveEvent(ClienteFormView source, Usuario cliente) {
            super(source, cliente);
        }
    }

    public static class DeleteEvent extends ClienteFormEvent {
        DeleteEvent(ClienteFormView source, Usuario cliente) {
            super(source, cliente);
        }
    }

    public static class CloseEvent extends ClienteFormEvent {
        CloseEvent(ClienteFormView source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
