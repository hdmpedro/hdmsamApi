package br.com.crmHdmSamBackend.views.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import br.com.crmHdmSamBackend.model.Usuario;

public class UsuarioForm extends FormLayout {

    private Usuario usuario;

    TextField nome = new TextField("Nome");
    EmailField email = new EmailField("Email");
    TextField telefone = new TextField("Telefone");

    Button save = new Button("Salvar");
    Button delete = new Button("Deletar");
    Button close = new Button("Cancelar");

    Binder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);

    public UsuarioForm() {
        addClassName("usuario-form");
        binder.bindInstanceFields(this);

        add(nome, email, telefone, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, usuario)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(usuario);
            fireEvent(new SaveEvent(this, usuario));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        binder.readBean(usuario);
    }

    public static abstract class UsuarioFormEvent extends ComponentEvent<UsuarioForm> {
        private Usuario usuario;

        protected UsuarioFormEvent(UsuarioForm source, Usuario usuario) {
            super(source, false);
            this.usuario = usuario;
        }

        public Usuario getUsuario() {
            return usuario;
        }
    }

    public static class SaveEvent extends UsuarioFormEvent {
        SaveEvent(UsuarioForm source, Usuario usuario) {
            super(source, usuario);
        }
    }

    public static class DeleteEvent extends UsuarioFormEvent {
        DeleteEvent(UsuarioForm source, Usuario usuario) {
            super(source, usuario);
        }
    }

    public static class CloseEvent extends UsuarioFormEvent {
        CloseEvent(UsuarioForm source) {
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