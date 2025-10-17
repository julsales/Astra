package com.astra.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "data_nascimento")
    private String dataNascimento;

    @Column(name = "pontos_fidelidade")
    private Integer pontosFidelidade = 0;

    // Construtores
    public Cliente() {
        super();
    }

    public Cliente(String email, String senha, String nome, String cpf) {
        super(email, senha, nome);
        this.cpf = cpf;
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(Integer pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }

    public void adicionarPontos(Integer pontos) {
        this.pontosFidelidade += pontos;
    }
}
