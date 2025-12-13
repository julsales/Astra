package com.astra.cinema.aplicacao.relatorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.comum.PrecoIngresso;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

/**
 * Caso de uso para calcular filmes mais populares (top 10 por ingressos vendidos).
 * Segue o padrão DDD: Controller → Use Case → Repository → Infraestrutura
 */
public class CalcularFilmesPopularesUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public CalcularFilmesPopularesUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
        this.filmeRepositorio = exigirNaoNulo(filmeRepositorio, "O repositório de filmes não pode ser nulo");
    }

    public List<Map<String, Object>> executar() {
        List<Compra> todasCompras = compraRepositorio.listarTodas();
        
        // Contar ingressos por filme e calcular receita
        Map<Integer, Integer> ingressosPorFilme = new HashMap<>();
        Map<Integer, Double> receitaPorFilme = new HashMap<>();
        Map<Integer, String> nomesFilmes = new HashMap<>();
        
        for (Compra compra : todasCompras) {
            // Pula compras canceladas
            if (compra.getStatus() == com.astra.cinema.dominio.compra.StatusCompra.CANCELADA) {
                continue;
            }
            
            for (Ingresso ingresso : compra.getIngressos()) {
                // Pula ingressos cancelados
                if (ingresso.getStatus() == com.astra.cinema.dominio.compra.StatusIngresso.CANCELADO) {
                    continue;
                }
                
                Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
                if (sessao != null) {
                    Integer filmeId = sessao.getFilmeId().getId();
                    
                    // Contabilizar ingresso
                    ingressosPorFilme.put(filmeId, ingressosPorFilme.getOrDefault(filmeId, 0) + 1);
                    
                    // Calcular receita usando PrecoIngresso
                    double preco = ingresso.getTipo() == com.astra.cinema.dominio.compra.TipoIngresso.INTEIRA
                        ? PrecoIngresso.obterPrecoInteira()
                        : PrecoIngresso.obterPrecoMeia();
                    receitaPorFilme.put(filmeId, receitaPorFilme.getOrDefault(filmeId, 0.0) + preco);
                    
                    // Guardar nome do filme
                    if (!nomesFilmes.containsKey(filmeId)) {
                        Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
                        if (filme != null) {
                            nomesFilmes.put(filmeId, filme.getTitulo());
                        }
                    }
                }
            }
        }
        
        // Ordenar por quantidade de ingressos (top 10)
        return ingressosPorFilme.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .limit(10)
            .map(entry -> {
                Map<String, Object> filme = new HashMap<>();
                filme.put("filmeId", entry.getKey());
                filme.put("titulo", nomesFilmes.getOrDefault(entry.getKey(), "Desconhecido"));
                filme.put("ingressos", entry.getValue());
                filme.put("receita", Math.round(receitaPorFilme.getOrDefault(entry.getKey(), 0.0) * 100) / 100.0);
                return filme;
            })
            .collect(Collectors.toList());
    }
}
