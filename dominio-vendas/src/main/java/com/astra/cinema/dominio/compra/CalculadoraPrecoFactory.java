package com.astra.cinema.dominio.compra;

import java.math.BigDecimal;

/**
 * PADRÃO FACTORY + STRATEGY
 * Factory para criar instâncias de CalculadoraPreco com base no TipoIngresso.
 */
public class CalculadoraPrecoFactory {

    /**
     * Cria a estratégia de cálculo de preço apropriada para o tipo de ingresso.
     *
     * @param tipo Tipo do ingresso
     * @return Calculadora de preço correspondente
     */
    public static CalculadoraPreco criar(TipoIngresso tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de ingresso não pode ser nulo");
        }

        return switch (tipo) {
            case INTEIRA -> new PrecoInteira();
            case MEIA -> new PrecoMeiaEntrada();
            default -> throw new IllegalArgumentException("Tipo de ingresso não suportado: " + tipo);
        };
    }

    /**
     * Cria uma estratégia promocional customizada.
     *
     * @param percentualDesconto Desconto em decimal (0.30 = 30%)
     * @param nomePromocao Nome da promoção
     * @return Calculadora de preço promocional
     */
    public static CalculadoraPreco criarPromocional(BigDecimal percentualDesconto, String nomePromocao) {
        return new PrecoPromocional(percentualDesconto, nomePromocao);
    }
}
