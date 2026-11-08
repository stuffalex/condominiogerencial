package com.residencial.anhandui.domain.ports.output;

import com.residencial.anhandui.domain.model.CotaCondominial;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICotaRepositoryPort {
    List<CotaCondominial> listarPorAnoMes(String anoMes);
    List<CotaCondominial> listarPorApartamento(UUID apartamentoId);
    Optional<CotaCondominial> buscarPorId(UUID id);
    Optional<CotaCondominial> buscarPorApartamentoEAnoMes(UUID apartamentoId, String anoMes);
    CotaCondominial salvar(CotaCondominial cota);
    void salvarTodas(List<CotaCondominial> cotas);
    boolean possuiPendenciasAte(UUID apartamentoId, LocalDate dataReferencia);
}
