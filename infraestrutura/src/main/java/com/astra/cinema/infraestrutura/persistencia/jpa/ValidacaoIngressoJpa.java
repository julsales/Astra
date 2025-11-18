package com.astra.cinema.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidade JPA para ValidacaoIngresso.
 * Tabela que registra o histórico de validações de ingressos por funcionários.
 */
@Entity
@Table(name = "VALIDACAO_INGRESSO")
public class ValidacaoIngressoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ingresso_id", nullable = false)
    private Integer ingressoId;

    @Column(name = "funcionario_id", nullable = false)
    private Integer funcionarioId;

    @Column(name = "data_hora_validacao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraValidacao;

    @Column(nullable = false)
    private Boolean sucesso;

    @Column(length = 500)
    private String mensagem;

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

    public Integer getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Integer funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public Date getDataHoraValidacao() {
        return dataHoraValidacao;
    }

    public void setDataHoraValidacao(Date dataHoraValidacao) {
        this.dataHoraValidacao = dataHoraValidacao;
    }

    public Boolean getSucesso() {
        return sucesso;
    }

    public void setSucesso(Boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
