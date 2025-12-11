package com.astra.cinema.apresentacao.servicos;

import com.astra.cinema.aplicacao.servicos.VendaProdutoService;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpa;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementação do serviço de venda de produtos
 * Responsável por criar registros de venda na infraestrutura
 */
public class VendaProdutoServiceImpl implements VendaProdutoService {

    private static final Logger log = LoggerFactory.getLogger(VendaProdutoServiceImpl.class);

    private final VendaJpaRepository vendaJpaRepository;

    public VendaProdutoServiceImpl(VendaJpaRepository vendaJpaRepository) {
        this.vendaJpaRepository = vendaJpaRepository;
    }

    @Override
    public void criarVendaParaCompra(int produtoId, int quantidade, int compraId) {
        // Para cada unidade do produto, cria uma linha na tabela venda
        for (int i = 0; i < quantidade; i++) {
            VendaJpa vendaJpa = new VendaJpa();
            vendaJpa.setProdutoId(produtoId);
            vendaJpa.setQuantidade(1);
            vendaJpa.setPagamentoId(null); // Opcional, compra já tem pagamento
            vendaJpa.setStatus("CONFIRMADA");
            vendaJpa.setCompraId(compraId); // Associa à compra
            vendaJpa.setCriadoEm(java.time.LocalDateTime.now());
            vendaJpaRepository.save(vendaJpa);
            log.info("Venda criada: produto ID {} associado à compra {}", produtoId, compraId);
        }
    }
}
