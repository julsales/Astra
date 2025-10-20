package com.astra.pagamento.dominio;

import io.cucumber.java.Before;

public class StepsCompartilhados extends PagamentoFuncionalidade {
    protected static PagamentoId pagamentoId;
    protected static MetodoPagamentoId metodoPagamentoId;
    
    @Before
    public void limparRepositorios() {
        pagamentoRepositorio.limpar();
        metodoPagamentoRepositorio.limpar();
    }
    
    public static void setPagamentoId(PagamentoId id) {
        pagamentoId = id;
    }
    
    public static PagamentoId getPagamentoId() {
        return pagamentoId;
    }
    
    public static void setMetodoPagamentoId(MetodoPagamentoId id) {
        metodoPagamentoId = id;
    }
    
    public static MetodoPagamentoId getMetodoPagamentoId() {
        return metodoPagamentoId;
    }
}
