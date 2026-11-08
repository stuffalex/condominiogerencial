package com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto;

public record AtualizarMoradorRequest(
        String proprietarioNome,
        String proprietarioCpf,
        String proprietarioTelefone,
        String proprietarioEmail,
        String proprietarioObservacao,
        String moradorNome,
        String moradorCpf,
        String moradorTelefone,
        String moradorEmail,
        String moradorObservacao
) {}
