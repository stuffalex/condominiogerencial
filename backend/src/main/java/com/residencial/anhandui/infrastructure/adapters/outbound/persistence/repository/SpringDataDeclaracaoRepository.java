package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository;

import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.DeclaracaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataDeclaracaoRepository extends JpaRepository<DeclaracaoEntity, String> {
    Optional<DeclaracaoEntity> findByCodigoAutenticidade(String codigoAutenticidade);
}
