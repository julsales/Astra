package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.repository.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonSteps {

    @Autowired
    private TestContext context;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CompraRepository compraRepository;
    
    @Autowired
    private IngressoRepository ingressoRepository;
    
    @Autowired
    private AssentoRepository assentoRepository;
    
    @Autowired
    private SessaoRepository sessaoRepository;
    
    @Autowired
    private FilmeRepository filmeRepository;
    
    @Autowired
    private SalaRepository salaRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private VendaBomboniereRepository vendaBomboniereRepository;
    
    @Autowired
    private ProgramacaoRepository programacaoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Before
    public void beforeScenario() {
        limparBancoDeDados();
        context.limparContexto();
    }

    @After
    public void afterScenario() {
        limparBancoDeDados();
        context.limparContexto();
    }

    @Dado("o sistema está limpo")
    public void oSistemaEstaLimpo() {
        limparBancoDeDados();
        context.limparContexto();
    }
    
    private void limparBancoDeDados() {
        try {
            // Ordem importa por causa das foreign keys
            compraRepository.deleteAllInBatch();
            ingressoRepository.deleteAllInBatch();
            vendaBomboniereRepository.deleteAllInBatch();
            programacaoRepository.deleteAllInBatch();
            pagamentoRepository.deleteAllInBatch();
            assentoRepository.deleteAllInBatch();
            sessaoRepository.deleteAllInBatch();
            filmeRepository.deleteAllInBatch();
            salaRepository.deleteAllInBatch();
            produtoRepository.deleteAllInBatch();
            clienteRepository.deleteAllInBatch();
            funcionarioRepository.deleteAllInBatch();
            
            // Limpar cache do EntityManager
            if (entityManager != null) {
                entityManager.flush();
                entityManager.clear();
            }
        } catch (Exception e) {
            // Ignorar erros de limpeza (primeira execução)
        }
    }
}

