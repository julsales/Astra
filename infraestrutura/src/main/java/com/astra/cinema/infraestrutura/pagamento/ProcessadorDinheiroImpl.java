package com.astra.cinema.infraestrutura.pagamento;

import com.astra.cinema.dominio.pagamento.Pagamento;
import com.astra.cinema.dominio.pagamento.ProcessadorPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * PADRÃO TEMPLATE METHOD - Implementação Concreta (Infraestrutura)
 * Processador para pagamentos em DINHEIRO (presencial).
 * 
 * Apesar de não usar APIs externas, pertence à infraestrutura por ser
 * uma implementação concreta de infraestrutura de pagamento.
 */
@Component("processadorDinheiro")
public class ProcessadorDinheiroImpl extends ProcessadorPagamento {

    @Override
    protected String processarComGateway(Pagamento pagamento, BigDecimal valor) {
        System.out.println("💵 Processando pagamento em DINHEIRO...");
        System.out.println("   Valor: R$ " + valor);

        // Dinheiro não precisa gateway externo
        // Apenas confirma recebimento presencial

        String codigoAutorizacao = "CASH-" + System.currentTimeMillis();
        System.out.println("   ✅ Pagamento recebido! Código: " + codigoAutorizacao);

        return codigoAutorizacao;
    }

    @Override
    protected boolean verificarLimites(BigDecimal valor) {
        // Dinheiro: limite de R$ 1.000 por segurança
        return valor.compareTo(new BigDecimal("1000.00")) <= 0;
    }

    @Override
    protected void confirmarTransacao(Pagamento pagamento, String codigoAutorizacao) {
        super.confirmarTransacao(pagamento, codigoAutorizacao);
        System.out.println("   💰 Registre o valor no caixa físico!");
    }

    @Override
    public String getNome() {
        return "Dinheiro";
    }
}
