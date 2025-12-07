package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.compra.Ingresso;

/**
 * PADRÃO DECORATOR - Decorator Concreto
 * Adiciona validação de QR Code ao validador base.
 */
public class ValidadorQRCode extends ValidadorIngressoDecorator {

    public ValidadorQRCode(ValidadorIngresso validadorBase) {
        super(validadorBase);
    }

    @Override
    protected ResultadoValidacao validarAdicional(Ingresso ingresso) {
        String qrCode = ingresso.getQrCode();

        if (qrCode == null || qrCode.trim().isEmpty()) {
            return new ResultadoValidacao(false, "QR Code inválido ou ausente");
        }

        // Simula validação de formato do QR Code
        if (qrCode.length() < 10) {
            return new ResultadoValidacao(false, "QR Code com formato inválido");
        }

        return new ResultadoValidacao(true, "QR Code válido: " + qrCode.substring(0, 8) + "...");
    }
}
