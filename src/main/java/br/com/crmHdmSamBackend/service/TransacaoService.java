package br.com.crmHdmSamBackend.service;

import br.com.crmHdmSamBackend.model.Transacao;
import br.com.crmHdmSamBackend.model.dto.*;
import br.com.crmHdmSamBackend.exception.*;
import br.com.crmHdmSamBackend.model.*;
import br.com.crmHdmSamBackend.model.dto.UsuarioDTO;
import br.com.crmHdmSamBackend.model.enums.StatusTransacao;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.repository.TransacaoRepository;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<TransacaoDTO> findByUsuarioId(UUID usuarioId) {
        validateUsuarioExists(usuarioId);
        return transacaoRepository.findByUsuarioIdOrderByDataDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> findByUsuarioIdAndTipo(UUID usuarioId, TipoTransacao tipo) {
        validateUsuarioExists(usuarioId);
        return transacaoRepository.findByUsuarioIdAndTipo(usuarioId, tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> findByUsuarioIdAndStatus(UUID usuarioId, StatusTransacao status) {
        validateUsuarioExists(usuarioId);
        return transacaoRepository.findByUsuarioIdAndStatus(usuarioId, status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> findByUsuarioIdAndPeriodo(UUID usuarioId, OffsetDateTime inicio, OffsetDateTime fim) {
        validateUsuarioExists(usuarioId);
        return transacaoRepository.findByUsuarioIdAndDataBetween(usuarioId, inicio, fim)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> findByUsuarioIdAndMetodoPagamento(UUID usuarioId, String metodo) {
        validateUsuarioExists(usuarioId);
        return transacaoRepository.findByUsuarioIdAndMetodoPagamento(usuarioId, metodo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransacaoDTO findById(UUID id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com ID: " + id));
        return toDTO(transacao);
    }

    public TransacaoDTO create(UUID usuarioId, TransacaoCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setTelefone(dto.getTelefone());
        transacao.setTipo(TipoTransacao.valueOf(dto.getTipo().toUpperCase()));
        transacao.setCategoria(dto.getCategoria());
        transacao.setDescricao(dto.getDescricao());
        transacao.setQuantia(dto.getQuantia());
        transacao.setData(dto.getData() != null ? dto.getData() : OffsetDateTime.now());
        transacao.setMetodoPagamento(dto.getMetodoPagamento());
        transacao.setStatus(dto.getStatus() != null ?
                StatusTransacao.valueOf(dto.getStatus().toUpperCase()) : StatusTransacao.CONFIRMADA);

        Transacao saved = transacaoRepository.save(transacao);
        return toDTO(saved);
    }

    public TransacaoDTO update(UUID id, TransacaoUpdateDTO dto) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com ID: " + id));

        if (dto.getTelefone() != null) {
            transacao.setTelefone(dto.getTelefone());
        }
        if (dto.getCategoria() != null) {
            transacao.setCategoria(dto.getCategoria());
        }
        if (dto.getDescricao() != null) {
            transacao.setDescricao(dto.getDescricao());
        }
        if (dto.getQuantia() != null) {
            transacao.setQuantia(dto.getQuantia());
        }
        if (dto.getData() != null) {
            transacao.setData(dto.getData());
        }
        if (dto.getMetodoPagamento() != null) {
            transacao.setMetodoPagamento(dto.getMetodoPagamento());
        }
        if (dto.getStatus() != null) {
            transacao.setStatus(StatusTransacao.valueOf(dto.getStatus().toUpperCase()));
        }

        transacao.setAtualizadoEm(OffsetDateTime.now());
        Transacao updated = transacaoRepository.save(transacao);
        return toDTO(updated);
    }

    public void delete(UUID id) {
        if (!transacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transação não encontrada com ID: " + id);
        }
        transacaoRepository.deleteById(id);
    }

    public ResumoFinanceiroDTO getResumoFinanceiro(UUID usuarioId) {
        validateUsuarioExists(usuarioId);

        BigDecimal totalEntradas = transacaoRepository.sumByUsuarioIdAndTipo(usuarioId, TipoTransacao.ENTRADA);
        BigDecimal totalSaidas = transacaoRepository.sumByUsuarioIdAndTipo(usuarioId, TipoTransacao.SAIDA);

        if (totalEntradas == null) totalEntradas = BigDecimal.ZERO;
        if (totalSaidas == null) totalSaidas = BigDecimal.ZERO;

        BigDecimal saldo = totalEntradas.subtract(totalSaidas);

        Long totalTransacoes = (long) transacaoRepository.findByUsuarioId(usuarioId).size();
        Long transacoesPendentes = transacaoRepository.countByUsuarioIdAndStatus(usuarioId, StatusTransacao.PENDENTE);

        return new ResumoFinanceiroDTO(totalEntradas, totalSaidas, saldo, totalTransacoes, transacoesPendentes);
    }

    public List<CategoriaSomaDTO> getSomaPorCategoria(UUID usuarioId, TipoTransacao tipo) {
        validateUsuarioExists(usuarioId);
        List<Object[]> results = transacaoRepository.sumByCategoria(usuarioId, tipo);

        return results.stream()
                .map(result -> new CategoriaSomaDTO(
                        (String) result[0],
                        (BigDecimal) result[1]
                ))
                .collect(Collectors.toList());
    }

    private void validateUsuarioExists(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
    }

    private TransacaoDTO toDTO(Transacao transacao) {
        return new TransacaoDTO(
                transacao.getId(),
                transacao.getUsuario().getId(),
                transacao.getTelefone(),
                transacao.getTipo().name().toLowerCase(),
                transacao.getCategoria(),
                transacao.getDescricao(),
                transacao.getQuantia(),
                transacao.getData(),
                transacao.getMetodoPagamento(),
                transacao.getStatus().name().toLowerCase(),
                transacao.getCriadoEm(),
                transacao.getAtualizadoEm()
        );
    }


    public ResumoFinanceiroDTO getResumoFinanceiroPorPeriodo(UUID usuarioId, OffsetDateTime inicio, OffsetDateTime fim) {
        validateUsuarioExists(usuarioId);

        BigDecimal totalEntradas = transacaoRepository.sumByUsuarioIdAndTipoAndDataBetween(
                usuarioId, TipoTransacao.ENTRADA, inicio, fim);
        BigDecimal totalSaidas = transacaoRepository.sumByUsuarioIdAndTipoAndDataBetween(
                usuarioId, TipoTransacao.SAIDA, inicio, fim);

        if (totalEntradas == null) totalEntradas = BigDecimal.ZERO;
        if (totalSaidas == null) totalSaidas = BigDecimal.ZERO;

        BigDecimal saldo = totalEntradas.subtract(totalSaidas);

        Long totalTransacoes = (long) transacaoRepository.findByUsuarioIdAndDataBetween(
                usuarioId, inicio, fim).size();
        Long transacoesPendentes = transacaoRepository.countByUsuarioIdAndStatus(usuarioId, StatusTransacao.PENDENTE);

        return new ResumoFinanceiroDTO(totalEntradas, totalSaidas, saldo, totalTransacoes, transacoesPendentes);
    }


    public Transacao save(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }


    public List<Transacao> findAll() {
        return transacaoRepository.findAll();
    }



    public void delete(Transacao transacao) {
        transacaoRepository.delete(transacao);
    }

    public List<Transacao> findByDescricaoContaining(String descricao) {
        return transacaoRepository.findByDescricaoContainingIgnoreCase(descricao);
    }

    public List<Transacao> findByTipo(TipoTransacao tipo) {
        return transacaoRepository.findByTipo(tipo);
    }

    public List<Transacao> findByStatus(StatusTransacao status) {
        return transacaoRepository.findByStatus(status);
    }
}
