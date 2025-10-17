package com.astra.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingressos")
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "assento_id", nullable = false)
    private Assento assento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIngresso tipo = TipoIngresso.INTEIRA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIngresso status = StatusIngresso.PENDENTE;

    @Column(name = "codigo_validacao", unique = true)
    private String codigoValidacao;

    @Column(name = "data_utilizacao")
    private LocalDateTime dataUtilizacao;

    // Construtores
    public Ingresso() {
    }

    public Ingresso(Compra compra, Assento assento, TipoIngresso tipo) {
        this.compra = compra;
        this.assento = assento;
        this.tipo = tipo;
    }

    // Métodos de negócio
    public void validar() {
        this.status = StatusIngresso.VALIDO;
        this.codigoValidacao = gerarCodigoValidacao();
    }

    public void utilizar() {
        if (this.status != StatusIngresso.VALIDO) {
            throw new IllegalStateException("Ingresso não está válido");
        }
        this.status = StatusIngresso.UTILIZADO;
        this.dataUtilizacao = LocalDateTime.now();
    }

    private String gerarCodigoValidacao() {
        // Usar timestamp e hashCode do objeto para garantir unicidade
        return "ING" + System.currentTimeMillis() + Math.abs(System.identityHashCode(this));
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Assento getAssento() {
        return assento;
    }

    public void setAssento(Assento assento) {
        this.assento = assento;
    }

    public TipoIngresso getTipo() {
        return tipo;
    }

    public void setTipo(TipoIngresso tipo) {
        this.tipo = tipo;
    }

    public StatusIngresso getStatus() {
        return status;
    }

    public void setStatus(StatusIngresso status) {
        this.status = status;
    }

    public String getCodigoValidacao() {
        return codigoValidacao;
    }

    public void setCodigoValidacao(String codigoValidacao) {
        this.codigoValidacao = codigoValidacao;
    }

    public LocalDateTime getDataUtilizacao() {
        return dataUtilizacao;
    }

    public void setDataUtilizacao(LocalDateTime dataUtilizacao) {
        this.dataUtilizacao = dataUtilizacao;
    }

    public enum TipoIngresso {
        INTEIRA,
        MEIA
    }

    public enum StatusIngresso {
        PENDENTE,
        VALIDO,
        UTILIZADO,
        CANCELADO
    }
}
