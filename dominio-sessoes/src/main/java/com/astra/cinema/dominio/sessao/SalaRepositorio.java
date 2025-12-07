package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.SalaId;
import java.util.List;

/**
 * Repositório para persistência de Salas de Cinema.
 * 
 * Salas são entidades de infraestrutura do cinema que raramente mudam.
 * Normalmente são cadastradas na inicialização do sistema.
 */
public interface SalaRepositorio {
    
    /**
     * Salva ou atualiza uma sala.
     */
    Sala salvar(Sala sala);
    
    /**
     * Busca uma sala pelo seu identificador.
     */
    Sala obterPorId(SalaId salaId);
    
    /**
     * Busca uma sala pelo nome.
     */
    Sala obterPorNome(String nome);
    
    /**
     * Lista todas as salas disponíveis no cinema.
     */
    List<Sala> listarTodas();
    
    /**
     * Remove uma sala (raro - apenas em reforma/desativação).
     */
    void remover(SalaId salaId);
}
