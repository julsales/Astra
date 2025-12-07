package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.AssentoId;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * PADRÃO ITERATOR
 * Iterator customizado para percorrer assentos de uma sessão.
 * Permite iterar apenas sobre assentos disponíveis, ocupados, ou todos.
 */
public class AssentoIterator implements Iterator<Map.Entry<AssentoId, Boolean>> {

    private final Iterator<Map.Entry<AssentoId, Boolean>> iterator;
    private final FiltroAssento filtro;
    private Map.Entry<AssentoId, Boolean> proximoElemento;

    public enum FiltroAssento {
        TODOS,         // Todos os assentos
        DISPONIVEIS,   // Apenas assentos disponíveis (true)
        OCUPADOS       // Apenas assentos ocupados (false)
    }

    public AssentoIterator(Map<AssentoId, Boolean> assentos, FiltroAssento filtro) {
        this.iterator = assentos.entrySet().iterator();
        this.filtro = filtro != null ? filtro : FiltroAssento.TODOS;
        this.proximoElemento = encontrarProximo();
    }

    @Override
    public boolean hasNext() {
        return proximoElemento != null;
    }

    @Override
    public Map.Entry<AssentoId, Boolean> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Não há mais assentos para iterar");
        }

        Map.Entry<AssentoId, Boolean> atual = proximoElemento;
        proximoElemento = encontrarProximo();
        return atual;
    }

    /**
     * Encontra o próximo elemento que satisfaz o filtro.
     */
    private Map.Entry<AssentoId, Boolean> encontrarProximo() {
        while (iterator.hasNext()) {
            Map.Entry<AssentoId, Boolean> entry = iterator.next();

            boolean atendeFiltro = switch (filtro) {
                case TODOS -> true;
                case DISPONIVEIS -> entry.getValue(); // true = disponível
                case OCUPADOS -> !entry.getValue();   // false = ocupado
            };

            if (atendeFiltro) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remoção não suportada");
    }
}
