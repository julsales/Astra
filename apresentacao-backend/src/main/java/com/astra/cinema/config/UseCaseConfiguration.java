                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            package com.astra.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.astra.cinema.aplicacao.bomboniere.AdicionarProdutoUseCase;
import com.astra.cinema.aplicacao.bomboniere.ModificarProdutoUseCase;
import com.astra.cinema.aplicacao.bomboniere.RemoverProdutoUseCase;
import com.astra.cinema.aplicacao.compra.CancelarCompraUseCase;
import com.astra.cinema.aplicacao.compra.IniciarCompraUseCase;
import com.astra.cinema.aplicacao.filme.AdicionarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.AlterarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;
import com.astra.cinema.aplicacao.funcionario.ConsultarHistoricoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.RemarcarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.funcionario.ValidarIngressoFuncionarioUseCase;
import com.astra.cinema.aplicacao.ingresso.RemarcarIngressoUseCase;
import com.astra.cinema.aplicacao.ingresso.ValidarIngressoUseCase;
import com.astra.cinema.aplicacao.relatorio.CalcularAnalyticsUseCase;
import com.astra.cinema.aplicacao.relatorio.CalcularFilmesPopularesUseCase;
import com.astra.cinema.aplicacao.relatorio.CalcularOcupacaoSalasUseCase;
import com.astra.cinema.aplicacao.relatorio.CalcularRelatorioVendasUseCase;
import com.astra.cinema.aplicacao.servicos.BomboniereService;
import com.astra.cinema.aplicacao.servicos.ClienteService;
import com.astra.cinema.aplicacao.servicos.CompraAppService;
import com.astra.cinema.aplicacao.servicos.FilmeService;
import com.astra.cinema.aplicacao.servicos.IngressoService;
import com.astra.cinema.aplicacao.servicos.SessaoService;
import com.astra.cinema.aplicacao.servicos.VendaProdutoService;
import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.ModificarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemarcarIngressosSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemoverSessaoUseCase;
import com.astra.cinema.aplicacao.usuario.AutenticarUsuarioUseCase;
import com.astra.cinema.aplicacao.usuario.RegistrarClienteUseCase;
import com.astra.cinema.aplicacao.usuario.funcionario.GerenciarFuncionariosUseCase;
import com.astra.cinema.apresentacao.servicos.VendaProdutoServiceImpl;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.bomboniere.VendaRepositorio;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import com.astra.cinema.dominio.operacao.ValidacaoIngressoRepositorio;
import com.astra.cinema.dominio.sessao.SalaRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.ClienteRepositorio;
import com.astra.cinema.dominio.usuario.FuncionarioRepositorio;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import com.astra.cinema.infraestrutura.persistencia.jpa.VendaJpaRepository;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, FuncionarioRepositorio funcionarioRepositorio) {
        return new AutenticarUsuarioUseCase(usuarioRepositorio, funcionarioRepositorio);
    }

    @Bean
    public RegistrarClienteUseCase registrarClienteUseCase(
            UsuarioRepositorio usuarioRepositorio,
            ClienteRepositorio clienteRepositorio) {
        return new RegistrarClienteUseCase(usuarioRepositorio, clienteRepositorio);
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
    public IniciarCompraUseCase iniciarCompraUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new IniciarCompraUseCase(compraRepositorio, sessaoRepositorio);
    }

    @Bean
    public CancelarCompraUseCase cancelarCompraUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new CancelarCompraUseCase(compraRepositorio, sessaoRepositorio);
    }

    // Filme Use Cases
    @Bean
    public AdicionarFilmeUseCase adicionarFilmeUseCase(FilmeRepositorio filmeRepositorio) {
        return new AdicionarFilmeUseCase(filmeRepositorio);
    }

    @Bean
    public AlterarFilmeUseCase alterarFilmeUseCase(
            FilmeRepositorio filmeRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new AlterarFilmeUseCase(filmeRepositorio, sessaoRepositorio);
    }

    @Bean
    public RemoverFilmeUseCase removerFilmeUseCase(
            FilmeRepositorio filmeRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new RemoverFilmeUseCase(filmeRepositorio, sessaoRepositorio);
    }

    // Sessão Use Cases
    @Bean
    public CriarSessaoUseCase criarSessaoUseCase(
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        return new CriarSessaoUseCase(sessaoRepositorio, filmeRepositorio);
    }

    @Bean
    public ModificarSessaoUseCase modificarSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        return new ModificarSessaoUseCase(sessaoRepositorio);
    }

    @Bean
    public RemoverSessaoUseCase removerSessaoUseCase(SessaoRepositorio sessaoRepositorio, FilmeRepositorio filmeRepositorio) {
        return new RemoverSessaoUseCase(sessaoRepositorio, filmeRepositorio);
    }

    @Bean
    public RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        return new RemarcarIngressosSessaoUseCase(sessaoRepositorio);
    }

    // Ingresso Use Cases
    @Bean
    public ValidarIngressoUseCase validarIngressoUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new ValidarIngressoUseCase(compraRepositorio, sessaoRepositorio);
    }

    @Bean
    public RemarcarIngressoUseCase remarcarIngressoUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new RemarcarIngressoUseCase(compraRepositorio, sessaoRepositorio);
    }

    // Produto Use Cases
    @Bean
    public AdicionarProdutoUseCase adicionarProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        return new AdicionarProdutoUseCase(produtoRepositorio);
    }

    @Bean
    public ModificarProdutoUseCase modificarProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        return new ModificarProdutoUseCase(produtoRepositorio);
    }

    @Bean
    public RemoverProdutoUseCase removerProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        return new RemoverProdutoUseCase(produtoRepositorio);
    }

    // Funcionário Use Cases
    @Bean
    public GerenciarFuncionariosUseCase gerenciarFuncionariosUseCase(
            FuncionarioRepositorio funcionarioRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        return new GerenciarFuncionariosUseCase(funcionarioRepositorio, usuarioRepositorio);
    }

    // ========== NOVOS SERVIÇOS DE APLICAÇÃO ==========

    @Bean
    public VendaProdutoService vendaProdutoService(VendaJpaRepository vendaJpaRepository) {
        return new VendaProdutoServiceImpl(vendaJpaRepository);
    }

    @Bean
    public IngressoService ingressoService(
            ValidarIngressoUseCase validarIngressoUseCase,
            RemarcarIngressoUseCase remarcarIngressoUseCase,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio,
            VendaRepositorio vendaRepositorio,
            RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio) {
        return new IngressoService(
                validarIngressoUseCase,
                remarcarIngressoUseCase,
                compraRepositorio,
                sessaoRepositorio,
                filmeRepositorio,
                vendaRepositorio,
                remarcacaoSessaoRepositorio
        );
    }

    @Bean
    public BomboniereService bomboniereService(
            ProdutoRepositorio produtoRepositorio,
            VendaRepositorio vendaRepositorio) {
        return new BomboniereService(produtoRepositorio, vendaRepositorio);
    }

    @Bean
    public FilmeService filmeService(
            FilmeRepositorio filmeRepositorio,
            SessaoRepositorio sessaoRepositorio,
            AdicionarFilmeUseCase adicionarFilmeUseCase,
            AlterarFilmeUseCase alterarFilmeUseCase,
            RemoverFilmeUseCase removerFilmeUseCase) {
        return new FilmeService(
                filmeRepositorio,
                sessaoRepositorio,
                adicionarFilmeUseCase,
                alterarFilmeUseCase,
                removerFilmeUseCase
        );
    }

    @Bean
    public SessaoService sessaoService(
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio,
            SalaRepositorio salaRepositorio,
            CriarSessaoUseCase criarSessaoUseCase,
            ModificarSessaoUseCase modificarSessaoUseCase,
            RemoverSessaoUseCase removerSessaoUseCase,
            RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase) {
        return new SessaoService(
                sessaoRepositorio,
                filmeRepositorio,
                salaRepositorio,
                criarSessaoUseCase,
                modificarSessaoUseCase,
                removerSessaoUseCase,
                remarcarIngressosSessaoUseCase
        );
    }

    @Bean
    public ClienteService clienteService(
            UsuarioRepositorio usuarioRepositorio,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        return new ClienteService(
                usuarioRepositorio,
                compraRepositorio,
                sessaoRepositorio,
                filmeRepositorio
        );
    }

    @Bean
    public CompraAppService compraAppService(
            IniciarCompraUseCase iniciarCompraUseCase,
            CancelarCompraUseCase cancelarCompraUseCase,
            CompraRepositorio compraRepositorio,
            ProdutoRepositorio produtoRepositorio,
            VendaProdutoService vendaProdutoService) {
        return new CompraAppService(
                iniciarCompraUseCase,
                cancelarCompraUseCase,
                compraRepositorio,
                produtoRepositorio,
                vendaProdutoService
        );
    }

    // ========== RELATÓRIOS USE CASES ==========

    @Bean
    public CalcularRelatorioVendasUseCase calcularRelatorioVendasUseCase(
            CompraRepositorio compraRepositorio,
            VendaRepositorio vendaRepositorio) {
        return new CalcularRelatorioVendasUseCase(compraRepositorio, vendaRepositorio);
    }

    @Bean
    public CalcularFilmesPopularesUseCase calcularFilmesPopularesUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        return new CalcularFilmesPopularesUseCase(compraRepositorio, sessaoRepositorio, filmeRepositorio);
    }

    @Bean
    public CalcularAnalyticsUseCase calcularAnalyticsUseCase(
            VendaRepositorio vendaRepositorio,
            ProdutoRepositorio produtoRepositorio) {
        return new CalcularAnalyticsUseCase(vendaRepositorio, produtoRepositorio);
    }

    @Bean
    public CalcularOcupacaoSalasUseCase calcularOcupacaoSalasUseCase(
            SessaoRepositorio sessaoRepositorio) {
        return new CalcularOcupacaoSalasUseCase(sessaoRepositorio);
    }
}
