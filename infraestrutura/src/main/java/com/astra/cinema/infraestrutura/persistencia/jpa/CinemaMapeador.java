package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public SessaoJpa mapearParaSessaoJpa(Sessao sessao, SalaJpa sala) {
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
        sessaoJpa.setSala(sala);
        
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
        SalaId salaId = sessaoJpa.getSala() != null ? 
            new SalaId(sessaoJpa.getSala().getId()) : 
            new SalaId(1); // fallback temporário
        
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
            salaId
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

    // ==================== COMPRA ====================
    
    /**
     * Mapeia Compra de domínio para CompraJpa
     */
    public CompraJpa mapearParaCompraJpa(Compra compra) {
        if (compra == null) {
            return null;
        }

        CompraJpa compraJpa = new CompraJpa();
        
        // Para novas compras, sempre deixa o ID como null para o JPA gerar automaticamente
        // O ID só é setado quando buscamos uma compra existente do banco
        // IDs temporários gerados com System.identityHashCode não devem ser usados aqui
        // (o JPA vai gerar o ID real ao salvar)
        
        compraJpa.setClienteId(compra.getClienteId().getId());
        compraJpa.setStatus(compra.getStatus().name());
        compraJpa.setPagamentoId(compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null);
        
        return compraJpa;
    }

    /**
     * Mapeia CompraJpa para Compra de domínio
     */
    public Compra mapearParaCompra(CompraJpa compraJpa, List<Ingresso> ingressos) {
        if (compraJpa == null) {
            return null;
        }

        // ...código original sem debug...

        CompraId compraId = new CompraId(compraJpa.getId());
        ClienteId clienteId = new ClienteId(compraJpa.getClienteId());
        PagamentoId pagamentoId = compraJpa.getPagamentoId() != null 
                ? new PagamentoId(compraJpa.getPagamentoId()) 
                : null;
        StatusCompra status = StatusCompra.valueOf(compraJpa.getStatus());

        return new Compra(compraId, clienteId, ingressos, pagamentoId, status);
    }

    // ==================== INGRESSO ====================
    
    /**
     * Mapeia Ingresso de domínio para IngressoJpa
     */
    public IngressoJpa mapearParaIngressoJpa(Ingresso ingresso, Integer compraId) {
        if (ingresso == null) {
            return null;
        }

        IngressoJpa ingressoJpa = new IngressoJpa();
        
        if (ingresso.getIngressoId() != null && ingresso.getIngressoId().getId() > 0) {
            ingressoJpa.setId(ingresso.getIngressoId().getId());
        }
        
        ingressoJpa.setCompraId(compraId);
        ingressoJpa.setSessaoId(ingresso.getSessaoId().getId());
        ingressoJpa.setAssento(ingresso.getAssentoId().getValor());
        ingressoJpa.setTipo(ingresso.getTipo().name());
        ingressoJpa.setStatus(ingresso.getStatus().name());
        ingressoJpa.setQrCode(ingresso.getQrCode()); // Salva o QR Code do banco
        
        return ingressoJpa;
    }

    /**
     * Mapeia IngressoJpa para Ingresso de domínio
     */
    public Ingresso mapearParaIngresso(IngressoJpa ingressoJpa) {
        if (ingressoJpa == null) {
            return null;
        }

        IngressoId ingressoId = new IngressoId(ingressoJpa.getId());
        SessaoId sessaoId = new SessaoId(ingressoJpa.getSessaoId());
        AssentoId assentoId = new AssentoId(ingressoJpa.getAssento());
        TipoIngresso tipo = TipoIngresso.valueOf(ingressoJpa.getTipo());
        StatusIngresso status = StatusIngresso.valueOf(ingressoJpa.getStatus());
        
        // Usa o QR Code do banco (gerado pelo backend)
        String qrCode = ingressoJpa.getQrCode();
        // Se não houver QR Code no banco (para ingressos antigos), gera um baseado no ID
        if (qrCode == null || qrCode.isEmpty()) {
            qrCode = "ASTRA" + ingressoJpa.getId();
        }

        return new Ingresso(ingressoId, sessaoId, assentoId, tipo, status, qrCode);
    }
}
