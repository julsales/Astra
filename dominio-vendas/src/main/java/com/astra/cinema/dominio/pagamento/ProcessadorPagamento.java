package com.astra.cinema.dominio.pagamento;

import java.math.BigDecimal;

/**
 * PADRÃO TEMPLATE METHOD
 * Classe abstrata que define o template (esqueleto) para processamento de pagamentos.
 *
 * O método processar() define a sequência de passos, mas permite que
 * subclasses customizem partes específicas do algoritmo.
 */
public abstract class ProcessadorPagamento {

    /**
     * TEMPLATE METHOD
     * Define o algoritmo completo de processamento de pagamento.
     * Não pode ser sobrescrito (final).
     */
    public final ResultadoProcessamento processar(Pagamento pagamento, BigDecimal valor) {
        // 1. Validar dados do pagamento
        if (!validarDados(pagamento, valor)) {
            return new ResultadoProcessamento(false, "Dados de pagamento inválidos", null);
        }

        // 2. Verificar limites
        if (!verificarLimites(valor)) {
            return new ResultadoProcessamento(false, "Valor excede limite permitido", null);
        }

        // 3. Processar com gateway específico (método abstrato - varia por subclasse)
        String codigoAutorizacao = processarComGateway(pagamento, valor);

        if (codigoAutorizacao == null || codigoAutorizacao.isEmpty()) {
            // 4. Tratar falha
            tratarFalha(pagamento);
            return new ResultadoProcessamento(false, "Pagamento recusado pelo gateway", null);
        }

        // 5. Confirmar transação
        confirmarTransacao(pagamento, codigoAutorizacao);

        // 6. Gerar comprovante
        gerarComprovante(pagamento, valor, codigoAutorizacao);

        return new ResultadoProcessamento(true, "Pagamento autorizado", codigoAutorizacao);
    }

    // Métodos com implementação padrão (podem ser sobrescritos)

    protected boolean validarDados(Pagamento pagamento, BigDecimal valor) {
        return pagamento != null && valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    protected boolean verificarLimites(BigDecimal valor) {
        // Limite padrão: R$ 10.000
        return valor.compareTo(new BigDecimal("10000.00")) <= 0;
    }

    protected void confirmarTransacao(Pagamento pagamento, String codigoAutorizacao) {
        System.out.println("✅ Transação confirmada: " + codigoAutorizacao);
    }

    protected void tratarFalha(Pagamento pagamento) {
        System.out.println("❌ Falha no processamento do pagamento ID: " + pagamento.getPagamentoId().getId());
    }

    protected void gerarComprovante(Pagamento pagamento, BigDecimal valor, String codigoAutorizacao) {
        String comprovante = String.format("Comprovante #%s - Valor: R$ %.2f - Auth: %s",
                pagamento.getPagamentoId().getId(), valor, codigoAutorizacao);
        System.out.println("📄 " + comprovante);
    }

    // Método abstrato - DEVE ser implementado pelas subclasses
    protected abstract String processarComGateway(Pagamento pagamento, BigDecimal valor);

    /**
     * Retorna o nome do processador (para identificação).
     */
    public abstract String getNome();

    /**
     * Classe interna para resultado do processamento.
     */
    public static class ResultadoProcessamento {
        private final boolean sucesso;
        private final String mensagem;
        private final String codigoAutorizacao;

        public ResultadoProcessamento(boolean sucesso, String mensagem, String codigoAutorizacao) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.codigoAutorizacao = codigoAutorizacao;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public String getCodigoAutorizacao() {
            return codigoAutorizacao;
        }
    }
}
