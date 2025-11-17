package com.astra.cinema.dominio.compra;

import com.astra.cinema.dominio.comum.*;
import java.util.List;

public interface CompraRepositorio {
    void salvar(Compra compra);
    Compra obterPorId(CompraId compraId);
    List<Compra> buscarPorCliente(ClienteId clienteId);
    Ingresso buscarIngressoPorQrCode(String qrCode);
    void atualizarIngresso(Ingresso ingresso);
    List<Ingresso> buscarIngressosAtivos();
}
