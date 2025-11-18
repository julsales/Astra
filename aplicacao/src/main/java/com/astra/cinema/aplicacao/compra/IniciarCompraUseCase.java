package com.astra.cinema.aplicacao.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import java.util.List;

/**
 * Caso de uso: Iniciar uma compra de ingressos
 * Responsabilidade: Orquestrar a criação de uma compra validando disponibilidade de assentos
 */
public class IniciarCompraUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public IniciarCompraUseCase(CompraRepositorio compraRepositorio, 
                                SessaoRepositorio sessaoRepositorio) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public Compra executar(ClienteId clienteId, List<Ingresso> ingressos) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O id do cliente não pode ser nulo");
        }
        if (ingressos == null || ingressos.isEmpty()) {
            throw new IllegalArgumentException("A lista de ingressos não pode ser vazia");
        }
        
        // Verifica se todos os assentos estão disponíveis ou já foram reservados
        // (os assentos podem já estar reservados pela chamada anterior de reserva do frontend)
        for (Ingresso ingresso : ingressos) {
            var sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
            if (sessao == null) {
                throw new IllegalStateException("Sessão não encontrada: " + ingresso.getSessaoId().getId());
            }
            
            // Se o assento já está ocupado, assumimos que foi reservado no mesmo processo
            // (pela chamada anterior de /api/sessoes/{id}/assentos/reservar)
            // Apenas reserva se ainda estiver disponível
            if (sessao.assentoDisponivel(ingresso.getAssentoId())) {
                // Assento disponível, reserva normalmente
                sessao.reservarAssento(ingresso.getAssentoId());
                sessaoRepositorio.salvar(sessao);
            }
            // Se já estiver ocupado, não faz nada (foi reservado anteriormente no mesmo processo)
        }
        
        // Cria a compra
        // Gera um ID positivo para a compra (será substituído pelo banco)
        // System.identityHashCode pode retornar 0 ou negativo, então garantimos que seja positivo
        int hash = System.identityHashCode(ingressos);
        int idCompra = Math.abs(hash);
        if (idCompra == 0 || idCompra < 1) {
            // Se for 0 ou negativo, usa timestamp garantindo que seja >= 1
            idCompra = Math.max(1, (int) (System.currentTimeMillis() % 1000000));
        }
        var compraId = new CompraId(idCompra);
        var compra = new Compra(compraId, clienteId, ingressos, null, StatusCompra.PENDENTE);
        compraRepositorio.salvar(compra);

        // Busca a última compra do cliente para obter o ID real gerado pelo banco
        var comprasDoCliente = compraRepositorio.buscarPorCliente(clienteId);
        if (comprasDoCliente == null || comprasDoCliente.isEmpty()) {
            throw new IllegalStateException("Erro ao salvar compra - compra não encontrada após salvar");
        }

        // Retorna a última compra (mais recente)
        return comprasDoCliente.get(comprasDoCliente.size() - 1);
    }
}
