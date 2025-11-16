package com.astra.cinema.dominio.usuario;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.ClienteId;

public class ClienteService {
    private final ClienteRepositorio clienteRepositorio;

    public ClienteService(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = exigirNaoNulo(clienteRepositorio,
            "O repositório de clientes não pode ser nulo");
    }

    public void salvar(Cliente cliente) {
        clienteRepositorio.salvar(exigirNaoNulo(cliente, "O cliente não pode ser nulo"));
    }

    public Cliente obter(ClienteId clienteId) {
        exigirNaoNulo(clienteId, "O id do cliente não pode ser nulo");
        return clienteRepositorio.obterPorId(clienteId);
    }
}
