package com.astra.cinema.dominio.compra;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PADRÃO STRATEGY - Implementação Concreta
 * Estratégia para cálculo de preço de MEIA-ENTRADA (50% do valor).
 */
public class PrecoMeiaEntrada implements CalculadoraPreco {

    private static final BigDecimal DESCONTO = new BigDecimal("0.50");

    @Override
    public BigDecimal calcular(BigDecimal precoBase) {
        if (precoBase == null || precoBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço base deve ser positivo");
        }
        // Retorna 50% do preço base
        return precoBase.multiply(DESCONTO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomeEstrategia() {
        return "Meia-Entrada";
    }
}
