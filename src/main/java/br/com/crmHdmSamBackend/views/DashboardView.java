package br.com.crmHdmSamBackend.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | FinanceApp")
@RolesAllowed("USER")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setSpacing(true);
        setPadding(true);

        H2 title = new H2("Dashboard Financeiro");
        add(title);

        HorizontalLayout cardsLayout = new HorizontalLayout();
        cardsLayout.setWidthFull();
        cardsLayout.setSpacing(true);

        cardsLayout.add(
                createStatCard("Receitas", "R$ 0,00", "success"),
                createStatCard("Despesas", "R$ 0,00", "error"),
                createStatCard("Saldo", "R$ 0,00", "primary"),
                createStatCard("Transações", "0", "contrast")
        );

        add(cardsLayout);
    }

    private Component createStatCard(String title, String value, String theme) {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Background.CONTRAST_5
        );
        card.getStyle()
                .set("border-left", "4px solid var(--lumo-" + theme + "-color)")
                .set("min-width", "200px");

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY
        );

        Span valueSpan = new Span(value);
        valueSpan.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD
        );

        VerticalLayout content = new VerticalLayout(titleSpan, valueSpan);
        content.setSpacing(false);
        content.setPadding(false);
        card.add(content);

        return card;
    }
}
