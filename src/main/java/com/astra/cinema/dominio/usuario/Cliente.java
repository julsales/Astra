package com.astra.cinema.dominio.usuario;

import com.astra.cinema.dominio.comum.ClienteId;

public class Cliente implements Cloneable {
    private final ClienteId clienteId;
    private String nome;
    private String email;

    public Cliente(ClienteId clienteId, String nome, String email) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O id do cliente não pode ser nulo");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou vazio");
        }
        
        this.clienteId = clienteId;
        this.nome = nome;
        this.email = email;
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

    @Override
    public Cliente clone() {
        try {
            return (Cliente) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
