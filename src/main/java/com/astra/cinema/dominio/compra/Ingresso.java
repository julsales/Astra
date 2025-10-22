package com.astra.cinema.dominio.compra;

import com.astra.cinema.dominio.comum.*;

public class Ingresso implements Cloneable {
    private final IngressoId ingressoId;
    private SessaoId sessaoId;
    private AssentoId assentoId;
    private TipoIngresso tipo;
    private StatusIngresso status;
    private String qrCode;

    public Ingresso(IngressoId ingressoId, SessaoId sessaoId, AssentoId assentoId, 
                   TipoIngresso tipo, StatusIngresso status, String qrCode) {
        if (ingressoId == null) {
            throw new IllegalArgumentException("O id do ingresso não pode ser nulo");
        }
        if (sessaoId == null) {
            throw new IllegalArgumentException("O id da sessão não pode ser nulo");
        }
        if (assentoId == null) {
            throw new IllegalArgumentException("O id do assento não pode ser nulo");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do ingresso não pode ser nulo");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status do ingresso não pode ser nulo");
        }
        
        this.ingressoId = ingressoId;
        this.sessaoId = sessaoId;
        this.assentoId = assentoId;
        this.tipo = tipo;
        this.status = status;
        this.qrCode = qrCode;
    }

    public IngressoId getIngressoId() {
        return ingressoId;
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

    public void setStatus(StatusIngresso status) {
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void utilizar() {
        if (status == StatusIngresso.UTILIZADO) {
            throw new IllegalStateException("O ingresso já foi utilizado");
        }
        if (status == StatusIngresso.CANCELADO) {
            throw new IllegalStateException("O ingresso está cancelado");
        }
        this.status = StatusIngresso.UTILIZADO;
    }

    public void cancelar() {
        if (status == StatusIngresso.UTILIZADO) {
            throw new IllegalStateException("Não é possível cancelar um ingresso já utilizado");
        }
        this.status = StatusIngresso.CANCELADO;
    }

    @Override
    public Ingresso clone() {
        try {
            return (Ingresso) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
