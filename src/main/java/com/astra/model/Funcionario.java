package com.astra.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FUNCIONARIO")
public class Funcionario extends Usuario {

    @Column(name = "matricula", unique = true)
    private String matricula;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "setor")
    private String setor;

    @Column(name = "salario")
    private Double salario;

    // Construtores
    public Funcionario() {
        super();
    }

    public Funcionario(String email, String senha, String nome, String matricula, String cargo) {
        super(email, senha, nome);
        this.matricula = matricula;
        this.cargo = cargo;
    }

    // Getters e Setters
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }
}
