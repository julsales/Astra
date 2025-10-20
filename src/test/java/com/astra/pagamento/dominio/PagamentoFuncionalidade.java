package com.astra.pagamento.dominio;

import com.astra.infraestrutura.persistencia.memoria.MetodoPagamentoRepositorioMemoria;
import com.astra.infraestrutura.persistencia.memoria.PagamentoRepositorioMemoria;

public class PagamentoFuncionalidade {
    protected static PagamentoServico pagamentoServico;
    protected static PagamentoRepositorioMemoria pagamentoRepositorio;
    protected static MetodoPagamentoRepositorioMemoria metodoPagamentoRepositorio;

    static {
        pagamentoRepositorio = new PagamentoRepositorioMemoria();
        metodoPagamentoRepositorio = new MetodoPagamentoRepositorioMemoria();
        pagamentoServico = new PagamentoServico(pagamentoRepositorio, metodoPagamentoRepositorio);
    }
}
