package com.residencial.anhandui.domain.ports.output;

import com.residencial.anhandui.domain.model.DocumentoCondominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentoRepositoryPort {
    DocumentoCondominio salvar(DocumentoCondominio doc);
    List<DocumentoCondominio> listarTodos();
    Optional<DocumentoCondominio> buscarPorId(UUID id);
}
