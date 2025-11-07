package br.com.crmHdmSamBackend.restControllers;


import br.com.crmHdmSamBackend.model.dto.*;
import br.com.crmHdmSamBackend.model.enums.StatusTransacao;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.service.TransacaoService;
import br.com.crmHdmSamBackend.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/transacoes")
class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public ResponseEntity<List<TransacaoDTO>> findByUsuarioId(@PathVariable UUID usuarioId) {
        List<TransacaoDTO> transacoes = transacaoService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TransacaoDTO>> findByUsuarioIdAndTipo(
            @PathVariable UUID usuarioId,
            @PathVariable String tipo) {
        TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
        List<TransacaoDTO> transacoes = transacaoService.findByUsuarioIdAndTipo(usuarioId, tipoTransacao);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransacaoDTO>> findByUsuarioIdAndStatus(
            @PathVariable UUID usuarioId,
            @PathVariable String status) {
        StatusTransacao statusTransacao = StatusTransacao.valueOf(status.toUpperCase());
        List<TransacaoDTO> transacoes = transacaoService.findByUsuarioIdAndStatus(usuarioId, statusTransacao);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<TransacaoDTO>> findByUsuarioIdAndPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam OffsetDateTime inicio,
            @RequestParam OffsetDateTime fim) {
        List<TransacaoDTO> transacoes = transacaoService.findByUsuarioIdAndPeriodo(usuarioId, inicio, fim);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/metodo/{metodo}")
    public ResponseEntity<List<TransacaoDTO>> findByUsuarioIdAndMetodoPagamento(
            @PathVariable UUID usuarioId,
            @PathVariable String metodo) {
        List<TransacaoDTO> transacoes = transacaoService.findByUsuarioIdAndMetodoPagamento(usuarioId, metodo);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoDTO> findById(@PathVariable UUID id) {
        TransacaoDTO transacao = transacaoService.findById(id);
        return ResponseEntity.ok(transacao);
    }

    @PostMapping
    public ResponseEntity<TransacaoDTO> create(
            @PathVariable UUID usuarioId,
            @RequestBody TransacaoCreateDTO dto) {
        TransacaoDTO transacao = transacaoService.create(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoDTO> update(
            @PathVariable UUID id,
            @RequestBody TransacaoUpdateDTO dto) {
        TransacaoDTO transacao = transacaoService.update(id, dto);
        return ResponseEntity.ok(transacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroDTO> getResumoFinanceiro(@PathVariable UUID usuarioId) {
        ResumoFinanceiroDTO resumo = transacaoService.getResumoFinanceiro(usuarioId);
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/resumo/periodo")
    public ResponseEntity<ResumoFinanceiroDTO> getResumoFinanceiroPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam OffsetDateTime inicio,
            @RequestParam OffsetDateTime fim) {
        ResumoFinanceiroDTO resumo = transacaoService.getResumoFinanceiroPorPeriodo(usuarioId, inicio, fim);
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/categorias/soma/{tipo}")
    public ResponseEntity<List<CategoriaSomaDTO>> getSomaPorCategoria(
            @PathVariable UUID usuarioId,
            @PathVariable String tipo) {
        TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
        List<CategoriaSomaDTO> somas = transacaoService.getSomaPorCategoria(usuarioId, tipoTransacao);
        return ResponseEntity.ok(somas);
    }
}