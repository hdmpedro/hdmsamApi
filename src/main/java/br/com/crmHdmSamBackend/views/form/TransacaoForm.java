package br.com.crmHdmSamBackend.views.form;


import br.com.crmHdmSamBackend.model.Transacao;
import br.com.crmHdmSamBackend.model.enums.StatusTransacao;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.ZoneId;

public class TransacaoForm extends FormLayout {

    private Transacao transacao;

    ComboBox<TipoTransacao> tipo = new ComboBox<>("Tipo");
    TextField categoria = new TextField("Categoria");
    TextArea descricao = new TextArea("Descrição");
    BigDecimalField quantia = new BigDecimalField("Valor");
    DateTimePicker data = new DateTimePicker("Data");
    TextField metodoPagamento = new TextField("Método de Pagamento");
    ComboBox<StatusTransacao> status = new ComboBox<>("Status");

    Button save = new Button("Salvar");
    Button delete = new Button("Deletar");
    Button close = new Button("Cancelar");

    Binder<Transacao> binder = new BeanValidationBinder<>(Transacao.class);

    public TransacaoForm() {
        addClassName("transacao-form");

        tipo.setItems(TipoTransacao.values());
        tipo.setItemLabelGenerator(TipoTransacao::toString);

        status.setItems(StatusTransacao.values());
        status.setItemLabelGenerator(StatusTransacao::toString);
        status.setValue(StatusTransacao.CONFIRMADA);

        quantia.setPrefixComponent(new Span("R$"));
        descricao.setHeight("100px");

        // CONFIGURAR O BINDING DA DATA MANUALMENTE ANTES do bindInstanceFields
        binder.forField(data)
                .withConverter(
                        // LocalDateTime -> OffsetDateTime
                        localDateTime -> localDateTime != null
                                ? localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime()
                                : null,
                        // OffsetDateTime -> LocalDateTime
                        offsetDateTime -> offsetDateTime != null
                                ? offsetDateTime.toLocalDateTime()
                                : null
                )
                .bind(Transacao::getData, Transacao::setData);

        // Agora bind os outros campos automaticamente
        // IMPORTANTE: remover "data" do binding automático
        binder.bindInstanceFields(this);

        add(
                tipo,
                categoria,
                descricao,
                quantia,
                data,
                metodoPagamento,
                status,
                createButtonsLayout()
        );

        setColspan(descricao, 2);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, transacao)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(transacao);
            fireEvent(new SaveEvent(this, transacao));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setTransacao(Transacao transacao) {
        this.transacao = transacao;
        binder.readBean(transacao);
    }

    public static abstract class TransacaoFormEvent extends ComponentEvent<TransacaoForm> {
        private Transacao transacao;

        protected TransacaoFormEvent(TransacaoForm source, Transacao transacao) {
            super(source, false);
            this.transacao = transacao;
        }

        public Transacao getTransacao() {
            return transacao;
        }
    }

    public static class SaveEvent extends TransacaoFormEvent {
        SaveEvent(TransacaoForm source, Transacao transacao) {
            super(source, transacao);
        }
    }

    public static class DeleteEvent extends TransacaoFormEvent {
        DeleteEvent(TransacaoForm source, Transacao transacao) {
            super(source, transacao);
        }
    }

    public static class CloseEvent extends TransacaoFormEvent {
        CloseEvent(TransacaoForm source) {
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

