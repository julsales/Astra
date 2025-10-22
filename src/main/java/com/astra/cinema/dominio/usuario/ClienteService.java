package com.astra.cinema.dominio.usuario;

import com.astra.cinema.dominio.comum.ClienteId;

public class ClienteService {
    private final ClienteRepositorio clienteRepositorio;

    public ClienteService(ClienteRepositorio clienteRepositorio) {
        if (clienteRepositorio == null) {
            throw new IllegalArgumentException("O repositório de clientes não pode ser nulo");
        }
        
        this.clienteRepositorio = clienteRepositorio;
    }

    public void salvar(Cliente cliente) {
        clienteRepositorio.salvar(cliente);
    }

    public Cliente obter(ClienteId clienteId) {
        return clienteRepositorio.obterPorId(clienteId);
    }
}
