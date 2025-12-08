package com.astra.cinema.dominio.usuario;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.ClienteId;

public class Cliente implements Cloneable {
    private final ClienteId clienteId;
    private String nome;
    private String email;
    private String cpf;

    public Cliente(ClienteId clienteId, String nome, String email, String cpf) {
        this.clienteId = exigirNaoNulo(clienteId, "O id do cliente não pode ser nulo");
        this.nome = exigirTexto(nome, "O nome não pode ser nulo ou vazio");
        this.email = exigirTexto(email, "O e-mail não pode ser nulo ou vazio");
        this.cpf = cpf; // CPF pode ser null inicialmente, mas será validado no use case
    }

    // Construtor para compatibilidade (sem CPF)
    public Cliente(ClienteId clienteId, String nome, String email) {
        this(clienteId, nome, email, null);
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public Cliente clone() {
        try {
            return (Cliente) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
