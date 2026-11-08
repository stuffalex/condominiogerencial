package com.residencial.anhandui.infrastructure.adapters.outbound.persistence;

import com.residencial.anhandui.domain.model.PagamentoDinheiro;
import com.residencial.anhandui.domain.ports.output.IPagamentoRepositoryPort;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.PagamentoEntity;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository.SpringDataPagamentoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PagamentoRepositoryAdapter implements IPagamentoRepositoryPort {

    private final SpringDataPagamentoRepository springRepository;

    public PagamentoRepositoryAdapter(SpringDataPagamentoRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public PagamentoDinheiro salvar(PagamentoDinheiro pagamento) {
        PagamentoEntity entity = toEntity(pagamento);
        PagamentoEntity salva = springRepository.save(entity);
        return toDomain(salva);
    }

    @Override
    public List<PagamentoDinheiro> listarTodos() {
        return springRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagamentoDinheiro> listarPorApartamento(UUID apartamentoId) {
        return springRepository.findByApartamentoIdOrderByDataHoraDesc(apartamentoId.toString()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long obterProximoNumeroRecibo() {
        return springRepository.obterProximoNumeroRecibo();
    }

    private PagamentoDinheiro toDomain(PagamentoEntity entity) {
        return new PagamentoDinheiro(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getCotaId()),
                UUID.fromString(entity.getApartamentoId()),
                entity.getNumeroRecibo(),
                entity.getValorPago(),
                entity.getDataHora(),
                entity.getRecebidoPor(),
                entity.getObservacao()
        );
    }

    private PagamentoEntity toEntity(PagamentoDinheiro domain) {
        PagamentoEntity entity = new PagamentoEntity();
        entity.setId(domain.getId().toString());
        entity.setCotaId(domain.getCotaId().toString());
        entity.setApartamentoId(domain.getApartamentoId().toString());
        entity.setNumeroRecibo(domain.getNumeroRecibo());
        entity.setValorPago(domain.getValorPago());
        entity.setDataHora(domain.getDataHora());
        entity.setRecebidoPor(domain.getRecebidoPor());
        entity.setObservacao(domain.getObservacao());
        return entity;
    }
}
