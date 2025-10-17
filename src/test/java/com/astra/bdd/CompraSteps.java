package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.model.*;
import com.astra.repository.*;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompraSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private AssentoRepository assentoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Contexto
    @Dado("que existe um filme {string} em cartaz")
    public void queExisteUmFilmeEmCartaz(String titulo) {
        Filme filme = new Filme(titulo, "Sinopse do filme", 150);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        context.setFilme(filme);
    }

    @Dado("que existe uma sala {string} com capacidade para {int} assentos")
    public void queExisteUmaSalaComCapacidadePara(String numero, Integer capacidade) {
        // Ignorar número passado e gerar um único
        String numeroUnico = context.gerarNumeroSalaUnico();
        Sala sala = new Sala(numeroUnico, capacidade, Sala.TipoSala.STANDARD);
        sala = salaRepository.save(sala);
        context.setSala(sala);
    }

    @Dado("que existe uma sessão para o filme às {string} com preço de {string}")
    public void queExisteUmaSessaoParaOFilmeAsComPrecoDe(String horario, String preco) {
        LocalDateTime dataHora = LocalDateTime.now().withHour(19).withMinute(0).withSecond(0);
        BigDecimal precoDecimal = new BigDecimal(preco.replace("R$ ", "").replace(",", "."));
        
        Sessao sessao = new Sessao(context.getFilme(), context.getSala(), dataHora, precoDecimal);
        sessao = sessaoRepository.save(sessao);
        context.setSessao(sessao);
        
        // Criar assentos para a sessão
        criarAssentosParaSessao(sessao);
    }

    private void criarAssentosParaSessao(Sessao sessao) {
        for (int i = 1; i <= 10; i++) {
            Assento assento = new Assento(sessao, "A" + i);
            assento = assentoRepository.save(assento);
            context.getAssentos().add(assento);
        }
    }

    // Regra de Negócio 1 - Iniciar compra com assentos disponíveis
    @Dado("que existe uma sessão com assentos A1 e A2 disponíveis")
    public void queExisteUmaSessaoComAssentosA1EA2Disponiveis() {
        // Assentos já foram criados no contexto
        Assento a1 = context.getAssentoPorIdentificacao("A1");
        Assento a2 = context.getAssentoPorIdentificacao("A2");
        
        assertNotNull(a1);
        assertNotNull(a2);
        assertEquals(Assento.StatusAssento.DISPONIVEL, a1.getStatus());
        assertEquals(Assento.StatusAssento.DISPONIVEL, a2.getStatus());
    }

    @Dado("um cliente autenticado deseja comprar dois ingressos")
    public void umClienteAutenticadoDesejaComprarDoisIngressos() {
        context.inicializarClientePadrao();
        assertNotNull(context.getCliente());
    }

    @Quando("o cliente seleciona os assentos A1 e A2 e inicia a compra")
    public void oClienteSelecionaOsAssentosA1EA2EIniciaACompra() {
        Cliente cliente = context.getCliente();
        Sessao sessao = context.getSessao();
        
        Compra compra = new Compra(cliente, sessao, new BigDecimal("60.00"));
        
        // Adicionar ingressos
        Assento a1 = context.getAssentoPorIdentificacao("A1");
        Assento a2 = context.getAssentoPorIdentificacao("A2");
        
        Ingresso ingresso1 = new Ingresso(compra, a1, Ingresso.TipoIngresso.INTEIRA);
        Ingresso ingresso2 = new Ingresso(compra, a2, Ingresso.TipoIngresso.INTEIRA);
        
        compra.adicionarIngresso(ingresso1);
        compra.adicionarIngresso(ingresso2);
        
        compra = compraRepository.save(compra);
        context.setCompra(compra);
        
        // Reservar assentos
        a1.reservar();
        a2.reservar();
        assentoRepository.save(a1);
        assentoRepository.save(a2);
    }

    @Então("a compra é criada com status {string}")
    public void aCompraECriadaComStatus(String status) {
        Compra compra = context.getCompra();
        assertNotNull(compra);
        assertEquals(Compra.StatusCompra.valueOf(status), compra.getStatus());
    }

    @Então("os assentos A1 e A2 ficam temporariamente reservados")
    public void osAssentosA1EA2FicamTemporariamenteReservados() {
        Assento a1 = assentoRepository.findBySessaoAndIdentificacao(
            context.getSessao(), "A1").orElse(null);
        Assento a2 = assentoRepository.findBySessaoAndIdentificacao(
            context.getSessao(), "A2").orElse(null);
        
        assertNotNull(a1);
        assertNotNull(a2);
        assertEquals(Assento.StatusAssento.RESERVADO, a1.getStatus());
        assertEquals(Assento.StatusAssento.RESERVADO, a2.getStatus());
    }

    // Tentativa com assento indisponível
    @Dado("que o assento A1 da sessão já está ocupado")
    public void queOAssentoA1DaSessaoJaEstaOcupado() {
        Assento a1 = context.getAssentoPorIdentificacao("A1");
        a1.ocupar();
        assentoRepository.save(a1);
    }

    @Quando("o cliente tenta iniciar uma compra incluindo o assento A1")
    public void oClienteTentaIniciarUmaCompraIncluindoOAssentoA1() {
        try {
            Cliente cliente = context.getCliente();
            Sessao sessao = context.getSessao();
            Assento a1 = context.getAssentoPorIdentificacao("A1");
            
            Compra compra = new Compra(cliente, sessao, new BigDecimal("30.00"));
            Ingresso ingresso = new Ingresso(compra, a1, Ingresso.TipoIngresso.INTEIRA);
            
            // Tentar reservar assento ocupado
            a1.reservar();
            
            compraRepository.save(compra);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema rejeita a criação da compra")
    public void oSistemaRejeitaACriacaoDaCompra() {
        assertNotNull(context.getException());
    }

    @Então("informa que o assento está indisponível")
    public void informaQueOAssentoEstaIndisponivel() {
        assertTrue(context.getMensagemErro().contains("não está disponível") ||
                   context.getMensagemErro().contains("indisponível"));
    }

    // Regra de Negócio 2 - Confirmar compra com pagamento autorizado
    @Dado("que existe uma compra pendente associada a um pagamento com status {string}")
    public void queExisteUmaCompraPendenteAssociadaAUmPagamentoComStatus(String statusPagamento) {
        context.inicializarClientePadrao();
        
        Cliente cliente = context.getCliente();
        Sessao sessao = context.getSessao();
        
        Compra compra = new Compra(cliente, sessao, new BigDecimal("60.00"));
        
        // Adicionar ingressos
        Assento a3 = context.getAssentoPorIdentificacao("A3");
        Assento a4 = context.getAssentoPorIdentificacao("A4");
        
        Ingresso ing1 = new Ingresso(compra, a3, Ingresso.TipoIngresso.INTEIRA);
        Ingresso ing2 = new Ingresso(compra, a4, Ingresso.TipoIngresso.INTEIRA);
        
        compra.adicionarIngresso(ing1);
        compra.adicionarIngresso(ing2);
        
        compra = compraRepository.save(compra);
        
        // Criar pagamento
        Pagamento pagamento = new Pagamento(compra.getValorTotal(), Pagamento.FormaPagamento.CARTAO_CREDITO);
        pagamento.setStatus(Pagamento.StatusPagamento.valueOf(statusPagamento));
        compra.setPagamento(pagamento);
        
        pagamento = pagamentoRepository.save(pagamento);
        compra = compraRepository.save(compra);
        
        context.setCompra(compra);
        context.setPagamento(pagamento);
    }

    @Quando("o cliente confirma a compra")
    public void oClienteConfirmaACompra() {
        try {
            Compra compra = context.getCompra();
            compra.confirmar();
            compraRepository.save(compra);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o status da compra é atualizado para {string}")
    public void oStatusDaCompraEAtualizadoPara(String status) {
        Compra compra = compraRepository.findById(context.getCompra().getId()).orElse(null);
        assertNotNull(compra);
        assertEquals(Compra.StatusCompra.valueOf(status), compra.getStatus());
    }

    @Então("os ingressos passam a ter status {string}")
    public void osIngressosPassamATerStatus(String status) {
        Compra compra = compraRepository.findById(context.getCompra().getId()).orElse(null);
        assertNotNull(compra);
        
        for (Ingresso ingresso : compra.getIngressos()) {
            assertEquals(Ingresso.StatusIngresso.valueOf(status), ingresso.getStatus());
        }
    }

    @Quando("o cliente tenta confirmar a compra")
    public void oClienteTentaConfirmarACompra() {
        try {
            Compra compra = context.getCompra();
            compra.confirmar();
            compraRepository.save(compra);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema impede a confirmação")
    public void oSistemaImpedeAConfirmacao() {
        assertNotNull(context.getException());
    }

    @Então("informa que o pagamento não foi autorizado")
    public void informaQueOPagamentoNaoFoiAutorizado() {
        assertTrue(context.getMensagemErro().contains("não autorizado") ||
                   context.getMensagemErro().contains("Pagamento"));
    }

    // Regra de Negócio 3 - Cancelamento de compra
    @Dado("que existe uma compra confirmada com ingressos ainda válidos")
    public void queExisteUmaCompraConfirmadaComIngressosAindaValidos() {
        context.inicializarClientePadrao();
        
        Cliente cliente = context.getCliente();
        Sessao sessao = context.getSessao();
        
        Compra compra = new Compra(cliente, sessao, new BigDecimal("60.00"));
        
        Assento a5 = context.getAssentoPorIdentificacao("A5");
        Assento a6 = context.getAssentoPorIdentificacao("A6");
        
        Ingresso ing1 = new Ingresso(compra, a5, Ingresso.TipoIngresso.INTEIRA);
        Ingresso ing2 = new Ingresso(compra, a6, Ingresso.TipoIngresso.INTEIRA);
        ing1.validar();
        ing2.validar();
        
        compra.adicionarIngresso(ing1);
        compra.adicionarIngresso(ing2);
        
        // Criar pagamento bem-sucedido
        Pagamento pagamento = new Pagamento(compra.getValorTotal(), Pagamento.FormaPagamento.CARTAO_CREDITO);
        pagamento.autorizar("AUTH123");
        compra.setPagamento(pagamento);
        
        compra.setStatus(Compra.StatusCompra.CONFIRMADA);
        
        compra = compraRepository.save(compra);
        context.setCompra(compra);
    }

    @Quando("o cliente solicita o cancelamento")
    public void oClienteSolicitaOCancelamento() {
        try {
            Compra compra = context.getCompra();
            compra.cancelar();
            compraRepository.save(compra);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o status da compra é alterado para {string}")
    public void oStatusDaCompraEAlteradoPara(String status) {
        Compra compra = compraRepository.findById(context.getCompra().getId()).orElse(null);
        assertNotNull(compra);
        assertEquals(Compra.StatusCompra.valueOf(status), compra.getStatus());
    }

    @Então("o pagamento é estornado")
    public void oPagamentoEEstornado() {
        Compra compra = compraRepository.findById(context.getCompra().getId()).orElse(null);
        assertNotNull(compra);
        assertEquals(Pagamento.StatusPagamento.ESTORNADO, compra.getPagamento().getStatus());
    }

    @Dado("que o ingresso da compra já foi utilizado na entrada da sessão")
    public void queOIngressoDaCompraJaFoiUtilizadoNaEntradaDaSessao() {
        context.inicializarClientePadrao();
        
        Cliente cliente = context.getCliente();
        Sessao sessao = context.getSessao();
        
        Compra compra = new Compra(cliente, sessao, new BigDecimal("30.00"));
        
        Assento a7 = context.getAssentoPorIdentificacao("A7");
        Ingresso ingresso = new Ingresso(compra, a7, Ingresso.TipoIngresso.INTEIRA);
        ingresso.validar();
        ingresso.utilizar(); // Marcar como utilizado
        
        compra.adicionarIngresso(ingresso);
        compra.setStatus(Compra.StatusCompra.CONFIRMADA);
        
        compra = compraRepository.save(compra);
        context.setCompra(compra);
    }

    @Quando("o cliente tenta cancelar a compra")
    public void oClienteTentaCancelarACompra() {
        try {
            Compra compra = context.getCompra();
            compra.cancelar();
            compraRepository.save(compra);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema recusa o cancelamento")
    public void oSistemaRecusaOCancelamento() {
        assertNotNull(context.getException());
    }

    @Então("informa que o ingresso já foi utilizado")
    public void informaQueOIngressoJaFoiUtilizado() {
        assertTrue(context.getMensagemErro().contains("utilizado") ||
                   context.getMensagemErro().contains("já foi"));
    }
}
