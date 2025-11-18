package com.astra.cinema.infraestrutura.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gerador de QR Code único para ingressos
 * Gera códigos no formato ASTRA{timestamp}{random} para garantir unicidade
 */
@Component
public class QrCodeGenerator {
    
    private static final String PREFIX = "ASTRA";
    
    /**
     * Gera um QR Code único para um ingresso
     * Formato: ASTRA{timestamp}{random}
     * 
     * @return Código QR único
     */
    public String gerarQrCode() {
        // Usa timestamp + parte do UUID para garantir unicidade
        long timestamp = System.currentTimeMillis();
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        
        // Formato: ASTRA{timestamp}{uuidPart}
        // Exemplo: ASTRA1734567890123A1B2C3D4
        return PREFIX + timestamp + uuidPart;
    }
    
    /**
     * Gera um QR Code baseado no ID do ingresso (para compatibilidade)
     * Formato: ASTRA{id}
     * 
     * @param ingressoId ID do ingresso
     * @return Código QR
     */
    public String gerarQrCodePorId(Integer ingressoId) {
        return PREFIX + ingressoId;
    }
}

