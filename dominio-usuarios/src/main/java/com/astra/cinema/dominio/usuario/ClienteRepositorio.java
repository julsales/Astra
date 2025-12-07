package com.astra.cinema.dominio.usuario;

import com.astra.cinema.dominio.comum.ClienteId;

public interface ClienteRepositorio {
    void salvar(Cliente cliente);
    Cliente obterPorId(ClienteId clienteId);
    Cliente obterPorEmail(String email);
}
