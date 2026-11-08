package com.residencial.anhandui.domain.model;

public enum StatusCota {
    PENDENTE("Pendente"),
    PAGA("Paga"),
    ATRASADA("Em Atraso");

    private final String descricao;

    StatusCota(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
