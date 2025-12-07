package com.astra.cinema.dominio.sessao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entidade Sessão - Representa uma exibição de um filme em uma sala específica.
 * 
 * Uma sessão é caracterizada por:
 * - Filme que será exibido
 * - Sala onde ocorrerá (a sala define a capacidade)
 * - Horário da exibição
 * - Status (disponível, esgotada, cancelada)
 * - Mapa de assentos (derivado da capacidade da sala)
 * 
 * IMPORTANTE: A capacidade vem da Sala, não é um atributo da Sessão!
 */
public class Sessao implements Cloneable {
    private final SessaoId sessaoId;
    private FilmeId filmeId;
    private Date horario;
    private StatusSessao status;
    private Map<AssentoId, Boolean> mapaAssentosDisponiveis;
    private final SalaId salaId;

    public Sessao(SessaoId sessaoId, FilmeId filmeId, Date horario, StatusSessao status,
                  Map<AssentoId, Boolean> mapaAssentosDisponiveis, SalaId salaId) {
        // NOTA: sessaoId pode ser null durante a criação de uma nova sessão
        // O ID será gerado automaticamente pelo banco de dados (IDENTITY)
        // e preenchido após a persistência
        
        this.sessaoId = sessaoId;
        this.filmeId = exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        this.horario = horario;
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
        this.mapaAssentosDisponiveis = new HashMap<>(mapaAssentosDisponiveis != null ? mapaAssentosDisponiveis : new HashMap<>());
        this.salaId = exigirNaoNulo(salaId, "O id da sala não pode ser nulo");
    }

    public SessaoId getSessaoId() {
        return sessaoId;
    }

    public FilmeId getFilmeId() {
        return filmeId;
    }

    public Date getHorario() {
        return horario;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public Map<AssentoId, Boolean> getMapaAssentosDisponiveis() {
        return new HashMap<>(mapaAssentosDisponiveis);
    }

    public SalaId getSalaId() {
        return salaId;
    }
    
    /**
     * Retorna a quantidade de assentos disponíveis nesta sessão.
     * A capacidade total vem da Sala, mas para evitar quebrar o código existente,
     * retornamos o tamanho do mapa de assentos.
     */
    public int getCapacidade() {
        return mapaAssentosDisponiveis.size();
    }

    public boolean assentoDisponivel(AssentoId assentoId) {
        return mapaAssentosDisponiveis.getOrDefault(assentoId, false);
    }

    public void reservarAssento(AssentoId assentoId) {
        exigirEstado(assentoDisponivel(assentoId), "O assento não está disponível");
        mapaAssentosDisponiveis.put(assentoId, false);

        // Verifica se todos os assentos foram reservados
        if (mapaAssentosDisponiveis.values().stream().noneMatch(disponivel -> disponivel)) {
            marcarComoEsgotada();
        }
    }

    public void liberarAssento(AssentoId assentoId) {
        exigirNaoNulo(assentoId, "O assento não pode ser nulo");
        // Libera o assento, marcando como disponível
        mapaAssentosDisponiveis.put(assentoId, true);

        // Se estava esgotada e agora tem assento disponível, volta para DISPONIVEL
        if (this.status == StatusSessao.ESGOTADA) {
            this.status = StatusSessao.DISPONIVEL;
        }
    }

    public void marcarComoEsgotada() {
        // Verifica se ainda há assentos disponíveis
        exigirEstado(mapaAssentosDisponiveis.values().stream().noneMatch(Boolean::booleanValue),
            "Não é possível marcar como esgotada enquanto houver assentos disponíveis");
        this.status = StatusSessao.ESGOTADA;
    }

    @Override
    public Sessao clone() {
        try {
            Sessao cloned = (Sessao) super.clone();
            cloned.mapaAssentosDisponiveis = new HashMap<>(this.mapaAssentosDisponiveis);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
