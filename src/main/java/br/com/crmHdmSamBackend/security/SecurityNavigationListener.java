//package br.com.crmHdmSamBackend.security;
//import br.com.crmHdmSamBackend.views.login.LoginView;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.router.BeforeEnterObserver;
//import com.vaadin.flow.server.ServiceInitEvent;
//import com.vaadin.flow.server.VaadinServiceInitListener;
//import org.springframework.stereotype.Component;
//
//import br.com.crmHdmSamBackend.service.AuthenticationService;
//import br.com.crmHdmSamBackend.views.login.LoginView;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.server.ServiceInitEvent;
//import com.vaadin.flow.server.VaadinServiceInitListener;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class SecurityNavigationListener implements VaadinServiceInitListener {
//
//    private final AuthenticationService authenticationService;
//
//    public SecurityNavigationListener(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//    @Override
//    public void serviceInit(ServiceInitEvent event) {
//        event.getSource().addUIInitListener(uiEvent -> {
//            uiEvent.getUI().addBeforeEnterListener(this::checkAccess);
//        });
//    }
//
//    private void checkAccess(BeforeEnterEvent event) {
//        Class<?> targetView = event.getNavigationTarget();
//
//        if (targetView == null) {
//            return;
//        }
//
//        boolean isLoginView = LoginView.class.equals(targetView);
//        boolean isAuthenticated = authenticationService.isUsuarioAutenticado();
//
//        if (!isLoginView && !isAuthenticated) {
//            event.rerouteTo(LoginView.class);
//        } else if (isLoginView && isAuthenticated) {
//            event.rerouteTo("dashboard");
//        }
//    }
//}
