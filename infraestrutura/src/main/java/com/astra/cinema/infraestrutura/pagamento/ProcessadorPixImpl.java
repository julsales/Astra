package com.astra.cinema.infraestrutura.pagamento;

import com.astra.cinema.dominio.pagamento.Pagamento;
import com.astra.cinema.dominio.pagamento.ProcessadorPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * PADRÃO TEMPLATE METHOD - Implementação Concreta (Infraestrutura)
 * Processador específico para pagamentos via PIX.
 * 
 * Contém integração com APIs externas (Banco Central, PSPs) e por isso
 * pertence à camada de infraestrutura.
 */
@Component("processadorPix")
public class ProcessadorPixImpl extends ProcessadorPagamento {

    @Override
    protected String processarComGateway(Pagamento pagamento, BigDecimal valor) {
        System.out.println("⚡ Processando pagamento com PIX...");
        System.out.println("   Valor: R$ " + valor);

        // Simulação de processamento PIX (instantâneo)
        // Em produção, integraria com Banco Central via API PIX
        // Exemplo: pixService.criarQRCode() / pixService.verificarPagamento()

        try {
            Thread.sleep(200); // PIX é mais rápido
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // PIX tem alta taxa de aprovação (95%)
        if (Math.random() < 0.95) {
            String codigoAutorizacao = "PIX-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            System.out.println("   ✅ Pagamento confirmado! Código: " + codigoAutorizacao);
            return codigoAutorizacao;
        } else {
            System.out.println("   ❌ Saldo insuficiente ou chave inválida");
            return null;
        }
    }

    @Override
    protected boolean verificarLimites(BigDecimal valor) {
        // PIX: sem limite por transação (até o limite da conta)
        return true;
    }

    @Override
    protected void gerarComprovante(Pagamento pagamento, BigDecimal valor, String codigoAutorizacao) {
        // Comprovante PIX tem formato específico
        String comprovante = String.format("""
                ═══════════════════════════════
                      COMPROVANTE PIX
                ═══════════════════════════════
                Valor: R$ %.2f
                Código: %s
                ═══════════════════════════════
                """,
                valor,
                codigoAutorizacao
        );
        System.out.println(comprovante);
    }

    @Override
    public String getNome() {
        return "PIX";
    }
}
