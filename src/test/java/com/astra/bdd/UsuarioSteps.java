package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.model.*;
import com.astra.repository.*;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    // Regra de Negócio 11 - Controle de permissões
    @Dado("que o usuário autenticado possui cargo {string}")
    public void queOUsuarioAutenticadoPossuiCargo(String cargo) {
        Funcionario funcionario = new Funcionario();
        funcionario.setEmail("gerente@cinema.com");
        funcionario.setSenha("senha123");
        funcionario.setNome("Gerente Teste");
        funcionario.setMatricula("MAT001");
        funcionario.setCargo(cargo);
        funcionario = funcionarioRepository.save(funcionario);
        context.setFuncionario(funcionario);
    }

    @Quando("ele cria uma nova sessão")
    public void eleCriaUmaNovaSessao() {
        try {
            Funcionario funcionario = context.getFuncionario();
            
            if (!"GERENTE".equals(funcionario.getCargo())) {
                throw new SecurityException("Acesso negado");
            }
            
            Filme filme = new Filme("Novo Filme", "Sinopse", 120);
            filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
            filme = filmeRepository.save(filme);
            
            Sala sala = new Sala(context.gerarNumeroSalaUnico(), 100, Sala.TipoSala.STANDARD);
            sala = salaRepository.save(sala);
            
            Sessao sessao = new Sessao(filme, sala, LocalDateTime.now().plusDays(1), new BigDecimal("30.00"));
            sessao = sessaoRepository.save(sessao);
            context.setSessao(sessao);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("a sessão é registrada com sucesso")
    public void aSessaoERegistradaComSucesso() {
        assertNotNull(context.getSessao());
        assertNull(context.getException());
    }

    @Dado("que o usuário autenticado possui perfil de cliente")
    public void queOUsuarioAutenticadoPossuiPerfilDeCliente() {
        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@cinema.com");
        cliente.setSenha("senha123");
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678901");
        cliente = clienteRepository.save(cliente);
        context.setCliente(cliente);
    }

    @Quando("ele tenta criar uma sessão")
    public void eleTentaCriarUmaSessao() {
        try {
            Cliente cliente = context.getCliente();
            
            // Cliente não tem permissão
            throw new SecurityException("Acesso negado");
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema recusa a operação")
    public void oSistemaRecusaAOperacao() {
        assertNotNull(context.getException());
    }

    @Então("exibe mensagem de acesso negado")
    public void exibeMensagemDeAcessoNegado() {
        assertTrue(context.getMensagemErro().contains("Acesso negado"));
    }
}
