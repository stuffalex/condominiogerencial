package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Apartamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private BlocoEnum bloco;
    private String numero;
    private int andar; // 0 = Térreo, 1 = 1º Andar, 2 = 2º Andar
    private String vagaGaragem;
    private Pessoa proprietario;
    private Pessoa morador;

    public Apartamento() {
    }

    public Apartamento(UUID id, BlocoEnum bloco, String numero, int andar, String vagaGaragem, Pessoa proprietario, Pessoa morador) {
        this.id = id != null ? id : UUID.randomUUID();
        this.bloco = bloco;
        this.numero = numero;
        this.andar = andar;
        this.vagaGaragem = vagaGaragem;
        this.proprietario = proprietario != null ? proprietario : Pessoa.criarNaoInformado("Proprietário");
        this.morador = morador != null ? morador : Pessoa.criarNaoInformado("Morador");
    }

    public String getDescricaoCompleta() {
        return bloco.getDescricao() + " - Apto " + numero + " (Garagem " + vagaGaragem + ")";
    }

    public String getDescricaoAndar() {
        return switch (andar) {
            case 0 -> "Térreo";
            case 1 -> "1º Andar";
            case 2 -> "2º Andar";
            default -> andar + "º Andar";
        };
    }

    public boolean isDadosPendentes() {
        return (proprietario != null && proprietario.isNaoInformado()) ||
               (morador != null && morador.isNaoInformado());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BlocoEnum getBloco() {
        return bloco;
    }

    public void setBloco(BlocoEnum bloco) {
        this.bloco = bloco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getAndar() {
        return andar;
    }

    public void setAndar(int andar) {
        this.andar = andar;
    }

    public String getVagaGaragem() {
        return vagaGaragem;
    }

    public void setVagaGaragem(String vagaGaragem) {
        this.vagaGaragem = vagaGaragem;
    }

    public Pessoa getProprietario() {
        return proprietario;
    }

    public void setProprietario(Pessoa proprietario) {
        this.proprietario = proprietario;
    }

    public Pessoa getMorador() {
        return morador;
    }

    public void setMorador(Pessoa morador) {
        this.morador = morador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Apartamento that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
