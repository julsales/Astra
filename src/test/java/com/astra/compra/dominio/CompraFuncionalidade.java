package com.astra.compra.dominio;

import com.astra.compra.dominio.ingresso.CompraRepositorio;
import com.astra.compra.dominio.ingresso.CompraServico;
import com.astra.infraestrutura.persistencia.memoria.RepositorioMemoria;

public class CompraFuncionalidade {
    protected CompraRepositorio compraRepositorio;
    protected CompraServico compraServico;

    public CompraFuncionalidade() {
        var repositorio = new RepositorioMemoria();
        
        compraRepositorio = repositorio;
        compraServico = new CompraServico(repositorio);
    }
}
