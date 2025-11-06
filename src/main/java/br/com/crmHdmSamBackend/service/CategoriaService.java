package br.com.crmHdmSamBackend.service;

import br.com.crmHdmSamBackend.model.dto.*;
import br.com.crmHdmSamBackend.exception.*;
import br.com.crmHdmSamBackend.model.*;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import br.com.crmHdmSamBackend.repository.CategoriaRepository;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<CategoriaDTO> findByUsuarioId(UUID usuarioId) {
        validateUsuarioExists(usuarioId);
        return categoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CategoriaDTO> findByUsuarioIdAndTipo(UUID usuarioId, TipoTransacao tipo) {
        validateUsuarioExists(usuarioId);
        return categoriaRepository.findByUsuarioIdAndTipoOrderByNome(usuarioId, tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO findById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com ID: " + id));
        return toDTO(categoria);
    }

    public CategoriaDTO create(UUID usuarioId, CategoriaCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + usuarioId));

        if (categoriaRepository.existsByUsuarioIdAndNome(usuarioId, dto.getNome())) {
            throw new CategoriaJaExisteException(dto.getNome());
        }

        Categoria categoria = new Categoria();
        categoria.setUsuario(usuario);
        categoria.setNome(dto.getNome());
        categoria.setTipo(TipoTransacao.valueOf(dto.getTipo().toUpperCase()));
        categoria.setIcon(dto.getIcon());

        Categoria saved = categoriaRepository.save(categoria);
        return toDTO(saved);
    }

    public CategoriaDTO update(UUID id, CategoriaUpdateDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com ID: " + id));

        if (dto.getNome() != null) {
            if (categoriaRepository.existsByUsuarioIdAndNome(categoria.getUsuario().getId(), dto.getNome()) &&
                    !categoria.getNome().equals(dto.getNome())) {
                throw new CategoriaJaExisteException(dto.getNome());
            }
            categoria.setNome(dto.getNome());
        }
        if (dto.getIcon() != null) {
            categoria.setIcon(dto.getIcon());
        }

        Categoria updated = categoriaRepository.save(categoria);
        return toDTO(updated);
    }

    public void delete(UUID id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Categoria não encontrada com ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    public Long countByUsuarioId(UUID usuarioId) {
        validateUsuarioExists(usuarioId);
        return categoriaRepository.countByUsuarioId(usuarioId);
    }

    private void validateUsuarioExists(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + usuarioId);
        }
    }

    private CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getUsuario().getId(),
                categoria.getNome(),
                categoria.getTipo().name().toLowerCase(),
                categoria.getIcon(),
                categoria.getCriadoEm()
        );
    }
}