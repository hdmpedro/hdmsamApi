package br.com.crmHdmSamBackend.restControllers;

import br.com.crmHdmSamBackend.exception.CredenciaisInvalidasException;
import br.com.crmHdmSamBackend.exception.InvalidRefreshTokenException;
import br.com.crmHdmSamBackend.exception.UsuarioBloqueadoException;
import br.com.crmHdmSamBackend.exception.UsuarioInativoException;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.model.dto.*;
import br.com.crmHdmSamBackend.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;

    @Autowired
    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request.getLogin(), request.getSenha());
            return ResponseEntity.ok(response);
        } catch (CredenciaisInvalidasException | UsuarioInativoException | UsuarioBloqueadoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/renovar")
    public ResponseEntity<AuthResponse> renovar(@Valid @RequestBody RenovarRequest request) {
        try {
            AuthResponse response = authService.refresh(request.getRenovarToken());
            return ResponseEntity.ok(response);
        } catch (InvalidRefreshTokenException | UsuarioInativoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/revogar-todos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> revogarTodos() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        authService.revogarTodosTokensUsuario(usuario.getId());
        return ResponseEntity.ok().build();
    }
}