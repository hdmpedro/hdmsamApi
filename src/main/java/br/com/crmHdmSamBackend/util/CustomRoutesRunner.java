//package br.com.crmHdmSamBackend.util;
//
//import br.com.crmHdmSamBackend.views.login.LoginView;
//import com.vaadin.flow.router.RouteConfiguration;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomRoutesRunner implements CommandLineRunner {
//
//    @Override
//    public void run(String... args) throws Exception {
//        RouteConfiguration routeConfiguration = RouteConfiguration.forApplicationScope();
//
//        // Configura as rotas
//        routeConfiguration.setRoute("", LoginView.class);
//        routeConfiguration.setRoute("login", LoginView.class);
//
//        // Verificação separada para debug
//        boolean rootRouteRegistered = routeConfiguration.isRouteRegistered("");
//        boolean loginRouteRegistered = routeConfiguration.isRouteRegistered("login");
//
//        if (rootRouteRegistered && loginRouteRegistered) {
//            System.out.println("✅ Rotas configuradas com sucesso!");
//        } else {
//            System.out.println("❌ Problema nas rotas - Root: " + rootRouteRegistered +
//                    ", Login: " + loginRouteRegistered);
//        }
//    }
//}