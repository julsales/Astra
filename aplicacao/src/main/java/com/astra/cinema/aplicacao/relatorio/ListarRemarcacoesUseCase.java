package com.astra.cinema.aplicacao.relatorio;

import com.astra.cinema.dominio.operacao.RemarcacaoSessao;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;

import java.util.List;

/**
 * Use Case: Listar remarcações de sessões
 * Responsabilidade: Obter histórico de remarcações para relatórios
 */
public class ListarRemarcacoesUseCase {
    
    private final RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio;
    
    public ListarRemarcacoesUseCase(RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio) {
        if (remarcacaoSessaoRepositorio == null) {
            throw new IllegalArgumentException("RemarcacaoSessaoRepositorio não pode ser nulo");
        }
        this.remarcacaoSessaoRepositorio = remarcacaoSessaoRepositorio;
    }
    
    public List<RemarcacaoSessao> executar() {
        return remarcacaoSessaoRepositorio.listarTodas();
    }
}
