package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.util.Objects;

public class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String observacao;

    public Pessoa() {
    }

    public Pessoa(String nome, String cpf, String telefone, String email, String observacao) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.observacao = observacao;
    }

    public static Pessoa criarNaoInformado(String tipo) {
        return new Pessoa(
                "A Identificar (" + tipo + ")",
                "000.000.000-00",
                "Não informado",
                "nao-informado@anhandui.local",
                "Cadastro inicial padrão. Aguardando informações cadastrais."
        );
    }

    public boolean isNaoInformado() {
        return nome != null && nome.startsWith("A Identificar");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa pessoa)) return false;
        return Objects.equals(nome, pessoa.nome) && Objects.equals(cpf, pessoa.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cpf);
    }
}
