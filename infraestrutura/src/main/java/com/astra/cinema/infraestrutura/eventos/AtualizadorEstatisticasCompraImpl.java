package com.astra.cinema.infraestrutura.eventos;

import com.astra.cinema.dominio.eventos.ObservadorEvento;
import com.astra.cinema.dominio.eventos.CompraConfirmadaEvento;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * PADRÃO OBSERVER - Observador Concreto (Infraestrutura)
 * Atualiza estatísticas quando uma compra é confirmada.
 * 
 * Esta classe pertence à infraestrutura porque:
 * - Persiste dados em banco de dados / cache / serviço de analytics
 * - Pode usar Redis, Elasticsearch, Google Analytics, etc.
 */
@Component
public class AtualizadorEstatisticasCompraImpl implements ObservadorEvento<CompraConfirmadaEvento> {

    // Em produção, injetaria repositório de estatísticas ou serviço de analytics
    // @Autowired
    // private EstatisticasRepository estatisticasRepository;
    
    private final AtomicInteger totalCompras = new AtomicInteger(0);
    private final AtomicInteger totalIngressos = new AtomicInteger(0);

    @Override
    public void atualizar(CompraConfirmadaEvento evento) {
        totalCompras.incrementAndGet();
        totalIngressos.addAndGet(evento.getQuantidadeIngressos());

        System.out.println("📊 [ESTATÍSTICAS] Compra registrada!");
        System.out.println("   Total de compras: " + totalCompras.get());
        System.out.println("   Total de ingressos vendidos: " + totalIngressos.get());

        // Em produção, persistiria no banco de dados:
        // Estatistica stat = new Estatistica();
        // stat.setTipo("COMPRA_CONFIRMADA");
        // stat.setCompraId(evento.getCompraId().getId());
        // stat.setQuantidadeIngressos(evento.getQuantidadeIngressos());
        // stat.setDataHora(evento.getDataHora());
        // estatisticasRepository.save(stat);
        
        // Ou enviaria para serviço de analytics:
        // analyticsService.track("purchase_completed", Map.of(
        //     "order_id", evento.getCompraId().getId(),
        //     "tickets", evento.getQuantidadeIngressos()
        // ));
    }

    @Override
    public Class<CompraConfirmadaEvento> getTipoEvento() {
        return CompraConfirmadaEvento.class;
    }

    public int getTotalCompras() {
        return totalCompras.get();
    }

    public int getTotalIngressos() {
        return totalIngressos.get();
    }
}
