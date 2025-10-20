package com.astra.compra.dominio.ingresso;

import com.astra.compra.dominio.cliente.ClienteId;
import io.cucumber.java.en.Given;

public class StepsCompartilhados {
    protected static ClienteId clienteId;
    protected static CompraId compraId;

    @Given("um cliente com ID {int}")
    public void um_cliente_com_id(Integer id) {
        clienteId = new ClienteId(id);
    }
    
    public static ClienteId getClienteId() {
        return clienteId;
    }
    
    public static void setCompraId(CompraId id) {
        compraId = id;
    }
    
    public static CompraId getCompraId() {
        return compraId;
    }
}
