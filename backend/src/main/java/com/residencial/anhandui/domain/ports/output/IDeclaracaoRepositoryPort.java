package com.residencial.anhandui.domain.ports.output;

import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;

import java.util.List;
import java.util.Optional;

public interface IDeclaracaoRepositoryPort {
    DeclaracaoNegativaDebito salvar(DeclaracaoNegativaDebito dnd);
    Optional<DeclaracaoNegativaDebito> buscarPorCodigo(String codigo);
    List<DeclaracaoNegativaDebito> listarTodas();
}
