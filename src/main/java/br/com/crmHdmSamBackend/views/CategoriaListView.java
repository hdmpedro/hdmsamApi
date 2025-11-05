package br.com.crmHdmSamBackend.views;


import br.com.crmHdmSamBackend.model.Categoria;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.views.form.CategoriaForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "categorias", layout = MainLayout.class)
@PageTitle("Categorias | FinanceApp")
@RolesAllowed("USER")

public class CategoriaListView extends VerticalLayout {

    private Grid<Categoria> grid = new Grid<>(Categoria.class, false);
    private TextField filterText = new TextField();
    private ComboBox<TipoTransacao> tipoFilter = new ComboBox<>();
    private CategoriaForm form;

    public CategoriaListView() {
        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
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
        form = new CategoriaForm();
        form.setWidth("25em");

        form.addSaveListener(e -> saveCategoria(e.getCategoria()));
        form.addDeleteListener(e -> deleteCategoria(e.getCategoria()));
        form.addCloseListener(e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassName("categoria-grid");
        grid.setSizeFull();

        grid.addColumn(Categoria::getNome).setHeader("Nome").setSortable(true);
        grid.addColumn(c -> c.getTipo().toString()).setHeader("Tipo").setSortable(true);
        grid.addColumn(Categoria::getIcon).setHeader("Ícone");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editCategoria(e.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nome...");
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        tipoFilter.setPlaceholder("Filtrar por tipo");
        tipoFilter.setItems(TipoTransacao.values());
        tipoFilter.setClearButtonVisible(true);

        Button addButton = new Button("Nova Categoria");
        addButton.setIcon(VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addCategoria());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, tipoFilter, addButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        return toolbar;
    }

    private void editCategoria(Categoria categoria) {
        if (categoria == null) {
            closeEditor();
        } else {
            form.setCategoria(categoria);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCategoria(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addCategoria() {
        grid.asSingleSelect().clear();
        editCategoria(new Categoria());
    }

    private void saveCategoria(Categoria categoria) {
        closeEditor();
    }

    private void deleteCategoria(Categoria categoria) {
        closeEditor();
    }
}
