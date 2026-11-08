package com.residencial.anhandui.domain.ports.output;

import com.residencial.anhandui.domain.model.PagamentoDinheiro;

import java.util.List;
import java.util.UUID;

public interface IPagamentoRepositoryPort {
    PagamentoDinheiro salvar(PagamentoDinheiro pagamento);
    List<PagamentoDinheiro> listarTodos();
    List<PagamentoDinheiro> listarPorApartamento(UUID apartamentoId);
    long obterProximoNumeroRecibo();
}
