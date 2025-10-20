package com.astra.pagamento.dominio;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MetodoPagamento implements Cloneable {
    private final MetodoPagamentoId id;
    private final String nome;
    private final TipoMetodo tipo;
    private boolean ativo;
    private final Map<String, String> configuracoes;

    public MetodoPagamento(MetodoPagamentoId id, String nome, TipoMetodo tipo) {
        Objects.requireNonNull(id, "O ID do método não pode ser nulo");
        Objects.requireNonNull(nome, "O nome não pode ser nulo");
        Objects.requireNonNull(tipo, "O tipo não pode ser nulo");
        
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = true;
        this.configuracoes = new HashMap<>();
    }

    public MetodoPagamentoId getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoMetodo getTipo() {
        return tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Map<String, String> getConfiguracoes() {
        return new HashMap<>(configuracoes);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void adicionarConfiguracao(String chave, String valor) {
        Objects.requireNonNull(chave, "A chave não pode ser nula");
        Objects.requireNonNull(valor, "O valor não pode ser nulo");
        this.configuracoes.put(chave, valor);
    }

    @Override
    public MetodoPagamento clone() {
        try {
            MetodoPagamento cloned = (MetodoPagamento) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Erro ao clonar método de pagamento", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetodoPagamento that = (MetodoPagamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
