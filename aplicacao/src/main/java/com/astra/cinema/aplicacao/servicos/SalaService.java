package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.dominio.comum.SalaId;
import com.astra.cinema.dominio.sessao.Sala;
import com.astra.cinema.dominio.sessao.SalaRepositorio;

import java.util.List;

/**
 * Service para operações de Sala
 * Camada de aplicação que orquestra operações relacionadas a salas
 */
public class SalaService {
    
    private final SalaRepositorio salaRepositorio;
    
    public SalaService(SalaRepositorio salaRepositorio) {
        if (salaRepositorio == null) {
            throw new IllegalArgumentException("SalaRepositorio não pode ser nulo");
        }
        this.salaRepositorio = salaRepositorio;
    }
    
    /**
     * Lista todas as salas disponíveis
     */
    public List<Sala> listarTodasSalas() {
        return salaRepositorio.listarTodas();
    }
    
    /**
     * Obtém uma sala por ID
     */
    public Sala obterSalaPorId(SalaId salaId) {
        if (salaId == null) {
            throw new IllegalArgumentException("SalaId não pode ser nulo");
        }
        return salaRepositorio.obterPorId(salaId);
    }
    
    /**
     * Salva uma sala
     */
    public void salvarSala(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("Sala não pode ser nula");
        }
        salaRepositorio.salvar(sala);
    }
}
