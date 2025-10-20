package com.astra.pagamento.dominio;

import java.util.List;

public interface MetodoPagamentoRepositorio {
    MetodoPagamento salvar(MetodoPagamento metodo);
    MetodoPagamento obter(MetodoPagamentoId id);
    List<MetodoPagamento> listarTodos();
    List<MetodoPagamento> listarAtivos();
}
