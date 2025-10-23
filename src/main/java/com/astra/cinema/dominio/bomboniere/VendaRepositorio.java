package com.astra.cinema.dominio.bomboniere;

import com.astra.cinema.dominio.comum.*;
import java.util.List;

public interface VendaRepositorio {
    void salvar(Venda venda);
    Venda obterPorId(VendaId vendaId);
    List<Venda> listarVendasPorStatus(StatusVenda status);
}
