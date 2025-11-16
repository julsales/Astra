package com.astra.cinema.dominio.pagamento;

import com.astra.cinema.dominio.comum.PagamentoId;
import java.util.List;

public interface PagamentoRepositorio {
    void salvar(Pagamento pagamento);
    Pagamento obterPorId(PagamentoId pagamentoId);
    List<Pagamento> buscarPorStatus(StatusPagamento status);
}
