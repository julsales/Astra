package com.astra.cinema.config;

import com.astra.cinema.aplicacao.funcionario.ConsultarHistoricoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.RemarcarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.ValidarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import com.astra.cinema.dominio.operacao.ValidacaoIngressoRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        return new AutenticarUsuarioUseCase(usuarioRepositorio);
    }

    @Bean
    public ValidarIngressoFuncionarioUseCase validarIngressoFuncionarioUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            ValidacaoIngressoRepositorio validacaoIngressoRepositorio) {
        return new ValidarIngressoFuncionarioUseCase(
            compraRepositorio,
            sessaoRepositorio,
            validacaoIngressoRepositorio
        );
    }

    @Bean
    public ConsultarHistoricoFuncionarioUseCase consultarHistoricoFuncionarioUseCase(
            ValidacaoIngressoRepositorio validacaoIngressoRepositorio,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new ConsultarHistoricoFuncionarioUseCase(
            validacaoIngressoRepositorio,
            compraRepositorio,
            sessaoRepositorio
        );
    }

    @Bean
    public RemarcarIngressoFuncionarioUseCase remarcarIngressoFuncionarioUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio) {
        return new RemarcarIngressoFuncionarioUseCase(
            compraRepositorio,
            sessaoRepositorio,
            remarcacaoSessaoRepositorio
        );
    }
}
