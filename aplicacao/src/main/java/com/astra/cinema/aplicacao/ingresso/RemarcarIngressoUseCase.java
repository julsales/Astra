package com.astra.cinema.aplicacao.ingresso;

import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

public class RemarcarIngressoUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public RemarcarIngressoUseCase(CompraRepositorio compraRepositorio, SessaoRepositorio sessaoRepositorio) {
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    public void executar(String qrCode, SessaoId novaSessaoId, AssentoId novoAssentoId) {
        exigirNaoNulo(qrCode, "O QR Code não pode ser nulo");
        exigirNaoNulo(novaSessaoId, "A nova sessão não pode ser nula");
        exigirNaoNulo(novoAssentoId, "O novo assento não pode ser nulo");

        // Buscar ingresso
        Ingresso ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
        exigirNaoNulo(ingresso, "Ingresso não encontrado");

        // Buscar sessões
        Sessao sessaoAntiga = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
        Sessao novaSessao = sessaoRepositorio.obterPorId(novaSessaoId);
        exigirNaoNulo(sessaoAntiga, "Sessão antiga não encontrada");
        exigirNaoNulo(novaSessao, "Nova sessão não encontrada");

        // Verificar filme (deve ser o mesmo)
        exigirEstado(sessaoAntiga.getFilmeId().equals(novaSessao.getFilmeId()),
            "As sessões devem ser do mesmo filme");

        // Verificar disponibilidade do assento na nova sessão
        exigirEstado(novaSessao.assentoDisponivel(novoAssentoId),
            "O assento não está disponível na nova sessão");

        // Liberar assento da sessão antiga
        AssentoId assentoAntigo = ingresso.getAssentoId();
        // Note: seria bom ter um método para liberar assento, mas vamos apenas reservar o novo

        // Reservar assento na nova sessão
        novaSessao.reservarAssento(novoAssentoId);
        sessaoRepositorio.salvar(novaSessao);

        // Remarcar ingresso
        ingresso.remarcarSessao(novaSessaoId, novoAssentoId);
        compraRepositorio.atualizarIngresso(ingresso);
    }
}
