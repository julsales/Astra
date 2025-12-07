package com.astra.cinema.infraestrutura.pagamento;

import com.astra.cinema.dominio.pagamento.Pagamento;
import com.astra.cinema.dominio.pagamento.ProcessadorPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * PADRÃO TEMPLATE METHOD - Implementação Concreta (Infraestrutura)
 * Processador específico para pagamentos com CARTÃO DE CRÉDITO.
 * 
 * Esta classe contém detalhes de implementação técnica (chamadas a gateways externos)
 * e por isso pertence à camada de infraestrutura, não ao domínio.
 */
@Component("processadorCartaoCredito")
public class ProcessadorCartaoCreditoImpl extends ProcessadorPagamento {

    @Override
    protected String processarComGateway(Pagamento pagamento, BigDecimal valor) {
        System.out.println("💳 Processando pagamento com CARTÃO DE CRÉDITO...");
        System.out.println("   Valor: R$ " + valor);

        // Simulação de chamada ao gateway de pagamento
        // Em produção, aqui faria uma chamada REST/SOAP para Cielo, Rede, PagSeguro, etc.
        // Exemplo: String response = restTemplate.postForObject("https://api.cielo.com/payment", request, String.class);

        try {
            Thread.sleep(500); // Simula latência de rede
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simula aprovação (90% de sucesso)
        if (Math.random() < 0.9) {
            String codigoAutorizacao = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("   ✅ Aprovado! Código: " + codigoAutorizacao);
            return codigoAutorizacao;
        } else {
            System.out.println("   ❌ Recusado pelo emissor");
            return null;
        }
    }

    @Override
    protected boolean verificarLimites(BigDecimal valor) {
        // Cartão de crédito: limite de R$ 5.000 por transação
        return valor.compareTo(new BigDecimal("5000.00")) <= 0;
    }

    @Override
    public String getNome() {
        return "Cartão de Crédito";
    }
}
