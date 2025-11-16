package com.astra.cinema.config;

import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        return new AutenticarUsuarioUseCase(usuarioRepositorio);
    }
}
