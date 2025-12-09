package com.astra.cinema.apresentacao.dto.response;

public class OcupacaoSalaDTO {
    private String nome;
    private Integer capacidade;
    private Integer assentosOcupados;
    private Integer ocupacao; // Percentual
    private Integer sessoesHoje;

    public OcupacaoSalaDTO() {
    }

    public OcupacaoSalaDTO(String nome, Integer capacidade, Integer assentosOcupados, 
                          Integer ocupacao, Integer sessoesHoje) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.assentosOcupados = assentosOcupados;
        this.ocupacao = ocupacao;
        this.sessoesHoje = sessoesHoje;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Integer getAssentosOcupados() {
        return assentosOcupados;
    }

    public void setAssentosOcupados(Integer assentosOcupados) {
        this.assentosOcupados = assentosOcupados;
    }

    public Integer getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(Integer ocupacao) {
        this.ocupacao = ocupacao;
    }

    public Integer getSessoesHoje() {
        return sessoesHoje;
    }

    public void setSessoesHoje(Integer sessoesHoje) {
        this.sessoesHoje = sessoesHoje;
    }
}
