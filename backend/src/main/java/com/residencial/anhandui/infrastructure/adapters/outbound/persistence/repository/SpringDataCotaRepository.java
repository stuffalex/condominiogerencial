package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository;

import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.CotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataCotaRepository extends JpaRepository<CotaEntity, String> {
    List<CotaEntity> findByAnoMes(String anoMes);
    List<CotaEntity> findByApartamentoIdOrderByAnoMesDesc(String apartamentoId);
    Optional<CotaEntity> findByApartamentoIdAndAnoMes(String apartamentoId, String anoMes);

    @Query("SELECT COUNT(c) > 0 FROM CotaEntity c WHERE c.apartamentoId = :apartamentoId AND c.status <> 'PAGA' AND c.dataVencimento <= :dataReferencia")
    boolean existsPendenciasAte(@Param("apartamentoId") String apartamentoId, @Param("dataReferencia") LocalDate dataReferencia);
}
