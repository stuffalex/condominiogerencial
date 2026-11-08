package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.repository;

import com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity.DocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataDocumentoRepository extends JpaRepository<DocumentoEntity, String> {
    List<DocumentoEntity> findAllByOrderByDataUploadDesc();
}
