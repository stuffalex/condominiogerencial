package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.exception.DomainException;
import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.exception.InadimplenciaDetectadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InadimplenciaDetectadaException.class)
    public ResponseEntity<Map<String, Object>> handleInadimplencia(InadimplenciaDetectadaException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.PRECONDITION_FAILED.value(),
                "erro", "Bloqueio de Emissão de DND - Inadimplência",
                "mensagem", ex.getMessage()
        ));
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleNaoEncontrado(EntidadeNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "erro", "Não Encontrado",
                "mensagem", ex.getMessage()
        ));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "erro", "Regra de Negócio Violada",
                "mensagem", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "erro", "Erro Interno no Servidor",
                "mensagem", ex.getMessage() != null ? ex.getMessage() : "Erro inesperado"
        ));
    }
}
