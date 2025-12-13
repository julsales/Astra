package com.astra.cinema.dominio.comum;

/**
 * Classe centralizada para gerenciar preços de ingressos.
 * Facilita alterações de preços em um único lugar.
 */
public final class PrecoIngresso {
    
    // Preços configurados
    private static final double PRECO_INTEIRA = 35.0;
    private static final double PRECO_MEIA = 17.5;
    
    private PrecoIngresso() {
        // Construtor privado para evitar instanciação
    }
    
    /**
     * Obtém o preço do ingresso inteira
     * @return Valor do ingresso inteira
     */
    public static double obterPrecoInteira() {
        return PRECO_INTEIRA;
    }
    
    /**
     * Obtém o preço do ingresso meia
     * @return Valor do ingresso meia
     */
    public static double obterPrecoMeia() {
        return PRECO_MEIA;
    }
}
