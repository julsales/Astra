package com.astra.cinema.dominio.compra;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirColecaoNaoVazia;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.eventos.CompraConfirmadaEvento;
import com.astra.cinema.dominio.eventos.PublicadorEventos;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.dominio.pagamento.StatusPagamento;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import java.util.List;

/**
 * Service de Compra - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 * 
 * PADRÃO OBSERVER: Publica eventos quando compras são confirmadas
 */
public class CompraService {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;
    private final PublicadorEventos publicadorEventos;

    public CompraService(CompraRepositorio compraRepositorio, 
                        SessaoRepositorio sessaoRepositorio,
                        PagamentoRepositorio pagamentoRepositorio,
                        PublicadorEventos publicadorEventos) {
        // Inicializa os use cases
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
        this.pagamentoRepositorio = exigirNaoNulo(pagamentoRepositorio, "O repositório de pagamentos não pode ser nulo");
        this.publicadorEventos = exigirNaoNulo(publicadorEventos, "O publicador de eventos não pode ser nulo");
    }

    public Compra iniciarCompra(ClienteId clienteId, List<Ingresso> ingressos) {
        exigirNaoNulo(clienteId, "O id do cliente não pode ser nulo");
        var ingressosValidados = exigirColecaoNaoVazia(ingressos, "A compra deve ter ingressos");

        ingressosValidados.forEach(ingresso -> {
            var sessao = exigirNaoNulo(
                sessaoRepositorio.obterPorId(ingresso.getSessaoId()),
                "Sessão não encontrada"
            );
            exigirEstado(
                sessao.assentoDisponivel(ingresso.getAssentoId()),
                "O assento " + ingresso.getAssentoId() + " não está disponível"
            );
        });

        ingressosValidados.forEach(ingresso -> {
            var sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
            sessao.reservarAssento(ingresso.getAssentoId());
            sessaoRepositorio.salvar(sessao);
        });

        var identificador = Math.max(1, Math.abs(System.identityHashCode(ingressosValidados)));
        var compra = new Compra(new CompraId(identificador), clienteId, ingressosValidados, null, StatusCompra.PENDENTE);
        compraRepositorio.salvar(compra);
        return compra;
    }

    /**
     * Confirma uma compra pendente.
     * RN2: A compra só pode ser confirmada se o pagamento associado for autorizado (SUCESSO).
     *
     * @param compraId ID da compra a confirmar
     * @param pagamentoId ID do pagamento associado
     */
    public void confirmarCompra(CompraId compraId, PagamentoId pagamentoId) {
        exigirNaoNulo(compraId, "O id da compra não pode ser nulo");
        exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");

        var compra = exigirNaoNulo(compraRepositorio.obterPorId(compraId), "Compra não encontrada");
        var pagamento = exigirNaoNulo(pagamentoRepositorio.obterPorId(pagamentoId), "Pagamento não encontrado");

        compra.setPagamentoId(pagamentoId);
        // RN2: Passa o status do pagamento para validação no domínio
        compra.confirmar(pagamento.getStatus());
        compraRepositorio.salvar(compra);

        // PADRÃO OBSERVER: Publica evento para notificar observadores
        CompraConfirmadaEvento evento = new CompraConfirmadaEvento(
            compra.getCompraId(),
            compra.getClienteId(),
            compra.getIngressos().size()
        );
        publicadorEventos.publicar(evento);
    }

    public void cancelarCompra(CompraId compraId) {
        exigirNaoNulo(compraId, "O id da compra não pode ser nulo");
        var compra = exigirNaoNulo(compraRepositorio.obterPorId(compraId), "Compra não encontrada");

        compra.cancelar();

        var pagamentoId = compra.getPagamentoId();
        if (pagamentoId != null) {
            var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);
            if (pagamento != null && pagamento.getStatus() == StatusPagamento.PENDENTE) {
                pagamento.cancelar();
                pagamentoRepositorio.salvar(pagamento);
            }
        }

        compraRepositorio.salvar(compra);
    }
}
