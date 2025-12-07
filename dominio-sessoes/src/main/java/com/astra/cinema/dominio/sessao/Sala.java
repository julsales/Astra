package com.astra.cinema.dominio.sessao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.SalaId;

/**
 * Entidade que representa uma Sala de Cinema no contexto de Sessões.
 * 
 * Uma sala é um espaço físico com características fixas:
 * - Identificador único (SalaId)
 * - Nome/Número (ex: "Sala 1", "Sala VIP", "Sala IMAX")
 * - Capacidade fixa (número de assentos instalados)
 * - Tipo (2D, 3D, IMAX, VIP, etc.)
 * 
 * A capacidade da sala NÃO muda entre sessões - é uma característica
 * intrínseca da infraestrutura física do cinema.
 */
public class Sala {
    private final SalaId salaId;
    private final String nome;
    private final int capacidade;
    private final TipoSala tipo;

    public Sala(SalaId salaId, String nome, int capacidade, TipoSala tipo) {
        this.salaId = salaId;
        this.nome = exigirTexto(nome, "O nome da sala não pode ser vazio");
        this.capacidade = exigirPositivo(capacidade, "A capacidade da sala deve ser positiva");
        this.tipo = tipo != null ? tipo : TipoSala.PADRAO;
    }

    public Sala(SalaId salaId, String nome, int capacidade) {
        this(salaId, nome, capacidade, TipoSala.PADRAO);
    }

    public SalaId getSalaId() {
        return salaId;
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nome + " (" + capacidade + " lugares)";
    }
}
