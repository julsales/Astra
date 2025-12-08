package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.aplicacao.compra.CancelarCompraUseCase;
import com.astra.cinema.aplicacao.compra.IniciarCompraUseCase;
import com.astra.cinema.apresentacao.dto.mapper.IngressoMapper;
import com.astra.cinema.apresentacao.dto.request.CriarCompraRequest;
import com.astra.cinema.apresentacao.dto.response.IngressoDTO;
import com.astra.cinema.apresentacao.exception.RecursoNaoEncontradoException;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.bomboniere.StatusVenda;
import com.astra.cinema.dominio.bomboniere.Venda;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de Compra
 * Endpoints para criar, cancelar e consultar compras de ingressos
 */
@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    private static final Logger log = LoggerFactory.getLogger(CompraController.class);

    private final IniciarCompraUseCase iniciarCompraUseCase;
    private final CancelarCompraUseCase cancelarCompraUseCase;
    private final CompraRepositorio compraRepositorio;
    private final IngressoMapper ingressoMapper;
    private final ProdutoRepositorio produtoRepositorio;
    private final VendaRepositorio vendaRepositorio;

    public CompraController(IniciarCompraUseCase iniciarCompraUseCase,
                           CancelarCompraUseCase cancelarCompraUseCase,
                           CompraRepositorio compraRepositorio,
                           IngressoMapper ingressoMapper,
                           ProdutoRepositorio produtoRepositorio,
                           VendaRepositorio vendaRepositorio) {
        this.iniciarCompraUseCase = iniciarCompraUseCase;
        this.cancelarCompraUseCase = cancelarCompraUseCase;
        this.compraRepositorio = compraRepositorio;
        this.ingressoMapper = ingressoMapper;
        this.produtoRepositorio = produtoRepositorio;
        this.vendaRepositorio = vendaRepositorio;
    }

    /**
     * Cria uma nova compra de ingressos
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> criarCompra(@Valid @RequestBody CriarCompraRequest request) {
        log.info("Criando compra - clienteId={} sessaoId={} assentos={} tipoIngresso={}",
                request.getClienteId(), request.getSessaoId(), request.getAssentos(), request.getTipoIngresso());

        // Cria ingressos com ID temporário único (será substituído pelo banco)
        AtomicInteger idTemporario = new AtomicInteger(1);
        List<Ingresso> ingressos = request.getAssentos().stream()
            .map(assento -> {
                TipoIngresso tipo = request.getTipoIngresso() != null
                    ? TipoIngresso.valueOf(request.getTipoIngresso().toUpperCase())
                    : TipoIngresso.INTEIRA;

                int idTemp = idTemporario.getAndIncrement();
                return new Ingresso(
                    new IngressoId(idTemp),
                    new SessaoId(request.getSessaoId().intValue()),
                    new AssentoId(assento),
                    tipo,
                    StatusIngresso.ATIVO,
                    null // QR Code será gerado automaticamente
                );
            })
            .collect(Collectors.toList());

        log.info("Ingressos construídos: {}", ingressos.size());

        // Salva a compra PRIMEIRO (gera QR Codes automaticamente)
        Compra compra = iniciarCompraUseCase.executar(
            new ClienteId(request.getClienteId().intValue()),
            ingressos
        );

        // Processar produtos da bomboniere (se houver) e associar à compra
        if (request.getProdutos() != null && !request.getProdutos().isEmpty()) {
            log.info("Processando {} produtos da bomboniere para compra {}", request.getProdutos().size(), compra.getCompraId().getId());
            for (CriarCompraRequest.ItemProduto itemProduto : request.getProdutos()) {
                Produto produto = produtoRepositorio.obterPorId(new ProdutoId(itemProduto.getProdutoId()));

                if (produto == null) {
                    throw new RecursoNaoEncontradoException("Produto", Long.valueOf(itemProduto.getProdutoId()));
                }

                // Reduzir estoque
                log.info("Reduzindo estoque do produto {} em {} unidades", produto.getNome(), itemProduto.getQuantidade());
                produto.reduzirEstoque(itemProduto.getQuantidade());
                produtoRepositorio.salvar(produto);
                log.info("Estoque atualizado: {} agora tem {} unidades", produto.getNome(), produto.getEstoque());

                // Criar venda associada à compra
                // Criar lista de produtos (repetidos pela quantidade)
                List<Produto> produtosVenda = new ArrayList<>();
                for (int i = 0; i < itemProduto.getQuantidade(); i++) {
                    produtosVenda.add(produto);
                }

                Venda venda = new Venda(
                    new VendaId(0), // Será gerado pelo banco
                    produtosVenda,
                    null, // Sem pagamento separado (incluído na compra)
                    StatusVenda.CONFIRMADA
                );

                // Salvar venda associada à compra (usando reflexão para chamar método sobrecarregado)
                try {
                    java.lang.reflect.Method salvarComCompraId = vendaRepositorio.getClass()
                        .getMethod("salvar", Venda.class, Integer.class);
                    salvarComCompraId.invoke(vendaRepositorio, venda, compra.getCompraId().getId());
                } catch (Exception e) {
                    log.warn("Erro ao associar venda à compra via reflexão, salvando sem associação", e);
                    vendaRepositorio.salvar(venda);
                }
                log.info("Venda de {} criada e associada à compra {}", produto.getNome(), compra.getCompraId().getId());
            }
        }

        // Busca a compra salva para obter os QR Codes gerados
        Compra compraCompleta = compraRepositorio.obterPorId(compra.getCompraId());
        if (compraCompleta == null) {
            throw new RecursoNaoEncontradoException("Compra", Long.valueOf(compra.getCompraId().getId()));
        }

        // Mapeia ingressos com QR Codes
        List<IngressoDTO> ingressosDTO = compraCompleta.getIngressos().stream()
            .map(ingressoMapper::toDTO)
            .collect(Collectors.toList());

        // Monta resposta com compatibilidade de campos
        Map<String, Object> response = Map.of(
            "compraId", compraCompleta.getCompraId().getId(),
            "clienteId", compraCompleta.getClienteId().getId(),
            "status", compraCompleta.getStatus().name(),
            "ingressos", ingressosDTO
        );

        log.info("Compra criada com sucesso - ID: {}", compraCompleta.getCompraId().getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela uma compra existente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelarCompra(@PathVariable Integer id) {
        log.info("Cancelando compra ID: {}", id);

        try {
            cancelarCompraUseCase.executar(new CompraId(id));
            log.info("Compra {} cancelada com sucesso", id);
            return ResponseEntity.ok(Map.of(
                "mensagem", "Compra cancelada com sucesso",
                "compraId", id
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao cancelar compra {}: {}", id, e.getMessage());
            return ResponseEntity.status(404).body(Map.of(
                "erro", e.getMessage(),
                "compraId", id
            ));
        }
    }

    /**
     * Obtém uma compra por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obterCompra(@PathVariable Integer id) {
        log.info("Buscando compra ID: {}", id);

        Compra compra = compraRepositorio.obterPorId(new CompraId(id));
        if (compra == null) {
            throw new RecursoNaoEncontradoException("Compra", Long.valueOf(id));
        }

        // Mapeia ingressos usando IngressoMapper
        List<IngressoDTO> ingressosDTO = compra.getIngressos().stream()
            .map(ingressoMapper::toDTO)
            .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
            "id", compra.getCompraId().getId(),
            "clienteId", compra.getClienteId().getId(),
            "status", compra.getStatus().name(),
            "ingressos", ingressosDTO
        );

        return ResponseEntity.ok(response);
    }
}

