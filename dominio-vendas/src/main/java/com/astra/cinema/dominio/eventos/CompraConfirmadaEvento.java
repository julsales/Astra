package com.astra.cinema.dominio.eventos;

import com.astra.cinema.dominio.comum.CompraId;
import com.astra.cinema.dominio.comum.ClienteId;

import java.util.Date;

/**
 * PADRÃO OBSERVER - Evento Concreto
 * Evento publicado quando uma compra é confirmada.
 */
public class CompraConfirmadaEvento {

    private final CompraId compraId;
    private final ClienteId clienteId;
    private final int quantidadeIngressos;
    private final Date dataHora;

    public CompraConfirmadaEvento(CompraId compraId, ClienteId clienteId, int quantidadeIngressos) {
        this.compraId = compraId;
        this.clienteId = clienteId;
        this.quantidadeIngressos = quantidadeIngressos;
        this.dataHora = new Date();
    }

    public CompraId getCompraId() {
        return compraId;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public int getQuantidadeIngressos() {
        return quantidadeIngressos;
    }

    public Date getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "CompraConfirmadaEvento{" +
                "compraId=" + compraId.getId() +
                ", clienteId=" + clienteId.getId() +
                ", quantidadeIngressos=" + quantidadeIngressos +
                ", dataHora=" + dataHora +
                '}';
    }
}
