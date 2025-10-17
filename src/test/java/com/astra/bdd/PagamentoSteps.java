package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.model.Pagamento;
import com.astra.repository.PagamentoRepository;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PagamentoSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    // Regra de Negócio 9 - Autorizar pagamento
    @Dado("que o cliente insere dados válidos do cartão")
    public void queOClienteInsereDadosValidosDoCartao() {
        Pagamento pagamento = new Pagamento(new BigDecimal("100.00"), Pagamento.FormaPagamento.CARTAO_CREDITO);
        pagamento = pagamentoRepository.save(pagamento);
        context.setPagamento(pagamento);
    }

    @Quando("o gateway responde com sucesso")
    public void oGatewayRespondeComSucesso() {
        Pagamento pagamento = context.getPagamento();
        pagamento.autorizar("AUTH" + System.currentTimeMillis());
        pagamentoRepository.save(pagamento);
    }

    @Então("o pagamento muda de status para {string}")
    public void oPagamentoMudaDeStatusPara(String status) {
        Pagamento pagamento = pagamentoRepository.findById(context.getPagamento().getId()).orElse(null);
        assertNotNull(pagamento);
        assertEquals(Pagamento.StatusPagamento.valueOf(status), pagamento.getStatus());
    }

    @Dado("que o cliente insere dados de cartão inválidos")
    public void queOClienteInsereDadosDeCartaoInvalidos() {
        Pagamento pagamento = new Pagamento(new BigDecimal("100.00"), Pagamento.FormaPagamento.CARTAO_CREDITO);
        pagamento = pagamentoRepository.save(pagamento);
        context.setPagamento(pagamento);
    }

    @Quando("o gateway retorna falha")
    public void oGatewayRetornaFalha() {
        Pagamento pagamento = context.getPagamento();
        pagamento.recusar("Cartão inválido");
        pagamentoRepository.save(pagamento);
    }

    // Regra de Negócio 10 - Cancelar pagamento
    @Dado("que o pagamento está com status {string}")
    public void queOPagamentoEstaComStatus(String status) {
        Pagamento pagamento = new Pagamento(new BigDecimal("100.00"), Pagamento.FormaPagamento.PIX);
        pagamento.setStatus(Pagamento.StatusPagamento.valueOf(status));
        pagamento = pagamentoRepository.save(pagamento);
        context.setPagamento(pagamento);
    }

    @Quando("o cliente solicita cancelamento")
    public void oClienteSolicitaCancelamento() {
        try {
            Pagamento pagamento = context.getPagamento();
            pagamento.cancelar();
            pagamentoRepository.save(pagamento);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o pagamento é atualizado para {string}")
    public void oPagamentoEAtualizadoPara(String status) {
        Pagamento pagamento = pagamentoRepository.findById(context.getPagamento().getId()).orElse(null);
        assertNotNull(pagamento);
        assertEquals(Pagamento.StatusPagamento.valueOf(status), pagamento.getStatus());
    }

    @Dado("que o pagamento já foi aprovado")
    public void queOPagamentoJaFoiAprovado() {
        Pagamento pagamento = new Pagamento(new BigDecimal("100.00"), Pagamento.FormaPagamento.CARTAO_CREDITO);
        pagamento.autorizar("AUTH123456");
        pagamento = pagamentoRepository.save(pagamento);
        context.setPagamento(pagamento);
    }

    @Quando("o cliente tenta cancelar")
    public void oClienteTentaCancelar() {
        try {
            Pagamento pagamento = context.getPagamento();
            pagamento.cancelar();
            pagamentoRepository.save(pagamento);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema rejeita a solicitação")
    public void oSistemaRejeitaASolicitacao() {
        assertNotNull(context.getException());
        assertTrue(context.getMensagemErro().contains("pendentes"));
    }
}
