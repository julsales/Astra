package com.astra.cinema.infraestrutura.config;

import com.astra.cinema.aplicacao.bomboniere.AdicionarProdutoUseCase;
import com.astra.cinema.aplicacao.bomboniere.ModificarProdutoUseCase;
import com.astra.cinema.aplicacao.bomboniere.RemoverProdutoUseCase;
import com.astra.cinema.aplicacao.filme.AdicionarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.AlterarFilmeUseCase;
import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;
import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.ModificarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemarcarIngressosSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemoverSessaoUseCase;
import com.astra.cinema.aplicacao.usuario.GerenciarCinemaUseCase;
import com.astra.cinema.aplicacao.usuario.funcionario.GerenciarFuncionariosUseCase;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.FuncionarioRepositorio;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de beans Spring
 * Define os Use Cases e Serviços de Domínio
 * 
 * Padrão: Dependency Injection Container
 * Inspirado no BackendAplicacao do SGB
 */
@Configuration
public class AplicacaoConfig {

    /**
     * Bean para o Use Case de Remover Filme
     */
    @Bean
    public RemoverFilmeUseCase removerFilmeUseCase(
            FilmeRepositorio filmeRepositorio,
            SessaoRepositorio sessaoRepositorio) {
        return new RemoverFilmeUseCase(filmeRepositorio, sessaoRepositorio);
    }

    /**
     * Bean para o Use Case de Criar Sessão
     */
    @Bean
    public CriarSessaoUseCase criarSessaoUseCase(
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        return new CriarSessaoUseCase(sessaoRepositorio, filmeRepositorio);
    }

    /**
     * Bean para o Use Case de Gerenciar Cinema (Controle de Acesso)
     */
    @Bean
    public GerenciarCinemaUseCase gerenciarCinemaUseCase() {
        return new GerenciarCinemaUseCase();
    }

    /**
     * Bean para o Use Case de gerenciamento de funcionários
     */
    @Bean
    public GerenciarFuncionariosUseCase gerenciarFuncionariosUseCase(
            FuncionarioRepositorio funcionarioRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        return new GerenciarFuncionariosUseCase(funcionarioRepositorio, usuarioRepositorio);
    }

    // ==================== FILMES ====================

    /**
     * Bean para o Use Case de Adicionar Filme
     */
    @Bean
    public AdicionarFilmeUseCase adicionarFilmeUseCase(FilmeRepositorio filmeRepositorio) {
        return new AdicionarFilmeUseCase(filmeRepositorio);
    }

    /**
     * Bean para o Use Case de Alterar Filme
     */
    @Bean
    public AlterarFilmeUseCase alterarFilmeUseCase(FilmeRepositorio filmeRepositorio) {
        return new AlterarFilmeUseCase(filmeRepositorio);
    }

    // ==================== SESSÕES ====================

    /**
     * Bean para o Use Case de Modificar Sessão
     */
    @Bean
    public ModificarSessaoUseCase modificarSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        return new ModificarSessaoUseCase(sessaoRepositorio);
    }

    /**
     * Bean para o Use Case de Remover Sessão
     */
    @Bean
    public RemoverSessaoUseCase removerSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        return new RemoverSessaoUseCase(sessaoRepositorio);
    }

    /**
     * Bean para o Use Case de Remarcar ingressos de uma sessão
     */
    @Bean
    public RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        return new RemarcarIngressosSessaoUseCase(sessaoRepositorio);
    }

    // ==================== PRODUTOS (BOMBONIERE) ====================

    /**
     * Bean para o Use Case de Adicionar Produto
     */
    @Bean
    public AdicionarProdutoUseCase adicionarProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        return new AdicionarProdutoUseCase(produtoRepositorio);
    }

    /**
     * Bean para o Use Case de Modificar Produto
     */
    @Bean
    public ModificarProdutoUseCase modificarProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        return new ModificarProdutoUseCase(produtoRepositorio);
    }

    /**
     * Bean para o Use Case de Remover Produto
     */
    @Bean
    public RemoverProdutoUseCase removerProdutoUseCase(ProdutoRepositorio produtoRepositorio) {
        return new RemoverProdutoUseCase(produtoRepositorio);
    }
}
