                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            package com.astra.cinema.config;

import com.astra.cinema.aplicacao.funcionario.ConsultarHistoricoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.RemarcarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.ValidarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.compra.CancelarCompraUseCase;
import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
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
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        return new ConsultarHistoricoFuncionarioUseCase(
            validacaoIngressoRepositorio,
            compraRepositorio,
            sessaoRepositorio,
            filmeRepositorio
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

    @Bean
    public CancelarCompraUseCase cancelarCompraUseCase(
            CompraRepositorio compraRepositorio,
            PagamentoRepositorio pagamentoRepositorio) {
        return new CancelarCompraUseCase(compraRepositorio, pagamentoRepositorio);
    }
}
