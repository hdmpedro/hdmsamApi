//package br.com.crmHdmSamBackend.security;
//import br.com.crmHdmSamBackend.security.annotation.AdminOnly;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.router.BeforeEnterObserver;
//import com.vaadin.flow.server.ServiceInitEvent;
//import com.vaadin.flow.server.VaadinServiceInitListener;
//import org.springframework.stereotype.Component;
//
//
//import br.com.crmHdmSamBackend.security.annotation.AdminOnly;
//import br.com.crmHdmSamBackend.service.AuthenticationService;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.server.ServiceInitEvent;
//import com.vaadin.flow.server.VaadinServiceInitListener;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class AdminAccessListener implements VaadinServiceInitListener {
//
//    private final AuthenticationService authenticationService;
//
//    public AdminAccessListener(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//    @Override
//    public void serviceInit(ServiceInitEvent event) {
//        event.getSource().addUIInitListener(uiEvent -> {
//            uiEvent.getUI().addBeforeEnterListener(this::checkAdminAccess);
//        });
//    }
//
//    private void checkAdminAccess(BeforeEnterEvent event) {
//        Class<?> targetView = event.getNavigationTarget();
//
//        if (targetView != null && targetView.isAnnotationPresent(AdminOnly.class)) {
//            if (!authenticationService.isUsuarioAdmin()) {
//                Notification notification = Notification.show("Acesso negado. Área restrita para administradores.");
//                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//                event.rerouteTo("dashboard");
//            }
//        }
//    }
//}