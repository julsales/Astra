package com.astra.cinema.aplicacao.relatorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

public class CalcularOcupacaoSalasUseCase {

    private final SessaoRepositorio sessaoRepositorio;

    public CalcularOcupacaoSalasUseCase(SessaoRepositorio sessaoRepositorio) {
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public List<Map<String, Object>> executar() {
        try {
            List<Sessao> todasSessoes = sessaoRepositorio.listarTodas();
            
            // Agrupar por sala
            Map<Integer, List<Sessao>> sessoesPorSala = todasSessoes.stream()
                .collect(Collectors.groupingBy(s -> s.getSalaId().getId()));
            
            List<Map<String, Object>> salas = new ArrayList<>();
            for (Map.Entry<Integer, List<Sessao>> entry : sessoesPorSala.entrySet()) {
                Map<String, Object> sala = new HashMap<>();
                sala.put("salaId", entry.getKey());
                sala.put("totalSessoes", entry.getValue().size());
                
                // Calcular ocupação média
                int totalAssentos = 0;
                int assentosOcupados = 0;
                
                for (Sessao sessao : entry.getValue()) {
                    Map<com.astra.cinema.dominio.comum.AssentoId, Boolean> assentos = sessao.getMapaAssentosDisponiveis();
                    totalAssentos += assentos.size();
                    assentosOcupados += (int) assentos.values().stream().filter(ocupado -> !ocupado).count();
                }
                
                double taxaOcupacao = totalAssentos > 0 ? (assentosOcupados * 100.0 / totalAssentos) : 0;
                sala.put("taxaOcupacao", Math.round(taxaOcupacao * 100) / 100.0);
                sala.put("totalAssentos", totalAssentos);
                sala.put("assentosOcupados", assentosOcupados);
                
                salas.add(sala);
            }
            
            // Ordenar por sala
            salas.sort((a, b) -> ((Integer)a.get("salaId")).compareTo((Integer)b.get("salaId")));
            
            return salas;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
