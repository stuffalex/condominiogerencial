package com.residencial.anhandui.infrastructure.adapters.outbound.persistence;

import com.residencial.anhandui.domain.model.BlocoEnum;
import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;
import com.residencial.anhandui.domain.ports.output.IDeclaracaoRepositoryPort;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.DeclaracaoEntity;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository.SpringDataDeclaracaoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DeclaracaoRepositoryAdapter implements IDeclaracaoRepositoryPort {

    private final SpringDataDeclaracaoRepository springRepository;

    public DeclaracaoRepositoryAdapter(SpringDataDeclaracaoRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public DeclaracaoNegativaDebito salvar(DeclaracaoNegativaDebito dnd) {
        DeclaracaoEntity entity = toEntity(dnd);
        DeclaracaoEntity salva = springRepository.save(entity);
        return toDomain(salva);
    }

    @Override
    public Optional<DeclaracaoNegativaDebito> buscarPorCodigo(String codigo) {
        return springRepository.findByCodigoAutenticidade(codigo).map(this::toDomain);
    }

    @Override
    public List<DeclaracaoNegativaDebito> listarTodas() {
        return springRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private DeclaracaoNegativaDebito toDomain(DeclaracaoEntity entity) {
        DeclaracaoNegativaDebito dnd = new DeclaracaoNegativaDebito(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getApartamentoId()),
                BlocoEnum.valueOf(entity.getBloco()),
                entity.getNumeroApartamento(),
                entity.getNomeTitular(),
                entity.getDataEmissao(),
                entity.getValidadeAte(),
                entity.getObservacao()
        );
        dnd.setCodigoAutenticidade(entity.getCodigoAutenticidade());
        return dnd;
    }

    private DeclaracaoEntity toEntity(DeclaracaoNegativaDebito domain) {
        DeclaracaoEntity entity = new DeclaracaoEntity();
        entity.setId(domain.getId().toString());
        entity.setCodigoAutenticidade(domain.getCodigoAutenticidade());
        entity.setApartamentoId(domain.getApartamentoId().toString());
        entity.setBloco(domain.getBloco().name());
        entity.setNumeroApartamento(domain.getNumeroApartamento());
        entity.setNomeTitular(domain.getNomeTitular());
        entity.setDataEmissao(domain.getDataEmissao());
        entity.setValidadeAte(domain.getValidadeAte());
        entity.setObservacao(domain.getObservacao());
        return entity;
    }
}
