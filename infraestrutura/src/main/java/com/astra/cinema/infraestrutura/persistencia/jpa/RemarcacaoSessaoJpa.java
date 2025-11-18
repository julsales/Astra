package com.astra.cinema.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidade JPA para RemarcacaoSessao.
 * Tabela que registra o histórico de remarcações de sessões por funcionários.
 */
@Entity
@Table(name = "REMARCACAO_SESSAO")
public class RemarcacaoSessaoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ingresso_id", nullable = false)
    private Integer ingressoId;

    @Column(name = "sessao_original_id", nullable = false)
    private Integer sessaoOriginalId;

    @Column(name = "sessao_nova_id", nullable = false)
    private Integer sessaoNovaId;

    @Column(name = "assento_original_id")
    private String assentoOriginalId;

    @Column(name = "assento_novo_id")
    private String assentoNovoId;

    @Column(name = "funcionario_id", nullable = false)
    private Integer funcionarioId;

    @Column(name = "data_hora_remarcacao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraRemarcacao;

    @Column(name = "motivo_tecnico", nullable = false, length = 1000)
    private String motivoTecnico;

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIngressoId() {
        return ingressoId;
    }

    public void setIngressoId(Integer ingressoId) {
        this.ingressoId = ingressoId;
    }

    public Integer getSessaoOriginalId() {
        return sessaoOriginalId;
    }

    public void setSessaoOriginalId(Integer sessaoOriginalId) {
        this.sessaoOriginalId = sessaoOriginalId;
    }

    public Integer getSessaoNovaId() {
        return sessaoNovaId;
    }

    public void setSessaoNovaId(Integer sessaoNovaId) {
        this.sessaoNovaId = sessaoNovaId;
    }

    public String getAssentoOriginalId() {
        return assentoOriginalId;
    }

    public void setAssentoOriginalId(String assentoOriginalId) {
        this.assentoOriginalId = assentoOriginalId;
    }

    public String getAssentoNovoId() {
        return assentoNovoId;
    }

    public void setAssentoNovoId(String assentoNovoId) {
        this.assentoNovoId = assentoNovoId;
    }

    public Integer getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Integer funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public Date getDataHoraRemarcacao() {
        return dataHoraRemarcacao;
    }

    public void setDataHoraRemarcacao(Date dataHoraRemarcacao) {
        this.dataHoraRemarcacao = dataHoraRemarcacao;
    }

    public String getMotivoTecnico() {
        return motivoTecnico;
    }

    public void setMotivoTecnico(String motivoTecnico) {
        this.motivoTecnico = motivoTecnico;
    }
}
