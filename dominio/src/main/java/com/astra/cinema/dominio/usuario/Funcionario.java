package com.astra.cinema.dominio.usuario;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

public class Funcionario implements Cloneable {
    private String nome;
    private Cargo cargo;

    public Funcionario(String nome, Cargo cargo) {
        this.nome = exigirTexto(nome, "O nome não pode ser nulo ou vazio");
        this.cargo = exigirNaoNulo(cargo, "O cargo não pode ser nulo");
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
