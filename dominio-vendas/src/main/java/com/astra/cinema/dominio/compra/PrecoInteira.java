package com.astra.cinema.dominio.compra;

import java.math.BigDecimal;

/**
 * PADRÃO STRATEGY - Implementação Concreta
 * Estratégia para cálculo de preço de ingresso INTEIRA (100% do valor).
 */
public class PrecoInteira implements CalculadoraPreco {

    @Override
    public BigDecimal calcular(BigDecimal precoBase) {
        if (precoBase == null || precoBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço base deve ser positivo");
        }
        // Retorna 100% do preço base
        return precoBase;
    }

    @Override
    public String getNomeEstrategia() {
        return "Inteira";
    }
}
