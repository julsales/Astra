package com.astra.compra.dominio.ingresso;

import com.astra.compra.dominio.cliente.ClienteId;
import com.astra.compra.dominio.sessao.AssentoId;
import com.astra.compra.dominio.sessao.SessaoId;

import java.util.Objects;

public class CompraServico {
    private final CompraRepositorio compraRepositorio;

    public CompraServico(CompraRepositorio compraRepositorio) {
        Objects.requireNonNull(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.compraRepositorio = compraRepositorio;
    }

    public Compra iniciarCompra(ClienteId clienteId) {
        Objects.requireNonNull(clienteId, "O ID do cliente não pode ser nulo");
        
        Compra compra = new Compra(clienteId);
        return compraRepositorio.salvar(compra);
    }

    public void adicionarIngresso(CompraId compraId, SessaoId sessaoId, AssentoId assentoId, TipoIngresso tipo) {
        Objects.requireNonNull(compraId, "O ID da compra não pode ser nulo");
        Objects.requireNonNull(sessaoId, "O ID da sessão não pode ser nulo");
        Objects.requireNonNull(assentoId, "O ID do assento não pode ser nulo");
        Objects.requireNonNull(tipo, "O tipo do ingresso não pode ser nulo");
        
        Compra compra = compraRepositorio.obter(compraId);
        Ingresso ingresso = new Ingresso(sessaoId, assentoId, tipo);
        
        compra.adicionarIngresso(ingresso);
        compraRepositorio.salvar(compra);
    }

    public void confirmarCompra(CompraId compraId) {
        Objects.requireNonNull(compraId, "O ID da compra não pode ser nulo");
        
        Compra compra = compraRepositorio.obter(compraId);
        compra.confirmarCompra();
        compraRepositorio.salvar(compra);
    }

    public void cancelarCompra(CompraId compraId) {
        Objects.requireNonNull(compraId, "O ID da compra não pode ser nulo");
        
        Compra compra = compraRepositorio.obter(compraId);
        compra.cancelarCompra();
        compraRepositorio.salvar(compra);
    }

    public Compra obterCompra(CompraId compraId) {
        Objects.requireNonNull(compraId, "O ID da compra não pode ser nulo");
        return compraRepositorio.obter(compraId);
    }
}
