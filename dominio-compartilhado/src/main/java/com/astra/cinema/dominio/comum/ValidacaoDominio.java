package com.astra.cinema.dominio.comum;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

/**
 * Helper centralizado para mensagens e invariantes de domínio.
 */
public final class ValidacaoDominio {

    private ValidacaoDominio() {
    }

    public static <T> T exigirNaoNulo(T valor, String mensagem) {
        return Objects.requireNonNull(valor, mensagem);
    }

    public static String exigirTexto(String valor, String mensagem) {
        return Validate.notBlank(valor, mensagem);
    }

    public static <T extends Collection<?>> T exigirColecaoNaoVazia(T colecao, String mensagem) {
        Validate.notNull(colecao, mensagem);
        Validate.isTrue(!colecao.isEmpty(), mensagem);
        return colecao;
    }

    public static int exigirPositivo(int valor, String mensagem) {
        Validate.isTrue(valor > 0, mensagem);
        return valor;
    }

    public static long exigirPositivo(long valor, String mensagem) {
        Validate.isTrue(valor > 0, mensagem);
        return valor;
    }

    public static double exigirPositivo(double valor, String mensagem) {
        Validate.isTrue(valor > 0, mensagem);
        return valor;
    }

    public static BigDecimal exigirPositivo(BigDecimal valor, String mensagem) {
        Validate.notNull(valor, mensagem);
        Validate.isTrue(valor.compareTo(BigDecimal.ZERO) > 0, mensagem);
        return valor;
    }

    public static double exigirNaoNegativo(double valor, String mensagem) {
        Validate.isTrue(valor >= 0, mensagem);
        return valor;
    }

    public static int exigirNaoNegativo(int valor, String mensagem) {
        Validate.isTrue(valor >= 0, mensagem);
        return valor;
    }

    public static void exigirEstado(boolean condicaoValida, String mensagem) {
        if (!condicaoValida) {
            throw new IllegalStateException(mensagem);
        }
    }
}
