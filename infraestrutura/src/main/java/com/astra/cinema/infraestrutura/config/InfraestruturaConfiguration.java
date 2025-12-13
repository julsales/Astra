package com.astra.cinema.infraestrutura.config;

import com.astra.cinema.dominio.eventos.ObservadorEvento;
import com.astra.cinema.dominio.eventos.PublicadorEventos;
import com.astra.cinema.dominio.eventos.CompraConfirmadaEvento;
import com.astra.cinema.infraestrutura.eventos.AtualizadorEstatisticasCompraImpl;
import com.astra.cinema.infraestrutura.eventos.NotificadorEmailCompraImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de Infraestrutura
 * 
 * Registra implementações concretas de serviços de infraestrutura no container Spring.
 * Esta classe pertence à camada de infraestrutura e faz a ligação entre
 * as abstrações do domínio e as implementações técnicas.
 */
@Configuration
public class InfraestruturaConfiguration {

    /**
     * Cria o PublicadorEventos como bean gerenciado pelo Spring.
     * CORREÇÃO: Não é mais singleton, gerenciado por DI.
     */
    @Bean
    public PublicadorEventos publicadorEventos() {
        return new PublicadorEventos();
    }

    /**
     * Registra observadores de eventos no PublicadorEventos
     * ao iniciar a aplicação.
     */
    @Bean
    public CommandLineRunner registrarObservadores(
            PublicadorEventos publicador,
            NotificadorEmailCompraImpl notificadorEmail,
            AtualizadorEstatisticasCompraImpl atualizadorEstatisticas) {
        
        return args -> {
            
            // Registrar observadores para CompraConfirmadaEvento
            publicador.registrar(notificadorEmail);
            publicador.registrar(atualizadorEstatisticas);
            
            System.out.println("✅ Observadores de eventos registrados:");
            System.out.println("   - NotificadorEmailCompraImpl");
            System.out.println("   - AtualizadorEstatisticasCompraImpl");
        };
    }
    
    // Os beans NotificadorEmailCompraImpl e AtualizadorEstatisticasCompraImpl
    // são criados automaticamente pelo Spring via @Component
    
    // Os ProcessadorPagamento são criados via @Component("processadorXXX")
    // e podem ser injetados onde necessário usando @Qualifier
}
