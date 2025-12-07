package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.compra.Ingresso;

/**
 * PADRÃO DECORATOR
 * Interface base para validadores de ingresso.
 * Define o contrato comum para validação simples ou decorada.
 */
public interface ValidadorIngresso {

    /**
     * Valida um ingresso e retorna o resultado.
     *
     * @param ingresso Ingresso a ser validado
     * @return Resultado da validação
     */
    ResultadoValidacao validar(Ingresso ingresso);

    /**
     * Classe para resultado da validação.
     */
    class ResultadoValidacao {
        private final boolean valido;
        private final String mensagem;

        public ResultadoValidacao(boolean valido, String mensagem) {
            this.valido = valido;
            this.mensagem = mensagem;
        }

        public boolean isValido() {
            return valido;
        }

        public String getMensagem() {
            return mensagem;
        }

        @Override
        public String toString() {
            return (valido ? "✅ " : "❌ ") + mensagem;
        }
    }
}
