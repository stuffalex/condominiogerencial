package com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RegistrarPagamentoPeriodoRequest(
        UUID apartamentoId,
        String anoMesInicio,
        String anoMesFim,
        BigDecimal valorPorMes,
        String recebidoPor,
        String observacao
) {}
