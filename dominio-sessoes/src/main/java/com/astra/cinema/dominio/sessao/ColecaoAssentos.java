package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.AssentoId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * PADRÃO ITERATOR - Coleção Agregada
 * Coleção de assentos que implementa Iterable para permitir iteração customizada.
 * 
 * CORREÇÃO: Usa cópia defensiva no construtor para evitar exposição da estrutura interna.
 */
public class ColecaoAssentos implements Iterable<Map.Entry<AssentoId, Boolean>> {

    private final Map<AssentoId, Boolean> assentos;
    private final AssentoIterator.FiltroAssento filtroDefault;

    public ColecaoAssentos(Map<AssentoId, Boolean> assentos) {
        this(assentos, AssentoIterator.FiltroAssento.TODOS);
    }

    public ColecaoAssentos(Map<AssentoId, Boolean> assentos, AssentoIterator.FiltroAssento filtro) {
        // Cópia defensiva para evitar modificações externas
        this.assentos = assentos != null ? new HashMap<>(assentos) : new HashMap<>();
        this.filtroDefault = filtro;
    }

    @Override
    public Iterator<Map.Entry<AssentoId, Boolean>> iterator() {
        return new AssentoIterator(assentos, filtroDefault);
    }

    /**
     * Cria um iterator com filtro específico.
     */
    public Iterator<Map.Entry<AssentoId, Boolean>> iterator(AssentoIterator.FiltroAssento filtro) {
        return new AssentoIterator(assentos, filtro);
    }

    /**
     * Retorna coleção iterável apenas com assentos disponíveis.
     */
    public Iterable<Map.Entry<AssentoId, Boolean>> disponiveis() {
        return () -> new AssentoIterator(assentos, AssentoIterator.FiltroAssento.DISPONIVEIS);
    }

    /**
     * Retorna coleção iterável apenas com assentos ocupados.
     */
    public Iterable<Map.Entry<AssentoId, Boolean>> ocupados() {
        return () -> new AssentoIterator(assentos, AssentoIterator.FiltroAssento.OCUPADOS);
    }

    /**
     * Conta assentos disponíveis usando o iterator.
     */
    public int contarDisponiveis() {
        int count = 0;
        for (@SuppressWarnings("unused") Map.Entry<AssentoId, Boolean> assento : disponiveis()) {
            count++;
        }
        return count;
    }

    /**
     * Conta assentos ocupados usando o iterator.
     */
    public int contarOcupados() {
        int count = 0;
        for (@SuppressWarnings("unused") Map.Entry<AssentoId, Boolean> assento : ocupados()) {
            count++;
        }
        return count;
    }

    /**
     * Retorna total de assentos.
     */
    public int total() {
        return assentos.size();
    }

    /**
     * Retorna percentual de ocupação.
     */
    public double percentualOcupacao() {
        if (assentos.isEmpty()) {
            return 0.0;
        }
        return (contarOcupados() * 100.0) / total();
    }
}
