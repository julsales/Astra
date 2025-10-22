package com.astra.cinema.dominio.compra;

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
        if (compraId == null) {
            throw new IllegalArgumentException("O id da compra não pode ser nulo");
        }
        if (clienteId == null) {
            throw new IllegalArgumentException("O id do cliente não pode ser nulo");
        }
        if (ingressos == null || ingressos.isEmpty()) {
            throw new IllegalArgumentException("A compra deve ter pelo menos um ingresso");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }
        
        this.compraId = compraId;
        this.clienteId = clienteId;
        this.ingressos = new ArrayList<>(ingressos);
        this.pagamentoId = pagamentoId;
        this.status = status;
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
        if (status != StatusCompra.PENDENTE) {
            throw new IllegalStateException("Apenas compras pendentes podem ser confirmadas");
        }
        if (pagamentoId == null) {
            throw new IllegalStateException("A compra deve ter um pagamento associado");
        }
        this.status = StatusCompra.CONFIRMADA;
        for (Ingresso ingresso : ingressos) {
            ingresso.setStatus(StatusIngresso.VALIDO);
        }
    }

    public void cancelar() {
        if (status == StatusCompra.CANCELADA) {
            throw new IllegalStateException("A compra já está cancelada");
        }
        
        // Verifica se algum ingresso já foi utilizado
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getStatus() == StatusIngresso.UTILIZADO) {
                throw new IllegalStateException("Não é possível cancelar uma compra com ingresso já utilizado");
            }
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
