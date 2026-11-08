package com.residencial.anhandui.domain.model;

public enum BlocoEnum {
    BLOCO_G("Bloco G"),
    BLOCO_H("Bloco H");

    private final String descricao;

    BlocoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
