package br.com.crmHdmSamBackend.views;
import br.com.crmHdmSamBackend.model.Usuario;

import br.com.crmHdmSamBackend.service.UsuarioService;
import br.com.crmHdmSamBackend.views.form.ClienteFormView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
@Route(value = "clientes", layout = MainLayout.class)
@PageTitle("Clientes | Sistema CRUD")
public class ClienteListView extends VerticalLayout {

    private final UsuarioService service;
    private final Grid<Usuario> grid = new Grid<>(Usuario.class, false);
    private final TextField filterText = new TextField();
    private ClienteFormView form;

    public ClienteListView(UsuarioService service) {
        this.service = service;
        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("cliente-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // Colunas
        grid.addColumn(Usuario::getId)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(Usuario::getNome)
                .setHeader("Nome")
                .setSortable(true);

        grid.addColumn(Usuario::getEmail)
                .setHeader("Email")
                .setSortable(true);

        grid.addColumn(Usuario::getTelefone)
                .setHeader("Telefone");

        grid.addColumn(cliente ->
                        cliente.getId()
                                )
                .setHeader("Nascimento")
                .setSortable(true);

        grid.addColumn(cliente -> 1 > 0 ? "Sim" : "Não")
                .setHeader("Ativo");

        // Coluna de ações
        grid.addComponentColumn(cliente -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> editCliente(cliente));

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(
                    ButtonVariant.LUMO_SMALL,
                    ButtonVariant.LUMO_ERROR
            );
            deleteButton.addClickListener(e -> deleteCliente(cliente));

            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Ações").setWidth("150px").setFlexGrow(0);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e ->
                editCliente(e.getValue())
        );
    }

    private void configureForm() {
        form = new ClienteFormView();
        form.setWidth("25em");

        form.addSaveListener(this::saveCliente);
        form.addDeleteListener(this::deleteCliente);
        form.addCloseListener(e -> closeEditor());
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nome ou email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("300px");

        Button addClienteButton = new Button("Novo Cliente");
        addClienteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClienteButton.addClickListener(e -> addCliente());

        HorizontalLayout toolbar = new HorizontalLayout(
                filterText,
                addClienteButton
        );
        toolbar.setAlignItems(Alignment.BASELINE);
        toolbar.setWidthFull();

        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void updateList() {
      //  grid.setItems((ListDataProvider<Usuario>) service.findAll());
    }

    private void addCliente() {
        grid.asSingleSelect().clear();
        editCliente(new Usuario());
    }

    private void editCliente(Usuario cliente) {
        if (cliente == null) {
            closeEditor();
        } else {
            form.setCliente(cliente);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCliente(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void saveCliente(ClienteFormView.SaveEvent event) {
        try {
            service.save(event.getCliente());
            updateList();
            closeEditor();
        } catch (Exception ex) {
            form.showError(ex.getMessage());
        }
    }

    private void deleteCliente(ClienteFormView.DeleteEvent event) {
        deleteCliente(event.getCliente());
    }

    private void deleteCliente(Usuario cliente) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar exclusão");
        dialog.setText("Deseja realmente excluir " + cliente.getNome() + "?");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");

        dialog.setConfirmText("Excluir");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            service.delete(cliente.getId());
            updateList();
            closeEditor();
        });

        dialog.open();
    }
}
