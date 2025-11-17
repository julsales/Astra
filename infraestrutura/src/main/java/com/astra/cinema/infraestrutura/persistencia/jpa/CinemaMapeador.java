package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.FuncionarioId;
import com.astra.cinema.dominio.comum.ProdutoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapeador entre entidades de domínio e entidades JPA
 * Padrão: Mapper (separa modelo de domínio do modelo de persistência)
 * 
 * Inspirado no JpaMapeador do projeto SGB
 */
@Component
public class CinemaMapeador {

    // ==================== FILME ====================
    
    /**
     * Mapeia Filme de domínio para FilmeJpa
     */
    public FilmeJpa mapearParaFilmeJpa(Filme filme) {
        if (filme == null) {
            return null;
        }

        FilmeJpa filmeJpa = new FilmeJpa();
        
        // Só seta ID se existir E for válido (não deixa setar ID 0 ou negativo)
        if (filme.getFilmeId() != null && filme.getFilmeId().getId() > 0) {
            filmeJpa.setId(filme.getFilmeId().getId());
        }
        // Se ID for null ou inválido, deixa null para o JPA gerar automaticamente
        
        filmeJpa.setTitulo(filme.getTitulo());
        filmeJpa.setSinopse(filme.getSinopse());
        filmeJpa.setClassificacaoEtaria(filme.getClassificacaoEtaria());
        filmeJpa.setDuracao(filme.getDuracao());
        filmeJpa.setStatus(filme.getStatus());
        filmeJpa.setImagemUrl(filme.getImagemUrl());
        
        return filmeJpa;
    }

    /**
     * Mapeia FilmeJpa para Filme de domínio
     */
    public Filme mapearParaFilme(FilmeJpa filmeJpa) {
        if (filmeJpa == null) {
            return null;
        }

        FilmeId filmeId = new FilmeId(filmeJpa.getId());
        
        return new Filme(
            filmeId,
            filmeJpa.getTitulo(),
            filmeJpa.getSinopse(),
            filmeJpa.getClassificacaoEtaria(),
            filmeJpa.getDuracao(),
            filmeJpa.getImagemUrl(),
            filmeJpa.getStatus()
        );
    }

    // ==================== SESSÃO ====================
    
    /**
     * Mapeia Sessao de domínio para SessaoJpa
     */
    public SessaoJpa mapearParaSessaoJpa(Sessao sessao) {
        if (sessao == null) {
            return null;
        }

        SessaoJpa sessaoJpa = new SessaoJpa();
        
        // Só seta ID se existir E for válido
        if (sessao.getSessaoId() != null && sessao.getSessaoId().getId() > 0) {
            sessaoJpa.setId(sessao.getSessaoId().getId());
        }
        // Se ID for null ou inválido, deixa null para o JPA gerar automaticamente
        
        sessaoJpa.setFilmeId(sessao.getFilmeId().getId());
        sessaoJpa.setHorario(sessao.getHorario());
        sessaoJpa.setStatus(sessao.getStatus());
    sessaoJpa.setSala(sessao.getSala());
    sessaoJpa.setCapacidade(sessao.getCapacidade());
        
        // Mapeia o mapa de assentos (AssentoId -> Boolean) para (String -> Boolean)
        Map<String, Boolean> assentosJpa = sessao.getMapaAssentosDisponiveis().entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().getValor(),
                    Map.Entry::getValue
                ));
        
        sessaoJpa.setAssentosDisponiveis(assentosJpa);
        
        return sessaoJpa;
    }

    /**
     * Mapeia SessaoJpa para Sessao de domínio
     */
    public Sessao mapearParaSessao(SessaoJpa sessaoJpa) {
        if (sessaoJpa == null) {
            return null;
        }

        SessaoId sessaoId = new SessaoId(sessaoJpa.getId());
        FilmeId filmeId = new FilmeId(sessaoJpa.getFilmeId());
        
        // Mapeia o mapa de assentos (String -> Boolean) para (AssentoId -> Boolean)
        Map<AssentoId, Boolean> assentosDominio = sessaoJpa.getAssentosDisponiveis().entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> new AssentoId(entry.getKey()),
                    Map.Entry::getValue
                ));
        
        return new Sessao(
            sessaoId,
            filmeId,
            sessaoJpa.getHorario(),
            sessaoJpa.getStatus(),
            assentosDominio,
            sessaoJpa.getSala() != null ? sessaoJpa.getSala() : "Sala 1",
            sessaoJpa.getCapacidade() != null ? sessaoJpa.getCapacidade() : assentosDominio.size()
        );
    }

    // ==================== FUNCIONÁRIO ====================
    
    /**
     * Mapeia Funcionario de domínio para FuncionarioJpa
     */
    public FuncionarioJpa mapearParaFuncionarioJpa(Funcionario funcionario) {
        if (funcionario == null) {
            return null;
        }

        FuncionarioJpa funcionarioJpa = new FuncionarioJpa();
        if (funcionario.getFuncionarioId() != null) {
            funcionarioJpa.setId(funcionario.getFuncionarioId().getValor());
        }
        funcionarioJpa.setNome(funcionario.getNome());
        funcionarioJpa.setCargo(funcionario.getCargo());
        
        return funcionarioJpa;
    }

    /**
     * Mapeia FuncionarioJpa para Funcionario de domínio
     */
    public Funcionario mapearParaFuncionario(FuncionarioJpa funcionarioJpa) {
        if (funcionarioJpa == null) {
            return null;
        }

        FuncionarioId funcionarioId = funcionarioJpa.getId() != null ? new FuncionarioId(funcionarioJpa.getId()) : null;

        return new Funcionario(
            funcionarioId,
            funcionarioJpa.getNome(),
            funcionarioJpa.getCargo()
        );
    }

    // ==================== PRODUTO ====================
    
    /**
     * Mapeia ProdutoJpa para Produto de domínio
     */
    public Produto paraDominio(ProdutoJpa produtoJpa) {
        if (produtoJpa == null) {
            return null;
        }

        ProdutoId produtoId = new ProdutoId(produtoJpa.getId());
        
        return new Produto(
            produtoId,
            produtoJpa.getNome(),
            produtoJpa.getPreco(),
            produtoJpa.getEstoque()
        );
    }

    /**
     * Mapeia Produto de domínio para ProdutoJpa
     */
    public ProdutoJpa paraJpa(Produto produto) {
        if (produto == null) {
            return null;
        }

        return new ProdutoJpa(
            produto.getProdutoId().getId(),
            produto.getNome(),
            produto.getPreco(),
            produto.getEstoque()
        );
    }
}
