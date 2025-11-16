package com.astra.cinema.interface_adapters.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RelatorioDashboardService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RelatorioDashboardService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RelatorioDashboardDTO gerar(RelatorioFiltro filtro) {
        MapSqlParameterSource params = criarParametros(filtro);
        ResumoFinanceiro resumoFinanceiro = montarResumoFinanceiro(params);
        ResumoOperacional resumoOperacional = montarResumoOperacional(params);
        List<SerieTemporalPonto> serieTemporal = montarSerieTemporal(params);
        List<RankingFilmeItem> rankingFilmes = montarRankingFilmes(params);
        List<RankingProdutoItem> rankingProdutos = montarRankingProdutos(params);

        return new RelatorioDashboardDTO(
                new RelatorioPeriodo(filtro.dataInicio(), filtro.dataFim()),
                resumoFinanceiro,
                resumoOperacional,
                serieTemporal,
                rankingFilmes,
                rankingProdutos);
    }

    private ResumoFinanceiro montarResumoFinanceiro(MapSqlParameterSource params) {
        BigDecimal faturamentoIngressos = consultarDecimal(SQL_FATURAMENTO_INGRESSOS, params);
        BigDecimal faturamentoBomboniere = consultarDecimal(SQL_FATURAMENTO_BOMBONIERE, params);
        BigDecimal faturamentoTotal = faturamentoIngressos.add(faturamentoBomboniere);

        int ingressosVendidos = consultarInteiro(SQL_INGRESSOS_VENDIDOS, params);
        int produtosVendidos = consultarInteiro(SQL_PRODUTOS_VENDIDOS, params);

        return new ResumoFinanceiro(faturamentoTotal, faturamentoIngressos, faturamentoBomboniere,
                ingressosVendidos, produtosVendidos);
    }

    private ResumoOperacional montarResumoOperacional(MapSqlParameterSource params) {
        double taxaOcupacao = consultarDouble(SQL_TAXA_OCUPACAO_MEDIA, params);
        int totalSessoes = consultarInteiro(SQL_TOTAL_SESSOES, params);
        int totalFilmes = consultarInteiro(SQL_TOTAL_FILMES, params);
        int totalFuncionarios = consultarInteiro(SQL_TOTAL_FUNCIONARIOS, params);
        int totalClientes = consultarInteiro(SQL_TOTAL_CLIENTES, params);

        return new ResumoOperacional(taxaOcupacao, totalSessoes, totalFilmes, totalFuncionarios, totalClientes);
    }

    private List<SerieTemporalPonto> montarSerieTemporal(MapSqlParameterSource params) {
        Map<LocalDate, Double> ingressosPorDia = consultarSerie(SQL_SERIE_INGRESSOS, params);
        Map<LocalDate, Double> bombonierePorDia = consultarSerie(SQL_SERIE_BOMBONIERE, params);

        Map<LocalDate, double[]> combinado = new HashMap<>();
        ingressosPorDia.forEach((dia, valor) ->
                combinado.computeIfAbsent(dia, d -> new double[2])[0] = valor);
        bombonierePorDia.forEach((dia, valor) ->
                combinado.computeIfAbsent(dia, d -> new double[2])[1] = valor);

        return combinado.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new SerieTemporalPonto(
                        entry.getKey(),
                        entry.getValue()[0],
                        entry.getValue()[1]))
                .collect(Collectors.toList());
    }

    private List<RankingFilmeItem> montarRankingFilmes(MapSqlParameterSource params) {
        return jdbcTemplate.query(SQL_RANKING_FILMES, params, (rs, rowNum) -> {
            int capacidadeTotal = rs.getInt("capacidade_total");
            long ingressos = rs.getLong("ingressos");
            double ocupacao = capacidadeTotal > 0 ? Math.min(1d, (double) ingressos / capacidadeTotal) : 0d;
            return new RankingFilmeItem(
                    rs.getInt("filme_id"),
                    rs.getString("titulo"),
                    ingressos,
                    ocupacao);
        });
    }

    private List<RankingProdutoItem> montarRankingProdutos(MapSqlParameterSource params) {
        return jdbcTemplate.query(SQL_RANKING_PRODUTOS, params, (rs, rowNum) ->
                new RankingProdutoItem(
                        rs.getInt("produto_id"),
                        rs.getString("nome"),
                        rs.getLong("quantidade"),
                        rs.getDouble("faturamento")));
    }

    public byte[] gerarPdf(RelatorioDashboardDTO relatorio) {
        return RelatorioPdfBuilder.gerar(relatorio);
    }

    private BigDecimal consultarDecimal(String sql, MapSqlParameterSource params) {
        BigDecimal valor = jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private int consultarInteiro(String sql, MapSqlParameterSource params) {
        Integer valor = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return valor != null ? valor : 0;
    }

        private double consultarDouble(String sql, MapSqlParameterSource params) {
                Double valor = jdbcTemplate.queryForObject(sql, params, Double.class);
                return valor != null ? valor : 0d;
        }

    private Map<LocalDate, Double> consultarSerie(String sql, MapSqlParameterSource params) {
        Map<LocalDate, Double> mapa = new HashMap<>();
        jdbcTemplate.query(sql, params, rs -> {
            LocalDate dia = rs.getDate("dia").toLocalDate();
            mapa.put(dia, rs.getDouble("valor"));
        });
        return mapa;
    }

    private MapSqlParameterSource criarParametros(RelatorioFiltro filtro) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmeId", filtro.filmeId());
        params.addValue("dataInicio", filtro.dataInicio() != null ? asTimestamp(filtro.dataInicio().atStartOfDay()) : null);
        params.addValue("dataFim", filtro.dataFim() != null ? asTimestamp(filtro.dataFim().plusDays(1).atStartOfDay()) : null);
        return params;
    }

    private Timestamp asTimestamp(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime);
    }

    private static final String SQL_FATURAMENTO_INGRESSOS =
            "SELECT COALESCE(SUM(p.valor),0) FROM pagamento p " +
            "JOIN compra c ON c.pagamento_id = p.id " +
            "WHERE p.status = 'SUCESSO' " +
            "AND (:dataInicio IS NULL OR c.criado_em >= :dataInicio) " +
            "AND (:dataFim IS NULL OR c.criado_em < :dataFim)";

    private static final String SQL_FATURAMENTO_BOMBONIERE =
            "SELECT COALESCE(SUM(p.valor),0) FROM pagamento p " +
            "JOIN venda v ON v.pagamento_id = p.id " +
            "WHERE p.status = 'SUCESSO' " +
            "AND (:dataInicio IS NULL OR v.criado_em >= :dataInicio) " +
            "AND (:dataFim IS NULL OR v.criado_em < :dataFim)";

    private static final String SQL_INGRESSOS_VENDIDOS =
            "SELECT COUNT(i.id) FROM ingresso i " +
            "JOIN compra c ON c.id = i.compra_id " +
            "WHERE c.status = 'CONFIRMADA' " +
            "AND (:dataInicio IS NULL OR c.criado_em >= :dataInicio) " +
            "AND (:dataFim IS NULL OR c.criado_em < :dataFim)";

    private static final String SQL_PRODUTOS_VENDIDOS =
            "SELECT COALESCE(SUM(v.quantidade),0) FROM venda v " +
            "WHERE v.status = 'CONFIRMADA' " +
            "AND (:dataInicio IS NULL OR v.criado_em >= :dataInicio) " +
            "AND (:dataFim IS NULL OR v.criado_em < :dataFim)";

    private static final String SQL_TAXA_OCUPACAO_MEDIA =
            "SELECT COALESCE(AVG(vendidos.capacidade_ratio),0) FROM (" +
            "  SELECT s.id, s.capacidade, " +
            "         CASE WHEN s.capacidade > 0 THEN CAST(COUNT(i.id) AS DOUBLE PRECISION)/s.capacidade ELSE 0 END AS capacidade_ratio " +
            "  FROM sessao s " +
            "  LEFT JOIN ingresso i ON i.sessao_id = s.id " +
            "  LEFT JOIN compra c ON c.id = i.compra_id AND c.status = 'CONFIRMADA' " +
            "  WHERE (:dataInicio IS NULL OR s.horario >= :dataInicio) " +
            "    AND (:dataFim IS NULL OR s.horario < :dataFim) " +
            "  GROUP BY s.id, s.capacidade" +
            ") vendidos";

    private static final String SQL_TOTAL_SESSOES = "SELECT COUNT(*) FROM sessao";
    private static final String SQL_TOTAL_FILMES = "SELECT COUNT(*) FROM filme WHERE status = 'EM_CARTAZ'";
    private static final String SQL_TOTAL_FUNCIONARIOS = "SELECT COUNT(*) FROM funcionario";
    private static final String SQL_TOTAL_CLIENTES = "SELECT COUNT(*) FROM cliente";

    private static final String SQL_RANKING_FILMES =
            "SELECT f.id AS filme_id, f.titulo, COUNT(i.id) AS ingressos, COALESCE(SUM(s.capacidade),0) AS capacidade_total " +
            "FROM filme f " +
            "LEFT JOIN sessao s ON s.filme_id = f.id " +
            "LEFT JOIN ingresso i ON i.sessao_id = s.id " +
            "LEFT JOIN compra c ON c.id = i.compra_id AND c.status = 'CONFIRMADA' " +
            "WHERE (:filmeId IS NULL OR f.id = :filmeId) " +
            "  AND (:dataInicio IS NULL OR s.horario >= :dataInicio) " +
            "  AND (:dataFim IS NULL OR s.horario < :dataFim) " +
            "GROUP BY f.id, f.titulo " +
            "ORDER BY ingressos DESC " +
            "LIMIT 5";

    private static final String SQL_RANKING_PRODUTOS =
            "SELECT p.id AS produto_id, p.nome, COALESCE(SUM(v.quantidade),0) AS quantidade, " +
            "       COALESCE(SUM(v.quantidade * p.preco),0) AS faturamento " +
            "FROM produto p " +
            "LEFT JOIN venda v ON v.produto_id = p.id AND v.status = 'CONFIRMADA' " +
            "WHERE (:dataInicio IS NULL OR v.criado_em >= :dataInicio OR v.criado_em IS NULL) " +
            "  AND (:dataFim IS NULL OR v.criado_em < :dataFim OR v.criado_em IS NULL) " +
            "GROUP BY p.id, p.nome " +
            "ORDER BY quantidade DESC " +
            "LIMIT 5";

    private static final String SQL_SERIE_INGRESSOS =
            "SELECT DATE(c.criado_em) AS dia, COALESCE(SUM(p.valor),0) AS valor " +
            "FROM compra c " +
            "JOIN pagamento p ON p.id = c.pagamento_id AND p.status = 'SUCESSO' " +
            "WHERE (:dataInicio IS NULL OR c.criado_em >= :dataInicio) " +
            "  AND (:dataFim IS NULL OR c.criado_em < :dataFim) " +
            "GROUP BY DATE(c.criado_em)";

    private static final String SQL_SERIE_BOMBONIERE =
            "SELECT DATE(v.criado_em) AS dia, COALESCE(SUM(p.valor),0) AS valor " +
            "FROM venda v " +
            "JOIN pagamento p ON p.id = v.pagamento_id AND p.status = 'SUCESSO' " +
            "WHERE (:dataInicio IS NULL OR v.criado_em >= :dataInicio) " +
            "  AND (:dataFim IS NULL OR v.criado_em < :dataFim) " +
            "GROUP BY DATE(v.criado_em)";

    public record RelatorioFiltro(LocalDate dataInicio, LocalDate dataFim, Integer filmeId) {}

    public record RelatorioDashboardDTO(
            RelatorioPeriodo periodo,
            ResumoFinanceiro resumoFinanceiro,
            ResumoOperacional resumoOperacional,
            List<SerieTemporalPonto> serieTemporal,
            List<RankingFilmeItem> rankingFilmes,
            List<RankingProdutoItem> rankingProdutos) {}

    public record RelatorioPeriodo(LocalDate inicio, LocalDate fim) {}

    public record ResumoFinanceiro(BigDecimal faturamentoTotal,
                                   BigDecimal faturamentoIngressos,
                                   BigDecimal faturamentoBomboniere,
                                   int ingressosVendidos,
                                   int produtosVendidos) {}

    public record ResumoOperacional(double taxaOcupacaoMedia,
                                    int totalSessoes,
                                    int totalFilmes,
                                    int totalFuncionarios,
                                    int totalClientes) {}

    public record SerieTemporalPonto(LocalDate data, double faturamentoIngressos, double faturamentoBomboniere) {}

    public record RankingFilmeItem(Integer filmeId, String titulo, long ingressos, double ocupacaoMedia) {}

    public record RankingProdutoItem(Integer produtoId, String nome, long quantidade, double faturamento) {}
}
