package com.astra.pagamento.dominio;

import java.util.List;

public interface PagamentoRepositorio {
    Pagamento salvar(Pagamento pagamento);
    Pagamento obter(PagamentoId id);
    List<Pagamento> buscarPorStatus(StatusPagamento status);
}
