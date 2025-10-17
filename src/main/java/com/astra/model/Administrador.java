package com.astra.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    @Column(name = "nivel_acesso")
    private Integer nivelAcesso = 3; // 1=Baixo, 2=Médio, 3=Alto

    @Column(name = "departamento")
    private String departamento;

    // Construtores
    public Administrador() {
        super();
    }

    public Administrador(String email, String senha, String nome, Integer nivelAcesso) {
        super(email, senha, nome);
        this.nivelAcesso = nivelAcesso;
    }

    // Getters e Setters
    public Integer getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(Integer nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
