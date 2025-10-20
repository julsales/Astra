package com.astra.compra.dominio.ingresso;

import com.astra.compra.dominio.sessao.AssentoId;
import com.astra.compra.dominio.sessao.SessaoId;

import java.util.Objects;

public class Ingresso implements Cloneable {
    private final IngressoId id;
    private final SessaoId sessaoId;
    private final AssentoId assentoId;
    private TipoIngresso tipo;
    private StatusIngresso status;
    private String qrCode;

    // Construtor para criação (sem ID)
    public Ingresso(SessaoId sessaoId, AssentoId assentoId, TipoIngresso tipo) {
        Objects.requireNonNull(sessaoId, "O ID da sessão não pode ser nulo");
        Objects.requireNonNull(assentoId, "O ID do assento não pode ser nulo");
        Objects.requireNonNull(tipo, "O tipo do ingresso não pode ser nulo");
        
        this.id = null;
        this.sessaoId = sessaoId;
        this.assentoId = assentoId;
        this.tipo = tipo;
        this.status = StatusIngresso.PENDENTE;
        this.qrCode = null;
    }

    // Construtor para persistência (com ID)
    public Ingresso(IngressoId id, SessaoId sessaoId, AssentoId assentoId, TipoIngresso tipo) {
        Objects.requireNonNull(id, "O ID não pode ser nulo");
        Objects.requireNonNull(sessaoId, "O ID da sessão não pode ser nulo");
        Objects.requireNonNull(assentoId, "O ID do assento não pode ser nulo");
        Objects.requireNonNull(tipo, "O tipo do ingresso não pode ser nulo");
        
        this.id = id;
        this.sessaoId = sessaoId;
        this.assentoId = assentoId;
        this.tipo = tipo;
        this.status = StatusIngresso.PENDENTE;
        this.qrCode = null;
    }

    public IngressoId getId() {
        return id;
    }

    public SessaoId getSessaoId() {
        return sessaoId;
    }

    public AssentoId getAssentoId() {
        return assentoId;
    }

    public TipoIngresso getTipo() {
        return tipo;
    }

    public StatusIngresso getStatus() {
        return status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public boolean valido() {
        return status == StatusIngresso.VALIDO;
    }

    public boolean cancelado() {
        return status == StatusIngresso.CANCELADO;
    }

    public boolean utilizado() {
        return status == StatusIngresso.UTILIZADO;
    }

    public void validar() {
        if (status != StatusIngresso.PENDENTE) {
            throw new IllegalStateException("Apenas ingressos pendentes podem ser validados");
        }
        this.status = StatusIngresso.VALIDO;
        gerarQrCode();
    }

    public void cancelar() {
        if (status == StatusIngresso.UTILIZADO) {
            throw new IllegalStateException("Ingressos utilizados não podem ser cancelados");
        }
        if (status == StatusIngresso.CANCELADO) {
            throw new IllegalStateException("O ingresso já está cancelado");
        }
        this.status = StatusIngresso.CANCELADO;
    }

    public void utilizar() {
        if (status != StatusIngresso.VALIDO) {
            throw new IllegalStateException("Apenas ingressos válidos podem ser utilizados");
        }
        this.status = StatusIngresso.UTILIZADO;
    }

    private void gerarQrCode() {
        // Gera um QR Code simples baseado no ID do ingresso
        this.qrCode = "QR-" + (id != null ? id.getValor() : "TEMP") + "-" + System.currentTimeMillis();
    }

    @Override
    public Ingresso clone() {
        try {
            return (Ingresso) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "id=" + id +
                ", assento=" + assentoId +
                ", tipo=" + tipo +
                ", status=" + status +
                '}';
    }
}
