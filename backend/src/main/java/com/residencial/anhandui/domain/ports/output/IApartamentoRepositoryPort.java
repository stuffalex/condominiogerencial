package com.residencial.anhandui.domain.ports.output;

import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BlocoEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IApartamentoRepositoryPort {
    List<Apartamento> listarTodos();
    Optional<Apartamento> buscarPorId(UUID id);
    Optional<Apartamento> buscarPorBlocoENumero(BlocoEnum bloco, String numero);
    Apartamento salvar(Apartamento apartamento);
    void salvarTodos(List<Apartamento> apartamentos);
    long contar();
}
