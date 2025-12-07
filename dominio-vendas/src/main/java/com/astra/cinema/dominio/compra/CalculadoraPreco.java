package com.astra.cinema.dominio.compra;

import java.math.BigDecimal;

/**
 * PADRÃO STRATEGY
 * Interface que define a estratégia de cálculo de preço de ingresso.
 *
 * Permite diferentes algoritmos de precificação (inteira, meia-entrada,
 * promocional, etc) sem modificar a entidade Ingresso.
 */
public interface CalculadoraPreco {

    /**
     * Calcula o preço final do ingresso com base no preço base da sessão.
     *
     * @param precoBase Preço base da sessão
     * @return Preço final do ingresso
     */
    BigDecimal calcular(BigDecimal precoBase);

    /**
     * Retorna o nome da estratégia de precificação.
     */
    String getNomeEstrategia();
}
