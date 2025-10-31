package br.com.crmHdmSamBackend.restControllers;


import br.com.crmHdmSamBackend.model.dto.*;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.service.CategoriaService;
import br.com.crmHdmSamBackend.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/usuarios/{usuarioId}/categorias")
class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> findByUsuarioId(@PathVariable UUID usuarioId) {
        List<CategoriaDTO> categorias = categoriaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CategoriaDTO>> findByUsuarioIdAndTipo(
            @PathVariable UUID usuarioId,
            @PathVariable String tipo) {
        TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
        List<CategoriaDTO> categorias = categoriaService.findByUsuarioIdAndTipo(usuarioId, tipoTransacao);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> findById(@PathVariable UUID id) {
        CategoriaDTO categoria = categoriaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> create(
            @PathVariable UUID usuarioId,
            @RequestBody CategoriaCreateDTO dto) {
        CategoriaDTO categoria = categoriaService.create(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> update(
            @PathVariable UUID id,
            @RequestBody CategoriaUpdateDTO dto) {
        CategoriaDTO categoria = categoriaService.update(id, dto);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countByUsuarioId(@PathVariable UUID usuarioId) {
        Long count = categoriaService.countByUsuarioId(usuarioId);
        return ResponseEntity.ok(count);
    }
}
