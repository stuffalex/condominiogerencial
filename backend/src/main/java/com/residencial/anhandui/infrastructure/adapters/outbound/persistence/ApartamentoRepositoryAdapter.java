package com.residencial.anhandui.infrastructure.adapters.outbound.persistence;

import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BlocoEnum;
import com.residencial.anhandui.domain.model.Pessoa;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.ApartamentoEntity;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository.SpringDataApartamentoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ApartamentoRepositoryAdapter implements IApartamentoRepositoryPort {

    private final SpringDataApartamentoRepository springRepository;

    public ApartamentoRepositoryAdapter(SpringDataApartamentoRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public List<Apartamento> listarTodos() {
        return springRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Apartamento> buscarPorId(UUID id) {
        if (id == null) return Optional.empty();
        return springRepository.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public Optional<Apartamento> buscarPorBlocoENumero(BlocoEnum bloco, String numero) {
        return springRepository.findByBlocoAndNumero(bloco.name(), numero).map(this::toDomain);
    }

    @Override
    public Apartamento salvar(Apartamento apartamento) {
        ApartamentoEntity entity = toEntity(apartamento);
        ApartamentoEntity salva = springRepository.save(entity);
        return toDomain(salva);
    }

    @Override
    public void salvarTodos(List<Apartamento> apartamentos) {
        List<ApartamentoEntity> entities = apartamentos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        springRepository.saveAll(entities);
    }

    @Override
    public long contar() {
        return springRepository.count();
    }

    private Apartamento toDomain(ApartamentoEntity entity) {
        Pessoa prop = new Pessoa(
                entity.getProprietarioNome(),
                entity.getProprietarioCpf(),
                entity.getProprietarioTelefone(),
                entity.getProprietarioEmail(),
                entity.getProprietarioObservacao()
        );
        Pessoa morador = new Pessoa(
                entity.getMoradorNome(),
                entity.getMoradorCpf(),
                entity.getMoradorTelefone(),
                entity.getMoradorEmail(),
                entity.getMoradorObservacao()
        );
        return new Apartamento(
                UUID.fromString(entity.getId()),
                BlocoEnum.valueOf(entity.getBloco()),
                entity.getNumero(),
                entity.getAndar(),
                entity.getVagaGaragem(),
                prop,
                morador
        );
    }

    private ApartamentoEntity toEntity(Apartamento domain) {
        ApartamentoEntity entity = new ApartamentoEntity();
        entity.setId(domain.getId().toString());
        entity.setBloco(domain.getBloco().name());
        entity.setNumero(domain.getNumero());
        entity.setAndar(domain.getAndar());
        entity.setVagaGaragem(domain.getVagaGaragem());

        if (domain.getProprietario() != null) {
            entity.setProprietarioNome(domain.getProprietario().getNome());
            entity.setProprietarioCpf(domain.getProprietario().getCpf());
            entity.setProprietarioTelefone(domain.getProprietario().getTelefone());
            entity.setProprietarioEmail(domain.getProprietario().getEmail());
            entity.setProprietarioObservacao(domain.getProprietario().getObservacao());
        }

        if (domain.getMorador() != null) {
            entity.setMoradorNome(domain.getMorador().getNome());
            entity.setMoradorCpf(domain.getMorador().getCpf());
            entity.setMoradorTelefone(domain.getMorador().getTelefone());
            entity.setMoradorEmail(domain.getMorador().getEmail());
            entity.setMoradorObservacao(domain.getMorador().getObservacao());
        }

        return entity;
    }
}
