package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository;

import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.ApartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataApartamentoRepository extends JpaRepository<ApartamentoEntity, String> {
    Optional<ApartamentoEntity> findByBlocoAndNumero(String bloco, String numero);
}
