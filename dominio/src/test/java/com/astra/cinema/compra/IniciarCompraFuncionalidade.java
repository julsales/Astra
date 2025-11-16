package com.astra.cinema.compra;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.usuario.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class IniciarCompraFuncionalidade extends CinemaFuncionalidade {
    private ClienteId clienteId = new ClienteId(1);
    private FilmeId filmeId = new FilmeId(1);
    private SessaoId sessaoId = new SessaoId(1);
    private AssentoId assentoA1 = new AssentoId("A1");
    private AssentoId assentoA2 = new AssentoId("A2");
    
    private Compra compraRealizada;
    private RuntimeException excecao;

    @Dado("que existe uma sessão com assentos A1 e A2 disponíveis")
    public void que_existe_uma_sessao_com_assentos_a1_e_a2_disponiveis() {
        // Cria o filme
        var filme = new Filme(filmeId, "Duna 2", "Sinopse", "12", 150, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Cria a sessão com assentos disponíveis
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(assentoA1, true);
        assentos.put(assentoA2, true);
        
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        sessaoService.salvar(sessao);
    }

    @Dado("um cliente autenticado deseja comprar dois ingressos")
    public void um_cliente_autenticado_deseja_comprar_dois_ingressos() {
        var cliente = new Cliente(clienteId, "João Silva", "joao@email.com");
        clienteService.salvar(cliente);
    }

    @Quando("o cliente seleciona os assentos A1 e A2 e inicia a compra")
    public void o_cliente_seleciona_os_assentos_a1_e_a2_e_inicia_a_compra() {
        try {
            var ingresso1 = new Ingresso(new IngressoId(1), sessaoId, assentoA1, 
                                        TipoIngresso.INTEIRA, StatusIngresso.VALIDO, "QR1");
            var ingresso2 = new Ingresso(new IngressoId(2), sessaoId, assentoA2, 
                                        TipoIngresso.MEIA, StatusIngresso.VALIDO, "QR2");
            
            compraRealizada = compraService.iniciarCompra(clienteId, Arrays.asList(ingresso1, ingresso2));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("a compra é criada com status {string}")
    public void a_compra_e_criada_com_status(String status) {
        assertNotNull(compraRealizada);
        assertEquals(StatusCompra.valueOf(status), compraRealizada.getStatus());
    }

    @Então("os assentos A1 e A2 ficam temporariamente reservados")
    public void os_assentos_a1_e_a2_ficam_temporariamente_reservados() {
        var sessao = sessaoService.obter(sessaoId);
        assertFalse(sessao.assentoDisponivel(assentoA1));
        assertFalse(sessao.assentoDisponivel(assentoA2));
    }

    @Dado("que o assento A1 da sessão já está ocupado")
    public void que_o_assento_a1_da_sessao_ja_esta_ocupado() {
        // Cria o filme
        var filme = new Filme(filmeId, "Duna 2", "Sinopse", "12", 150, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Cria a sessão com A1 ocupado e A2 disponível
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(assentoA1, false); // Ocupado
        assentos.put(assentoA2, true);
        
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        sessaoService.salvar(sessao);
        
        var cliente = new Cliente(clienteId, "João Silva", "joao@email.com");
        clienteService.salvar(cliente);
    }

    @Quando("o cliente tenta iniciar uma compra incluindo o assento A1")
    public void o_cliente_tenta_iniciar_uma_compra_incluindo_o_assento_a1() {
        try {
            var ingresso1 = new Ingresso(new IngressoId(1), sessaoId, assentoA1, 
                                        TipoIngresso.INTEIRA, StatusIngresso.VALIDO, "QR1");
            
            compraRealizada = compraService.iniciarCompra(clienteId, Arrays.asList(ingresso1));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema rejeita a criação da compra")
    public void o_sistema_rejeita_a_criacao_da_compra() {
        assertNotNull(excecao);
        assertNull(compraRealizada);
    }

    @Então("informa que o assento está indisponível")
    public void informa_que_o_assento_esta_indisponivel() {
        assertTrue(excecao.getMessage().contains("não está disponível"));
    }
}
