package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository;

import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataPagamentoRepository extends JpaRepository<PagamentoEntity, String> {
    List<PagamentoEntity> findByApartamentoIdOrderByDataHoraDesc(String apartamentoId);

    @Query("SELECT COALESCE(MAX(p.numeroRecibo), 0) + 1 FROM PagamentoEntity p")
    long obterProximoNumeroRecibo();
}
