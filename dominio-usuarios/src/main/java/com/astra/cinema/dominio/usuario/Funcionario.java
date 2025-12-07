package com.astra.cinema.dominio.usuario;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.FuncionarioId;

public class Funcionario implements Cloneable {
    private FuncionarioId funcionarioId;
    private String nome;
    private Cargo cargo;

    public Funcionario(String nome, Cargo cargo) {
        this(null, nome, cargo);
    }

    public Funcionario(FuncionarioId funcionarioId, String nome, Cargo cargo) {
        this.funcionarioId = funcionarioId;
        this.nome = exigirTexto(nome, "O nome não pode ser nulo ou vazio");
        this.cargo = exigirNaoNulo(cargo, "O cargo não pode ser nulo");
    }

    public FuncionarioId getFuncionarioId() {
        return funcionarioId;
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
            Funcionario clone = (Funcionario) super.clone();
            clone.funcionarioId = this.funcionarioId;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
