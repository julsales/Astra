package com.astra.cinema.dominio.compra;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirColecaoNaoVazia;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;

import com.astra.cinema.dominio.comum.*;
import java.util.ArrayList;
import java.util.List;

public class Compra implements Cloneable {
    private final CompraId compraId;
    private ClienteId clienteId;
    private List<Ingresso> ingressos;
    private PagamentoId pagamentoId;
    private StatusCompra status;

    public Compra(CompraId compraId, ClienteId clienteId, List<Ingresso> ingressos, 
                  PagamentoId pagamentoId, StatusCompra status) {
        this.compraId = exigirNaoNulo(compraId, "O id da compra não pode ser nulo");
        this.clienteId = exigirNaoNulo(clienteId, "O id do cliente não pode ser nulo");
        this.ingressos = new ArrayList<>(exigirColecaoNaoVazia(ingressos, "A compra deve ter pelo menos um ingresso"));
        this.pagamentoId = pagamentoId;
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
    }

    public CompraId getCompraId() {
        return compraId;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public List<Ingresso> getIngressos() {
        return new ArrayList<>(ingressos);
    }

    public PagamentoId getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(PagamentoId pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public StatusCompra getStatus() {
        return status;
    }

    public void confirmar() {
        exigirEstado(status == StatusCompra.PENDENTE, "Apenas compras pendentes podem ser confirmadas");
        exigirEstado(pagamentoId != null, "A compra deve ter um pagamento associado");
        this.status = StatusCompra.CONFIRMADA;
        for (Ingresso ingresso : ingressos) {
            ingresso.setStatus(StatusIngresso.VALIDO);
        }
    }

    public void cancelar() {
        exigirEstado(status != StatusCompra.CANCELADA, "A compra já está cancelada");
        
        // Verifica se algum ingresso já foi utilizado
        for (Ingresso ingresso : ingressos) {
            exigirEstado(ingresso.getStatus() != StatusIngresso.UTILIZADO,
                "Não é possível cancelar uma compra com ingresso já utilizado");
        }
        
        this.status = StatusCompra.CANCELADA;
        for (Ingresso ingresso : ingressos) {
            ingresso.cancelar();
        }
    }

    @Override
    public Compra clone() {
        try {
            Compra cloned = (Compra) super.clone();
            cloned.ingressos = new ArrayList<>();
            for (Ingresso ingresso : this.ingressos) {
                cloned.ingressos.add(ingresso.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
