package com.astra.cinema.aplicacao.relatorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.comum.PrecoIngresso;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

/**
 * Caso de uso para calcular estatísticas de vendas (ingressos + produtos).
 * Segue o padrão DDD: Controller → Use Case → Repository → Infraestrutura
 */
public class CalcularRelatorioVendasUseCase {
    private final CompraRepositorio compraRepositorio;
    private final VendaRepositorio vendaRepositorio;

    public CalcularRelatorioVendasUseCase(
            CompraRepositorio compraRepositorio,
            VendaRepositorio vendaRepositorio) {
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.vendaRepositorio = exigirNaoNulo(vendaRepositorio, "O repositório de vendas não pode ser nulo");
    }

    public Map<String, Object> executar() {
        List<Compra> todasCompras = compraRepositorio.listarTodas();
        
        // Contadores
        int totalIngressos = 0;
        int totalIngressosAtivos = 0;
        double receitaIngressos = 0.0;
        long comprasConfirmadas = 0;
        long comprasPendentes = 0;
        long comprasCanceladas = 0;
        
        for (Compra compra : todasCompras) {
            // Contar por status
            switch (compra.getStatus()) {
                case CONFIRMADA:
                    comprasConfirmadas++;
                    break;
                case PENDENTE:
                    comprasPendentes++;
                    break;
                case CANCELADA:
                    comprasCanceladas++;
                    break;
            }
            
            // Calcular receita apenas de compras não canceladas
            if (compra.getStatus() != com.astra.cinema.dominio.compra.StatusCompra.CANCELADA) {
                // Receita de ingressos
                for (Ingresso ingresso : compra.getIngressos()) {
                    totalIngressos++;
                    
                    // Só conta ingressos ativos para receita
                    if (ingresso.getStatus() != com.astra.cinema.dominio.compra.StatusIngresso.CANCELADO) {
                        totalIngressosAtivos++;
                        double precoIngresso = ingresso.getTipo() == com.astra.cinema.dominio.compra.TipoIngresso.INTEIRA 
                            ? PrecoIngresso.obterPrecoInteira() 
                            : PrecoIngresso.obterPrecoMeia();
                        receitaIngressos += precoIngresso;
                    }
                }
            }
        }
        
        // Calcular receita de produtos usando repositório
        double receitaProdutos = vendaRepositorio.calcularReceitaTotal();
        
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalCompras", todasCompras.size());
        estatisticas.put("totalIngressos", totalIngressos);
        estatisticas.put("totalIngressosAtivos", totalIngressosAtivos);
        estatisticas.put("comprasConfirmadas", comprasConfirmadas);
        estatisticas.put("comprasPendentes", comprasPendentes);
        estatisticas.put("comprasCanceladas", comprasCanceladas);
        estatisticas.put("receitaIngressos", Math.round(receitaIngressos * 100) / 100.0);
        estatisticas.put("receitaProdutos", Math.round(receitaProdutos * 100) / 100.0);
        estatisticas.put("receitaTotal", Math.round((receitaIngressos + receitaProdutos) * 100) / 100.0);
        
        return estatisticas;
    }
}
