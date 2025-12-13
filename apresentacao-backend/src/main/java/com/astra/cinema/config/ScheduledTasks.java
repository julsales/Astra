package com.astra.cinema.config;

import com.astra.cinema.aplicacao.servicos.IngressoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarefas agendadas do sistema
 */
@Component
public class ScheduledTasks {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    
    private final IngressoService ingressoService;
    
    public ScheduledTasks(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }
    
    /**
     * Expira automaticamente ingressos de sessões que já passaram
     * Executa a cada 30 minutos
     */
    @Scheduled(fixedRate = 1800000) // 30 minutos = 1800000 ms
    public void expirarIngressosAutomaticamente() {
        try {
            log.info("Iniciando expiração automática de ingressos...");
            int quantidadeExpirada = ingressoService.expirarTodosIngressosExpirados();
            if (quantidadeExpirada > 0) {
                log.info("Expirados {} ingressos de sessões passadas", quantidadeExpirada);
            }
        } catch (Exception e) {
            log.error("Erro ao expirar ingressos automaticamente", e);
        }
    }
}
