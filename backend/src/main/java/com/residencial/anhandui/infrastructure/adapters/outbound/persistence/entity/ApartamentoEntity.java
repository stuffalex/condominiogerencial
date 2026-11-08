package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "apartamentos")
public class ApartamentoEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "bloco", nullable = false)
    private String bloco;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "andar", nullable = false)
    private int andar;

    @Column(name = "vaga_garagem", nullable = false)
    private String vagaGaragem;

    @Column(name = "proprietario_nome")
    private String proprietarioNome;

    @Column(name = "proprietario_cpf")
    private String proprietarioCpf;

    @Column(name = "proprietario_telefone")
    private String proprietarioTelefone;

    @Column(name = "proprietario_email")
    private String proprietarioEmail;

    @Column(name = "proprietario_observacao")
    private String proprietarioObservacao;

    @Column(name = "morador_nome")
    private String moradorNome;

    @Column(name = "morador_cpf")
    private String moradorCpf;

    @Column(name = "morador_telefone")
    private String moradorTelefone;

    @Column(name = "morador_email")
    private String moradorEmail;

    @Column(name = "morador_observacao")
    private String moradorObservacao;

    public ApartamentoEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
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

    public String getProprietarioNome() {
        return proprietarioNome;
    }

    public void setProprietarioNome(String proprietarioNome) {
        this.proprietarioNome = proprietarioNome;
    }

    public String getProprietarioCpf() {
        return proprietarioCpf;
    }

    public void setProprietarioCpf(String proprietarioCpf) {
        this.proprietarioCpf = proprietarioCpf;
    }

    public String getProprietarioTelefone() {
        return proprietarioTelefone;
    }

    public void setProprietarioTelefone(String proprietarioTelefone) {
        this.proprietarioTelefone = proprietarioTelefone;
    }

    public String getProprietarioEmail() {
        return proprietarioEmail;
    }

    public void setProprietarioEmail(String proprietarioEmail) {
        this.proprietarioEmail = proprietarioEmail;
    }

    public String getProprietarioObservacao() {
        return proprietarioObservacao;
    }

    public void setProprietarioObservacao(String proprietarioObservacao) {
        this.proprietarioObservacao = proprietarioObservacao;
    }

    public String getMoradorNome() {
        return moradorNome;
    }

    public void setMoradorNome(String moradorNome) {
        this.moradorNome = moradorNome;
    }

    public String getMoradorCpf() {
        return moradorCpf;
    }

    public void setMoradorCpf(String moradorCpf) {
        this.moradorCpf = moradorCpf;
    }

    public String getMoradorTelefone() {
        return moradorTelefone;
    }

    public void setMoradorTelefone(String moradorTelefone) {
        this.moradorTelefone = moradorTelefone;
    }

    public String getMoradorEmail() {
        return moradorEmail;
    }

    public void setMoradorEmail(String moradorEmail) {
        this.moradorEmail = moradorEmail;
    }

    public String getMoradorObservacao() {
        return moradorObservacao;
    }

    public void setMoradorObservacao(String moradorObservacao) {
        this.moradorObservacao = moradorObservacao;
    }
}
