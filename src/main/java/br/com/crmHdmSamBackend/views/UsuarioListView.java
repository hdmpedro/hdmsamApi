package br.com.crmHdmSamBackend.views;


import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.service.UsuarioService;
import br.com.crmHdmSamBackend.views.form.UsuarioForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.service.UsuarioService;
import br.com.crmHdmSamBackend.views.form.UsuarioForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;

@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Usuários | FinanceApp")
public class UsuarioListView extends VerticalLayout {

    private Grid<Usuario> grid = new Grid<>(Usuario.class, false);
    private TextField filterText = new TextField();
    private UsuarioForm form;

    private final UsuarioService usuarioService;

    public UsuarioListView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
        form = new UsuarioForm();
        form.setWidth("25em");

        form.addSaveListener(e -> saveUsuario(e.getUsuario()));
        form.addDeleteListener(e -> deleteUsuario(e.getUsuario()));
        form.addCloseListener(e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassName("usuario-grid");
        grid.setSizeFull();

        grid.addColumn(Usuario::getNome).setHeader("Nome").setSortable(true);
        grid.addColumn(Usuario::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(Usuario::getTelefone).setHeader("Telefone");
        grid.addColumn(u -> {
            if (u.getCriadoEm() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return u.getCriadoEm().format(formatter);
            }
            return "";
        }).setHeader("Criado em");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editUsuario(e.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nome ou email...");
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList()); 

        Button addButton = new Button("Novo Usuário");
        addButton.setIcon(VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addUsuario());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return toolbar;
    }

    private void editUsuario(Usuario usuario) {
        if (usuario == null) {
            closeEditor();
        } else {
            form.setUsuario(usuario);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setUsuario(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addUsuario() {
        grid.asSingleSelect().clear();
        editUsuario(new Usuario());
    }

    private void saveUsuario(Usuario usuario) {
        try {
            usuarioService.save(usuario);
            updateList();
            closeEditor();

            Notification notification = Notification.show("Usuário salvo com sucesso!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.setDuration(3000);
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao salvar: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.setDuration(5000);
        }
    }

    private void deleteUsuario(Usuario usuario) {
        try {
            if (usuario.getId() != null) {
                usuarioService.delete(usuario.getId());
                updateList();
                closeEditor();

                Notification notification = Notification.show("Usuário deletado com sucesso!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.setDuration(3000);
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao deletar: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.setDuration(5000);
        }
    }

    private void updateList() {
        try {
            String searchTerm = filterText.getValue();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                grid.setItems(usuarioService.findByNomeOrEmail(searchTerm));
            } else {
                grid.setItems(usuarioService.findAllUsuarios());
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao carregar dados: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }
}