package com.residencial.anhandui.infrastructure.adapters.outbound.persistence;

import com.residencial.anhandui.domain.model.CategoriaDocumento;
import com.residencial.anhandui.domain.model.DocumentoCondominio;
import com.residencial.anhandui.domain.ports.output.IDocumentoRepositoryPort;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.DocumentoEntity;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository.SpringDataDocumentoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DocumentoRepositoryAdapter implements IDocumentoRepositoryPort {

    private final SpringDataDocumentoRepository springRepository;

    public DocumentoRepositoryAdapter(SpringDataDocumentoRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public DocumentoCondominio salvar(DocumentoCondominio doc) {
        DocumentoEntity entity = toEntity(doc);
        DocumentoEntity salva = springRepository.save(entity);
        return toDomain(salva);
    }

    @Override
    public List<DocumentoCondominio> listarTodos() {
        return springRepository.findAllByOrderByDataUploadDesc().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DocumentoCondominio> buscarPorId(UUID id) {
        if (id == null) return Optional.empty();
        return springRepository.findById(id.toString()).map(this::toDomain);
    }

    private DocumentoCondominio toDomain(DocumentoEntity entity) {
        return new DocumentoCondominio(
                UUID.fromString(entity.getId()),
                entity.getTitulo(),
                CategoriaDocumento.valueOf(entity.getCategoria()),
                entity.getNomeArquivo(),
                entity.getCaminhoArquivo(),
                entity.getLocalizacaoFisica(),
                entity.getDataUpload()
        );
    }

    private DocumentoEntity toEntity(DocumentoCondominio domain) {
        DocumentoEntity entity = new DocumentoEntity();
        entity.setId(domain.getId().toString());
        entity.setTitulo(domain.getTitulo());
        entity.setCategoria(domain.getCategoria().name());
        entity.setNomeArquivo(domain.getNomeArquivo());
        entity.setCaminhoArquivo(domain.getCaminhoArquivo());
        entity.setLocalizacaoFisica(domain.getLocalizacaoFisica());
        entity.setDataUpload(domain.getDataUpload());
        return entity;
    }
}
