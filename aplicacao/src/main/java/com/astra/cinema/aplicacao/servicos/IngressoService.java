package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.aplicacao.ingresso.ExpirarIngressosUseCase;
import com.astra.cinema.aplicacao.ingresso.RemarcarIngressoUseCase;
import com.astra.cinema.aplicacao.ingresso.ValidarIngressoUseCase;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.Venda;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.comum.CompraId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.operacao.RemarcacaoSessao;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para operações de Ingresso
 * Centraliza toda lógica de negócio relacionada a ingressos
 */
public class IngressoService {

    private final ValidarIngressoUseCase validarIngressoUseCase;
    private final RemarcarIngressoUseCase remarcarIngressoUseCase;
    private final ExpirarIngressosUseCase expirarIngressosUseCase;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final VendaRepositorio vendaRepositorio;
    private final RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio;

    public IngressoService(
            ValidarIngressoUseCase validarIngressoUseCase,
            RemarcarIngressoUseCase remarcarIngressoUseCase,
            ExpirarIngressosUseCase expirarIngressosUseCase,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio,
            VendaRepositorio vendaRepositorio,
            RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio) {
        this.validarIngressoUseCase = validarIngressoUseCase;
        this.remarcarIngressoUseCase = remarcarIngressoUseCase;
        this.expirarIngressosUseCase = expirarIngressosUseCase;
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.vendaRepositorio = vendaRepositorio;
        this.remarcacaoSessaoRepositorio = remarcacaoSessaoRepositorio;
    }

    /**
     * Valida um ingresso pelo QR Code
     */
    public ResultadoValidacao validarIngresso(String qrCode) {
        var resultado = validarIngressoUseCase.executar(qrCode);

        Compra compra = null;
        String todosAssentos = null;

        if (resultado.getIngresso() != null) {
            compra = compraRepositorio.buscarCompraPorQrCode(qrCode);

            if (compra != null && compra.getIngressos() != null) {
                todosAssentos = compra.getIngressos().stream()
                    .map(i -> i.getAssentoId().getValor())
                    .collect(Collectors.joining(", "));
            } else {
                todosAssentos = resultado.getIngresso().getAssentoId().getValor();
            }
        }

        return new ResultadoValidacao(
            resultado.isValido(),
            resultado.getMensagem(),
            resultado.getIngresso(),
            resultado.getSessao(),
            todosAssentos
        );
    }

    /**
     * Remarca um ingresso para nova sessão e assento
     */
    public void remarcarIngresso(String qrCode, SessaoId novaSessaoId, AssentoId novoAssentoId) {
        remarcarIngressoUseCase.executar(qrCode, novaSessaoId, novoAssentoId);
    }

    /**
     * Expira ingressos ativos de uma sessão que já passou
     */
    public int expirarIngressosDaSessao(SessaoId sessaoId) {
        return expirarIngressosUseCase.executarParaSessao(sessaoId);
    }

    /**
     * Expira ingressos ativos de todas as sessões que já passaram
     */
    public int expirarTodosIngressosExpirados() {
        return expirarIngressosUseCase.executarParaTodasSessoes();
    }

    /**
     * Busca ingressos de um cliente, agrupados por sessão
     */
    public List<IngressoDetalhado> buscarIngressosPorCliente(Integer clienteId) {
        List<Compra> compras;

        if (clienteId != null) {
            compras = compraRepositorio.buscarPorCliente(new ClienteId(clienteId));
        } else {
            compras = new ArrayList<>();
        }

        return processarComprasParaIngressos(compras);
    }

    /**
     * Busca apenas ingressos ativos de um cliente
     */
    public List<IngressoDetalhado> buscarIngressosAtivosPorCliente(Integer clienteId) {
        List<Compra> compras;

        if (clienteId != null) {
            compras = compraRepositorio.buscarPorCliente(new ClienteId(clienteId));
        } else {
            compras = compraRepositorio.listarTodas();
        }

        compras = compras.stream()
            .filter(compra -> compra.getIngressos().stream()
                .anyMatch(ing -> ing.getStatus() == StatusIngresso.ATIVO))
            .collect(Collectors.toList());

        return processarComprasParaIngressosAtivos(compras);
    }

