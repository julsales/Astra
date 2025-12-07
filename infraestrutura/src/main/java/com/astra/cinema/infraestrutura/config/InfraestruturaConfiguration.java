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
     * Registra observadores de eventos no PublicadorEventos (Singleton)
     * ao iniciar a aplicação.
     */
    @Bean
    public CommandLineRunner registrarObservadores(
            NotificadorEmailCompraImpl notificadorEmail,
            AtualizadorEstatisticasCompraImpl atualizadorEstatisticas) {
        
        return args -> {
            PublicadorEventos publicador = PublicadorEventos.getInstancia();
            
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
