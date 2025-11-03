package br.com.crmHdmSamBackend.views.form;

import br.com.crmHdmSamBackend.model.Categoria;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class CategoriaForm extends FormLayout {

    private Categoria categoria;

    TextField nome = new TextField("Nome");
    ComboBox<TipoTransacao> tipo = new ComboBox<>("Tipo");
    TextField icon = new TextField("Ícone");

    Button save = new Button("Salvar");
    Button delete = new Button("Deletar");
    Button close = new Button("Cancelar");

    Binder<Categoria> binder = new BeanValidationBinder<>(Categoria.class);

    public CategoriaForm() {
        addClassName("categoria-form");

        tipo.setItems(TipoTransacao.values());
        tipo.setItemLabelGenerator(TipoTransacao::toString);

        binder.bindInstanceFields(this);

        add(nome, tipo, icon, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, categoria)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, categoria));
        }
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        binder.readBean(categoria);
    }

    public static abstract class CategoriaFormEvent extends ComponentEvent<CategoriaForm> {
        private Categoria categoria;

        protected CategoriaFormEvent(CategoriaForm source, Categoria categoria) {
            super(source, false);
            this.categoria = categoria;
        }

        public Categoria getCategoria() {
            return categoria;
        }
    }

    public static class SaveEvent extends CategoriaFormEvent {
        SaveEvent(CategoriaForm source, Categoria categoria) {
            super(source, categoria);
        }
    }

    public static class DeleteEvent extends CategoriaFormEvent {
        DeleteEvent(CategoriaForm source, Categoria categoria) {
            super(source, categoria);
        }
    }

    public static class CloseEvent extends CategoriaFormEvent {
        CloseEvent(CategoriaForm source) {
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