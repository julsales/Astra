package com.astra.cinema.dominio.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.aplicacao.compra.*;
import java.util.List;

/**
 * Service de Compra - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class CompraService {
    private final IniciarCompraUseCase iniciarCompraUseCase;
    private final ConfirmarCompraUseCase confirmarCompraUseCase;
    private final CancelarCompraUseCase cancelarCompraUseCase;

    public CompraService(CompraRepositorio compraRepositorio, 
                        SessaoRepositorio sessaoRepositorio,
                        PagamentoRepositorio pagamentoRepositorio) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        
        // Inicializa os use cases
        this.iniciarCompraUseCase = new IniciarCompraUseCase(compraRepositorio, sessaoRepositorio);
        this.confirmarCompraUseCase = new ConfirmarCompraUseCase(compraRepositorio, pagamentoRepositorio);
        this.cancelarCompraUseCase = new CancelarCompraUseCase(compraRepositorio, pagamentoRepositorio);
    }

    public Compra iniciarCompra(ClienteId clienteId, List<Ingresso> ingressos) {
        return iniciarCompraUseCase.executar(clienteId, ingressos);
    }

    public void confirmarCompra(CompraId compraId, PagamentoId pagamentoId) {
        confirmarCompraUseCase.executar(compraId, pagamentoId);
    }

    public void cancelarCompra(CompraId compraId) {
        cancelarCompraUseCase.executar(compraId);
    }
}
