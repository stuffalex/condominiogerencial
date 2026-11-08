package com.residencial.anhandui.domain.model;

public enum CategoriaDocumento {
    ATA_ASSEMBLEIA("Ata de Assembleia"),
    COMPROVANTE_PAGAMENTO("Comprovante de Pagamento"),
    CONTRATO_PRESTADOR("Contrato de Prestador de Serviço"),
    ORCAMENTO_OBRA_MURO("Orçamento para Obra do Muro"),
    NOTIFICACAO("Notificação"),
    OUTRO("Outro");

    private final String descricao;

    CategoriaDocumento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
