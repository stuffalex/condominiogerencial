package com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto;

import java.util.UUID;

public record EmitirDndRequest(
        UUID apartamentoId,
        String solicitante,
        String observacao
) {}
