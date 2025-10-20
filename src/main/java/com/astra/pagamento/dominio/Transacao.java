package com.astra.pagamento.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transacao implements Cloneable {
    private final String codigoTransacao;
    private final LocalDateTime data;
    private final String gateway;
    private final String respostaGateway;

    public Transacao(String codigoTransacao, LocalDateTime data, String gateway, String respostaGateway) {
        Objects.requireNonNull(codigoTransacao, "O código da transação não pode ser nulo");
        Objects.requireNonNull(data, "A data não pode ser nula");
        Objects.requireNonNull(gateway, "O gateway não pode ser nulo");
        
        this.codigoTransacao = codigoTransacao;
        this.data = data;
        this.gateway = gateway;
        this.respostaGateway = respostaGateway;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getGateway() {
        return gateway;
    }

    public String getRespostaGateway() {
        return respostaGateway;
    }

    @Override
    public Transacao clone() {
        try {
            return (Transacao) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Erro ao clonar transação", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(codigoTransacao, transacao.codigoTransacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoTransacao);
    }
}
