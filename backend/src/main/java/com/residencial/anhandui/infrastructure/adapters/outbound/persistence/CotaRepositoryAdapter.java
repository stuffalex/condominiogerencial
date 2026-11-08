package com.residencial.anhandui.infrastructure.adapters.outbound.persistence;

import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.StatusCota;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.CotaEntity;
import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository.SpringDataCotaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CotaRepositoryAdapter implements ICotaRepositoryPort {

    private final SpringDataCotaRepository springRepository;

    public CotaRepositoryAdapter(SpringDataCotaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public List<CotaCondominial> listarPorAnoMes(String anoMes) {
        return springRepository.findByAnoMes(anoMes).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CotaCondominial> listarPorApartamento(UUID apartamentoId) {
        return springRepository.findByApartamentoIdOrderByAnoMesDesc(apartamentoId.toString()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CotaCondominial> buscarPorId(UUID id) {
        if (id == null) return Optional.empty();
        return springRepository.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public Optional<CotaCondominial> buscarPorApartamentoEAnoMes(UUID apartamentoId, String anoMes) {
        return springRepository.findByApartamentoIdAndAnoMes(apartamentoId.toString(), anoMes).map(this::toDomain);
    }

    @Override
    public CotaCondominial salvar(CotaCondominial cota) {
        CotaEntity entity = toEntity(cota);
        CotaEntity salva = springRepository.save(entity);
        return toDomain(salva);
    }

    @Override
    public void salvarTodas(List<CotaCondominial> cotas) {
        List<CotaEntity> entities = cotas.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        springRepository.saveAll(entities);
    }

    @Override
    public boolean possuiPendenciasAte(UUID apartamentoId, LocalDate dataReferencia) {
        return springRepository.existsPendenciasAte(apartamentoId.toString(), dataReferencia);
    }

    private CotaCondominial toDomain(CotaEntity entity) {
        CotaCondominial cota = new CotaCondominial(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getApartamentoId()),
                entity.getAnoMes(),
                entity.getValorBase(),
                entity.getDataVencimento(),
                StatusCota.valueOf(entity.getStatus()),
                entity.getDataPagamento()
        );
        cota.setComprovanteFisicoArquivo(entity.getComprovanteFisicoArquivo());
        return cota;
    }

    private CotaEntity toEntity(CotaCondominial domain) {
        CotaEntity entity = new CotaEntity();
        entity.setId(domain.getId().toString());
        entity.setApartamentoId(domain.getApartamentoId().toString());
        entity.setAnoMes(domain.getAnoMes());
        entity.setValorBase(domain.getValorBase());
        entity.setDataVencimento(domain.getDataVencimento());
        entity.setStatus(domain.getStatus().name());
        entity.setDataPagamento(domain.getDataPagamento());
        entity.setComprovanteFisicoArquivo(domain.getComprovanteFisicoArquivo());
        return entity;
    }
}
