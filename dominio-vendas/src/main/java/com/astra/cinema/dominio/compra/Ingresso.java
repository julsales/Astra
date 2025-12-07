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
    private boolean utilizado;

    public Ingresso(IngressoId ingressoId, SessaoId sessaoId, AssentoId assentoId,
                   TipoIngresso tipo, StatusIngresso status, String qrCode) {
        this.ingressoId = exigirNaoNulo(ingressoId, "O id do ingresso não pode ser nulo");
        this.sessaoId = exigirNaoNulo(sessaoId, "O id da sessão não pode ser nulo");
        this.assentoId = exigirNaoNulo(assentoId, "O id do assento não pode ser nulo");
        this.tipo = exigirNaoNulo(tipo, "O tipo do ingresso não pode ser nulo");
        this.status = exigirNaoNulo(status, "O status do ingresso não pode ser nulo");
        this.qrCode = qrCode;
        this.utilizado = false;
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

    public boolean isUtilizado() {
        return utilizado;
    }

    public void utilizar() {
        exigirEstado(!utilizado, "O ingresso já foi utilizado");
        exigirEstado(status == StatusIngresso.VALIDADO, "O ingresso precisa estar validado para ser utilizado");
        this.utilizado = true;
    }

    public void validar() {
        exigirEstado(status != StatusIngresso.VALIDADO, "O ingresso já foi validado");
        this.status = StatusIngresso.VALIDADO;
    }

    public void cancelar() {
        exigirEstado(!utilizado, "Não é possível cancelar um ingresso já utilizado");
        this.status = StatusIngresso.ATIVO;
    }

    public void remarcarSessao(SessaoId novaSessaoId, AssentoId novoAssentoId) {
        exigirNaoNulo(novaSessaoId, "A nova sessão não pode ser nula");
        exigirNaoNulo(novoAssentoId, "O novo assento não pode ser nulo");
        // Permite remarcar ingressos em qualquer status (ATIVO ou VALIDADO)

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
