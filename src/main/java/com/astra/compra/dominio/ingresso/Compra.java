package com.astra.compra.dominio.ingresso;

import com.astra.compra.dominio.cliente.ClienteId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Compra implements Cloneable {
    private final CompraId id;
    private final ClienteId clienteId;
    private final List<Ingresso> ingressos;
    private StatusCompra status;

    // Construtor para criação (sem ID)
    public Compra(ClienteId clienteId) {
        this.id = null;
        Objects.requireNonNull(clienteId, "O ID do cliente não pode ser nulo");
        this.clienteId = clienteId;
        this.ingressos = new ArrayList<>();
        this.status = StatusCompra.PENDENTE;
    }

    // Construtor para persistência (com ID)
    public Compra(CompraId id, ClienteId clienteId) {
        Objects.requireNonNull(id, "O ID não pode ser nulo");
        Objects.requireNonNull(clienteId, "O ID do cliente não pode ser nulo");
        this.id = id;
        this.clienteId = clienteId;
        this.ingressos = new ArrayList<>();
        this.status = StatusCompra.PENDENTE;
    }

    public CompraId getId() {
        return id;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public List<Ingresso> getIngressos() {
        return Collections.unmodifiableList(ingressos);
    }

    public StatusCompra getStatus() {
        return status;
    }

    public void adicionarIngresso(Ingresso ingresso) {
        Objects.requireNonNull(ingresso, "O ingresso não pode ser nulo");
        
        if (status != StatusCompra.PENDENTE) {
            throw new IllegalStateException("Não é possível adicionar ingressos a uma compra que não está pendente");
        }
        
        ingressos.add(ingresso);
    }

    public void confirmarCompra() {
        if (status != StatusCompra.PENDENTE) {
            throw new IllegalStateException("Apenas compras pendentes podem ser confirmadas");
        }
        
        if (ingressos.isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar uma compra sem ingressos");
        }
        
        // Valida todos os ingressos
        for (Ingresso ingresso : ingressos) {
            ingresso.validar();
        }
        
        this.status = StatusCompra.CONFIRMADA;
    }

    public void cancelarCompra() {
        if (status == StatusCompra.CANCELADA) {
            throw new IllegalStateException("A compra já está cancelada");
        }
        
        // Verifica se algum ingresso já foi utilizado
        for (Ingresso ingresso : ingressos) {
            if (ingresso.utilizado()) {
                throw new IllegalStateException("Não é possível cancelar uma compra com ingressos já utilizados");
            }
        }
        
        // Cancela todos os ingressos
        for (Ingresso ingresso : ingressos) {
            if (!ingresso.cancelado()) {
                ingresso.cancelar();
            }
        }
        
        this.status = StatusCompra.CANCELADA;
    }

    public boolean pendente() {
        return status == StatusCompra.PENDENTE;
    }

    public boolean confirmada() {
        return status == StatusCompra.CONFIRMADA;
    }

    public boolean cancelada() {
        return status == StatusCompra.CANCELADA;
    }

    @Override
    public Compra clone() {
        try {
            Compra cloned = (Compra) super.clone();
            // Note: ingressos é compartilhado, mas é imutável para o mundo externo
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "Compra{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", status=" + status +
                ", ingressos=" + ingressos.size() +
                '}';
    }
}
