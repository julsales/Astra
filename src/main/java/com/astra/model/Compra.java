package com.astra.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<Ingresso> ingressos = new ArrayList<>();

    @OneToOne(mappedBy = "compra", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCompra status = StatusCompra.PENDENTE;

    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @PrePersist
    protected void onCreate() {
        dataCompra = LocalDateTime.now();
    }

    // Construtores
    public Compra() {
    }

    public Compra(Cliente cliente, Sessao sessao, BigDecimal valorTotal) {
        this.cliente = cliente;
        this.sessao = sessao;
        this.valorTotal = valorTotal;
    }

    // Métodos de negócio
    public void confirmar() {
        if (this.pagamento == null || this.pagamento.getStatus() != Pagamento.StatusPagamento.SUCESSO) {
            throw new IllegalStateException("Pagamento não autorizado");
        }
        this.status = StatusCompra.CONFIRMADA;
        this.ingressos.forEach(Ingresso::validar);
    }

    public void cancelar() {
        // Verifica se algum ingresso já foi utilizado
        boolean ingressoUtilizado = this.ingressos.stream()
                .anyMatch(i -> i.getStatus() == Ingresso.StatusIngresso.UTILIZADO);
        
        if (ingressoUtilizado) {
            throw new IllegalStateException("Ingresso já foi utilizado");
        }
        
        this.status = StatusCompra.CANCELADA;
        
        // Estorna pagamento
        if (this.pagamento != null) {
            this.pagamento.estornar();
        }
    }

    public void adicionarIngresso(Ingresso ingresso) {
        this.ingressos.add(ingresso);
        ingresso.setCompra(this);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public List<Ingresso> getIngressos() {
        return ingressos;
    }

    public void setIngressos(List<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        if (pagamento != null) {
            pagamento.setCompra(this);
        }
    }

    public StatusCompra getStatus() {
        return status;
    }

    public void setStatus(StatusCompra status) {
        this.status = status;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public enum StatusCompra {
        PENDENTE,
        CONFIRMADA,
        CANCELADA
    }
}
