package com.astra.cinema.dominio.compra;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;

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
        this.ingressoId = exigirNaoNulo(ingressoId, "O id do ingresso não pode ser nulo");
        this.sessaoId = exigirNaoNulo(sessaoId, "O id da sessão não pode ser nulo");
        this.assentoId = exigirNaoNulo(assentoId, "O id do assento não pode ser nulo");
        this.tipo = exigirNaoNulo(tipo, "O tipo do ingresso não pode ser nulo");
        this.status = exigirNaoNulo(status, "O status do ingresso não pode ser nulo");
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
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
    }

    public String getQrCode() {
        return qrCode;
    }

    public void utilizar() {
        exigirEstado(status != StatusIngresso.UTILIZADO, "O ingresso já foi utilizado");
        exigirEstado(status != StatusIngresso.CANCELADO, "O ingresso está cancelado");
        this.status = StatusIngresso.UTILIZADO;
    }

    public void cancelar() {
        exigirEstado(status != StatusIngresso.UTILIZADO,
            "Não é possível cancelar um ingresso já utilizado");
        this.status = StatusIngresso.CANCELADO;
    }

    public void remarcarSessao(SessaoId novaSessaoId, AssentoId novoAssentoId) {
        exigirNaoNulo(novaSessaoId, "A nova sessão não pode ser nula");
        exigirNaoNulo(novoAssentoId, "O novo assento não pode ser nulo");
        exigirEstado(status != StatusIngresso.UTILIZADO, "Não é possível remarcar um ingresso já utilizado");
        exigirEstado(status != StatusIngresso.CANCELADO, "Não é possível remarcar um ingresso cancelado");
        
        this.sessaoId = novaSessaoId;
        this.assentoId = novoAssentoId;
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
