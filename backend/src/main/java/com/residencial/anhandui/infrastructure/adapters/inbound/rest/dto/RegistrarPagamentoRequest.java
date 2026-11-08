package com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RegistrarPagamentoRequest(
        UUID cotaId,
        BigDecimal valorPago,
        String recebidoPor,
        String observacao
) {}
