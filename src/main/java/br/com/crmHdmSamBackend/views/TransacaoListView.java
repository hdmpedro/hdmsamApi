package br.com.crmHdmSamBackend.views;


import br.com.crmHdmSamBackend.model.Transacao;
import br.com.crmHdmSamBackend.model.enums.StatusTransacao;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.views.form.TransacaoForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.crmHdmSamBackend.model.Transacao;
import br.com.crmHdmSamBackend.model.enums.StatusTransacao;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.service.TransacaoService; // IMPORT DO SERVICE
import br.com.crmHdmSamBackend.views.form.TransacaoForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "transacoes", layout = MainLayout.class)
@PageTitle("Transações | FinanceApp")
@PermitAll
public class TransacaoListView extends VerticalLayout {

    private Grid<Transacao> grid = new Grid<>(Transacao.class, false);
    private TextField filterText = new TextField();
    private ComboBox<TipoTransacao> tipoFilter = new ComboBox<>();
    private ComboBox<StatusTransacao> statusFilter = new ComboBox<>();
    private TransacaoForm form;

    private final TransacaoService transacaoService;

    public TransacaoListView(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new TransacaoForm();
        form.setWidth("25em");

        form.addSaveListener(e -> saveTransacao(e.getTransacao()));
        form.addDeleteListener(e -> deleteTransacao(e.getTransacao()));
        form.addCloseListener(e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassName("transacao-grid");
        grid.setSizeFull();

        grid.addColumn(new ComponentRenderer<>(transacao -> {
            Span badge = new Span(transacao.getTipo().toString());
            badge.getElement().getThemeList().add(
                    transacao.getTipo() == TipoTransacao.ENTRADA ? "badge success" : "badge error"
            );
            return badge;
        })).setHeader("Tipo").setWidth("120px");

        grid.addColumn(Transacao::getCategoria).setHeader("Categoria").setSortable(true);
        grid.addColumn(Transacao::getDescricao).setHeader("Descrição");

        grid.addColumn(transacao -> {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            return formatter.format(transacao.getQuantia());
        }).setHeader("Valor").setSortable(true);

        grid.addColumn(t -> t.getData().toString()).setHeader("Data").setSortable(true);
        grid.addColumn(Transacao::getMetodoPagamento).setHeader("Método");

        grid.addColumn(new ComponentRenderer<>(transacao -> {
            Span badge = new Span(transacao.getStatus().toString());
            badge.getElement().getThemeList().add(
                    transacao.getStatus() == StatusTransacao.CONFIRMADA ? "badge success" : "badge contrast"
            );
            return badge;
        })).setHeader("Status").setWidth("120px");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editTransacao(e.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por descrição...");
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList()); // ADICIONA LISTENER

        tipoFilter.setPlaceholder("Tipo");
        tipoFilter.setItems(TipoTransacao.values());
        tipoFilter.setClearButtonVisible(true);
        tipoFilter.setWidth("150px");
        tipoFilter.addValueChangeListener(e -> updateList()); // ADICIONA LISTENER

        statusFilter.setPlaceholder("Status");
        statusFilter.setItems(StatusTransacao.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.setWidth("150px");
        statusFilter.addValueChangeListener(e -> updateList()); // ADICIONA LISTENER

        Button addButton = new Button("Nova Transação");
        addButton.setIcon(VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addTransacao());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, tipoFilter, statusFilter, addButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        return toolbar;
    }

    private void editTransacao(Transacao transacao) {
        if (transacao == null) {
            closeEditor();
        } else {
            form.setTransacao(transacao);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setTransacao(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addTransacao() {
        grid.asSingleSelect().clear();
        editTransacao(new Transacao());
    }

    private void saveTransacao(Transacao transacao) {
        try {
            transacaoService.save(transacao);
            updateList();
            closeEditor();

            Notification notification = Notification.show("Transação salva com sucesso!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao salvar: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }

    private void deleteTransacao(Transacao transacao) {
        try {
            transacaoService.delete(transacao);
            updateList();
            closeEditor();

            Notification notification = Notification.show("Transação deletada com sucesso!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao deletar: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }

    private void updateList() {
        try {
            String searchTerm = filterText.getValue();
            TipoTransacao tipo = tipoFilter.getValue();
            StatusTransacao status = statusFilter.getValue();

            if (searchTerm != null && !searchTerm.isEmpty()) {
                grid.setItems(transacaoService.findByDescricaoContaining(searchTerm));
            } else if (tipo != null) {
                grid.setItems(transacaoService.findByTipo(tipo));
            } else if (status != null) {
                grid.setItems(transacaoService.findByStatus(status));
            } else {
                grid.setItems(transacaoService.findAll());
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao carregar dados: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }
}