    /**
     * Busca produtos associados a uma compra
     */
    public List<ProdutoVenda> buscarProdutosDaCompra(CompraId compraId) {
        try {
            List<Venda> vendas = vendaRepositorio.buscarPorCompra(compraId);
            List<ProdutoVenda> produtos = new ArrayList<>();

            for (Venda venda : vendas) {
                Map<String, Integer> contagemPorProduto = new HashMap<>();
                Map<String, Produto> produtoPorNome = new HashMap<>();

                for (Produto produto : venda.getProdutos()) {
                    String nome = produto.getNome();
                    contagemPorProduto.put(nome, contagemPorProduto.getOrDefault(nome, 0) + 1);
                    produtoPorNome.put(nome, produto);
                }

                for (Map.Entry<String, Integer> entry : contagemPorProduto.entrySet()) {
                    String nome = entry.getKey();
                    Integer quantidade = entry.getValue();
                    Produto produto = produtoPorNome.get(nome);

                    produtos.add(new ProdutoVenda(
                        produto.getProdutoId().getId(),
                        produto.getNome(),
                        produto.getPreco(),
                        quantidade
                    ));
                }
            }

            return produtos;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<IngressoDetalhado> processarComprasParaIngressos(List<Compra> compras) {
        return compras.stream()
            .flatMap(compra -> {
                List<Ingresso> todosIngressos = compra.getIngressos();
                if (todosIngressos.isEmpty()) return java.util.stream.Stream.empty();

                Map<SessaoId, List<Ingresso>> ingressosPorSessao = todosIngressos.stream()
                    .collect(Collectors.groupingBy(Ingresso::getSessaoId));

                return ingressosPorSessao.values().stream()
                    .map(grupo -> construirIngressoDetalhado(compra, grupo));
            })
            .collect(Collectors.toList());
    }

    private List<IngressoDetalhado> processarComprasParaIngressosAtivos(List<Compra> compras) {
        return compras.stream()
            .flatMap(compra -> {
                List<Ingresso> grupo = compra.getIngressos().stream()
                    .filter(ing -> ing.getStatus() == StatusIngresso.ATIVO)
                    .collect(Collectors.toList());

                if (grupo.isEmpty()) return java.util.stream.Stream.empty();

                Map<SessaoId, List<Ingresso>> ingressosPorSessao = grupo.stream()
                    .collect(Collectors.groupingBy(Ingresso::getSessaoId));

                return ingressosPorSessao.values().stream()
                    .map(g -> construirIngressoDetalhado(compra, g));
            })
            .collect(Collectors.toList());
    }

    private IngressoDetalhado construirIngressoDetalhado(Compra compra, List<Ingresso> grupo) {
        Ingresso ingressoPrincipal = grupo.stream()
            .min(Comparator.comparing(ing -> ing.getIngressoId().getId()))
            .orElse(grupo.get(0));

        String todosAssentos = grupo.stream()
            .map(ing -> ing.getAssentoId().getValor())
            .collect(Collectors.joining(", "));

        List<String> listaAssentos = grupo.stream()
            .map(ing -> ing.getAssentoId().getValor())
            .collect(Collectors.toList());

        double valorIngressos = grupo.stream()
            .mapToDouble(ing -> ing.getTipo() == com.astra.cinema.dominio.compra.TipoIngresso.INTEIRA ? 35.0 : 17.5)
            .sum();

        List<IngressoDetalhe> ingressosDetalhados = grupo.stream()
            .map(ing -> new IngressoDetalhe(ing.getAssentoId().getValor(), ing.getTipo().name()))
            .collect(Collectors.toList());

        List<ProdutoVenda> produtos = buscarProdutosDaCompra(compra.getCompraId());

        double valorProdutos = produtos.stream()
            .mapToDouble(p -> p.preco() * p.quantidade())
            .sum();

        double total = valorIngressos + valorProdutos;

        Sessao sessao = null;
        Filme filme = null;
        try {
            sessao = sessaoRepositorio.obterPorId(ingressoPrincipal.getSessaoId());
            if (sessao != null) {
                filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
            }
        } catch (Exception ignored) {}

        HistoricoRemarcacao historico = null;
        try {
            List<RemarcacaoSessao> remarcacoes = remarcacaoSessaoRepositorio.listarPorIngresso(ingressoPrincipal.getIngressoId());
            if (!remarcacoes.isEmpty()) {
                RemarcacaoSessao remarcacaoRecente = remarcacoes.stream()
                    .max(Comparator.comparing(RemarcacaoSessao::getDataHoraRemarcacao))
                    .orElse(remarcacoes.get(0));

                Sessao sessaoOriginal = null;
                Filme filmeOriginal = null;
                try {
                    sessaoOriginal = sessaoRepositorio.obterPorId(remarcacaoRecente.getSessaoOriginal());
                    if (sessaoOriginal != null) {
                        filmeOriginal = filmeRepositorio.obterPorId(sessaoOriginal.getFilmeId());
                    }
                } catch (Exception ignored) {}

                historico = new HistoricoRemarcacao(
                    remarcacaoRecente.getDataHoraRemarcacao().toString(),
                    remarcacaoRecente.getMotivoTecnico(),
                    remarcacaoRecente.getAssentoOriginal() != null ? remarcacaoRecente.getAssentoOriginal().getValor() : null,
                    sessaoOriginal != null ? sessaoOriginal.getHorario().toString() : null,
                    sessaoOriginal != null ? "Sala " + sessaoOriginal.getSalaId().getId() : null,
                    filmeOriginal != null ? filmeOriginal.getTitulo() : null
                );
            }
        } catch (Exception ignored) {}

        return new IngressoDetalhado(
            ingressoPrincipal.getIngressoId().getId(),
            ingressoPrincipal.getQrCode(),
            ingressoPrincipal.getSessaoId().getId(),
            todosAssentos,
            listaAssentos,
            ingressoPrincipal.getAssentoId().getValor(),
            ingressoPrincipal.getTipo().name(),
            ingressoPrincipal.getStatus().name(),
            ingressoPrincipal.getStatus() == StatusIngresso.VALIDADO,
            historico != null,
            total,
            sessao != null ? sessao.getHorario().toString() : null,
            sessao != null ? sessao.getSalaId().getId() : null,
            sessao != null ? "Sala " + sessao.getSalaId().getId() : null,
            filme != null ? filme.getFilmeId().getId() : null,
            filme != null ? filme.getTitulo() : null,
            filme != null ? filme.getSinopse() : null,
            filme != null ? filme.getClassificacaoEtaria() : null,
            filme != null ? filme.getDuracao() : null,
            ingressosDetalhados,
            produtos,
            historico
        );
    }

    // Classes de resultado
    public record ResultadoValidacao(
        boolean valido,
        String mensagem,
        Ingresso ingresso,
        Sessao sessao,
        String todosAssentos
    ) {}

    public record IngressoDetalhado(
        int id,
        String qrCode,
        int sessaoId,
        String assento,
        List<String> assentos,
        String assentoIndividual,
        String tipo,
        String status,
        boolean foiValidado,
        boolean remarcado,
        double total,
        String horario,
        Integer salaId,
        String sala,
        Integer filmeId,
        String filmeTitulo,
        String filmeSinopse,
        String filmeClassificacaoEtaria,
        Integer filmeDuracao,
        List<IngressoDetalhe> ingressosDetalhados,
        List<ProdutoVenda> produtos,
        HistoricoRemarcacao historicoRemarcacao
    ) {}

    public record IngressoDetalhe(String assento, String tipo) {}

    public record ProdutoVenda(int id, String nome, double preco, int quantidade) {}

    public record HistoricoRemarcacao(
        String dataRemarcacao,
        String motivo,
        String assentoOriginal,
        String sessaoOriginalHorario,
        String sessaoOriginalSala,
        String sessaoOriginalFilme
    ) {}
}
