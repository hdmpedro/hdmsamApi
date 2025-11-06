package br.com.crmHdmSamBackend.views;


import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.security.service.AutenticacaoService;
import br.com.crmHdmSamBackend.views.listview.CategoriaListView;
import br.com.crmHdmSamBackend.views.listview.ClienteListView;
import br.com.crmHdmSamBackend.views.listview.TransacaoListView;
import br.com.crmHdmSamBackend.views.listview.UsuarioListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;


import br.com.crmHdmSamBackend.security.service.SecurityService;
import com.vaadin.flow.component.button.Button;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final AutenticacaoService autenticacaoService;

    public MainLayout(SecurityService securityService, AutenticacaoService autenticacaoService) {
        this.securityService = securityService;
        this.autenticacaoService = autenticacaoService;

        createHeader();
        createDrawer();

        setPrimarySection(Section.DRAWER);
    }

    private void createHeader() {
        H1 logo = new H1("HDM SAM");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );
        logo.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-xl)");

        Usuario usuario = autenticacaoService.getUsuarioAutenticado().orElse(null);
        Span userInfo = new Span(usuario != null ? usuario.getNome() : "Usuário");
        userInfo.addClassNames(LumoUtility.FontWeight.MEDIUM);
        userInfo.getStyle().set("margin-right", "10px");

        Button logout = new Button("Sair", new Icon(VaadinIcon.SIGN_OUT));
        logout.addClickListener(e -> securityService.logout());
        logout.getStyle().set("cursor", "pointer");

        HorizontalLayout userLayout = new HorizontalLayout(userInfo, logout);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userLayout.setSpacing(true);
        userLayout.getStyle()
                .set("margin-left", "auto")
                .set("padding-right", "var(--lumo-space-m)");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, userLayout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(logo);
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );
        header.getStyle()
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("background-color", "var(--lumo-base-color)");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout navigation = new VerticalLayout();
        navigation.setSizeFull();
        navigation.setPadding(false);
        navigation.setSpacing(false);
        navigation.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("background-color", "var(--lumo-contrast-5pct)");

        navigation.add(
                createLink("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD),
                createLink("Usuários", UsuarioListView.class, VaadinIcon.USERS),
                createLink("Clientes", ClienteListView.class, VaadinIcon.USER_CARD),
                createLink("Categorias", CategoriaListView.class, VaadinIcon.TAGS),
                createLink("Transações", TransacaoListView.class, VaadinIcon.MONEY_EXCHANGE)
        );

        addToDrawer(navigation);
    }

    private RouterLink createLink(String text, Class<? extends com.vaadin.flow.component.Component> view, VaadinIcon icon) {
        RouterLink link = new RouterLink();

        Icon linkIcon = new Icon(icon);
        linkIcon.getStyle()
                .set("margin-right", "var(--lumo-space-s)")
                .set("width", "var(--lumo-icon-size-s)")
                .set("height", "var(--lumo-icon-size-s)");

        Span linkText = new Span(text);

        link.add(linkIcon, linkText);
        link.setRoute(view);
        link.setTabIndex(-1);

        link.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("padding", "var(--lumo-space-s) var(--lumo-space-m)")
                .set("text-decoration", "none")
                .set("color", "var(--lumo-body-text-color)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("margin", "var(--lumo-space-xs) var(--lumo-space-s)")
                .set("transition", "background-color 0.2s");

        link.getElement().addEventListener("mouseenter", e -> {
            link.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
        });

        link.getElement().addEventListener("mouseleave", e -> {
            if (!link.getElement().hasAttribute("highlight")) {
                link.getStyle().set("background-color", "transparent");
            }
        });

        return link;
    }
}