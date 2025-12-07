package com.astra.cinema.dominio.compra;

import com.astra.cinema.dominio.comum.*;
import java.util.List;

public interface CompraRepositorio {
    void salvar(Compra compra);
    Compra obterPorId(CompraId compraId);
    List<Compra> buscarPorCliente(ClienteId clienteId);
    List<Compra> listarTodas();
    Ingresso buscarIngressoPorQrCode(String qrCode);
    Ingresso buscarIngressoPorId(IngressoId ingressoId);
    Compra buscarCompraPorQrCode(String qrCode);
    void atualizarIngresso(Ingresso ingresso);
    List<Ingresso> buscarIngressosAtivos();
    List<Ingresso> buscarIngressosAtivosPorCliente(ClienteId clienteId);
    CompraId obterCompraIdPorIngresso(IngressoId ingressoId);
}
