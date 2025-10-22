package com.astra.cinema.dominio.usuario;

public class Funcionario implements Cloneable {
    private String nome;
    private Cargo cargo;

    public Funcionario(String nome, Cargo cargo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou vazio");
        }
        if (cargo == null) {
            throw new IllegalArgumentException("O cargo não pode ser nulo");
        }
        
        this.nome = nome;
        this.cargo = cargo;
    }

    public String getNome() {
        return nome;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public boolean isGerente() {
        return cargo == Cargo.GERENTE;
    }

    @Override
    public Funcionario clone() {
        try {
            return (Funcionario) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
