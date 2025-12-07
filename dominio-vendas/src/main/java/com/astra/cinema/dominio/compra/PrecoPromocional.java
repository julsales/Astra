package com.astra.cinema.dominio.compra;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PADRÃO STRATEGY - Implementação Concreta
 * Estratégia para cálculo de preço PROMOCIONAL (desconto customizado).
 *
 * Exemplo: Desconto de 30% para matinês, clientes VIP, etc.
 */
public class PrecoPromocional implements CalculadoraPreco {

    private final BigDecimal percentualDesconto;
    private final String nomePromocao;

    /**
     * @param percentualDesconto Desconto em decimal (0.30 = 30% de desconto)
     * @param nomePromocao Nome da promoção (ex: "Matinê", "VIP", "Estudante")
     */
    public PrecoPromocional(BigDecimal percentualDesconto, String nomePromocao) {
        if (percentualDesconto == null || percentualDesconto.compareTo(BigDecimal.ZERO) < 0
            || percentualDesconto.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Desconto deve estar entre 0 e 1");
        }
        this.percentualDesconto = percentualDesconto;
        this.nomePromocao = nomePromocao != null ? nomePromocao : "Promocional";
    }

    @Override
    public BigDecimal calcular(BigDecimal precoBase) {
        if (precoBase == null || precoBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço base deve ser positivo");
        }
        // Aplica o desconto percentual
        BigDecimal desconto = precoBase.multiply(percentualDesconto);
        return precoBase.subtract(desconto).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomeEstrategia() {
        return nomePromocao + " (" + percentualDesconto.multiply(new BigDecimal("100")).intValue() + "% off)";
    }
}
