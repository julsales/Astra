package com.astra.cinema.dominio.compra;

/**
 * Status do ingresso simplificado: apenas ATIVO e VALIDADO.
 */
public enum StatusIngresso {
    ATIVO,       // Ingresso comprado mas não validado ainda (ativo para uso)
    VALIDADO     // Ingresso validado pelo funcionário (já foi usado)
}
