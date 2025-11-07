package br.com.crmHdmSamBackend.restControllers;

import br.com.crmHdmSamBackend.exception.*;
import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.model.dto.api.AuthResponse;
import br.com.crmHdmSamBackend.model.dto.api.LoginRequest;
import br.com.crmHdmSamBackend.model.dto.api.RenovarRequest;
import br.com.crmHdmSamBackend.model.dto.api.TokenInfoDTO;
import br.com.crmHdmSamBackend.security.service.ApiAuthService;
import br.com.crmHdmSamBackend.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final ApiAuthService servicoAutenticacao;

    @Autowired
    public ApiAuthController(ApiAuthService apiAuthService) {
        this.servicoAutenticacao = apiAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest pedido,
            HttpServletRequest httpRequest) {
        try {
            String enderecoIp = IpUtils.obterEnderecoIpCliente(httpRequest);
            AuthResponse resposta = servicoAutenticacao.login(
                    pedido.getLogin(),
                    pedido.getSenha(),
                    enderecoIp
            );
            return ResponseEntity.ok(resposta);
        } catch (CredenciaisInvalidasException | UsuarioInativoException |
                 UsuarioBloqueadoException | TentativasExcedidasException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/renovar")
    public ResponseEntity<AuthResponse> renovar(@Valid @RequestBody RenovarRequest pedido) {
        try {
            AuthResponse resposta = servicoAutenticacao.refresh(pedido.getRenovarToken());
            return ResponseEntity.ok(resposta);
        } catch (InvalidRefreshTokenException | UsuarioInativoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String cabecalhoAuth = request.getHeader("Authorization");

        if (cabecalhoAuth != null && cabecalhoAuth.startsWith("Bearer ")) {
            String token = cabecalhoAuth.substring(7);
            servicoAutenticacao.logout(token);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/revogar-todos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> revogarTodos() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        servicoAutenticacao.revogarTodosTokensUsuario(usuario.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tokens")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TokenInfoDTO>> listarTokens() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        List<TokenInfoDTO> tokens = servicoAutenticacao.listarTokensAtivos(usuario.getId());
        return ResponseEntity.ok(tokens);
    }

    @DeleteMapping("/tokens/{tokenId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> revogarToken(@PathVariable UUID tokenId) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        servicoAutenticacao.revogarTokenEspecifico(usuario.getId(), tokenId);
        return ResponseEntity.ok().build();
    }


}