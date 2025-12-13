package com.astra.cinema.dominio.compra;

/**
 * Status do ingresso: ATIVO (comprado), VALIDADO (usado), CANCELADO ou EXPIRADO.
 */
public enum StatusIngresso {
    ATIVO,       // Ingresso comprado mas não validado ainda (ativo para uso)
    VALIDADO,    // Ingresso validado pelo funcionário (já foi usado)
    CANCELADO,   // Ingresso cancelado (compra cancelada)
    EXPIRADO     // Ingresso expirado (sessão passou sem validação)
}
