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
        System.out.println("Iniciando remarcação. QRCode: " + qrCode + ", Nova Sessão: " + novaSessaoId + ", Novo Assento: " + novoAssentoId);

        exigirNaoNulo(qrCode, "O QR Code não pode ser nulo");
        exigirNaoNulo(novaSessaoId, "A nova sessão não pode ser nula");
        exigirNaoNulo(novoAssentoId, "O novo assento não pode ser nulo");

        // Buscar ingresso
        Ingresso ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
        exigirNaoNulo(ingresso, "Ingresso não encontrado");

        // Validar status do ingresso
        exigirEstado(ingresso.getStatus() != com.astra.cinema.dominio.compra.StatusIngresso.EXPIRADO,
            "Não é possível remarcar um ingresso expirado. A sessão passou sem validação.");
        exigirEstado(ingresso.getStatus() != com.astra.cinema.dominio.compra.StatusIngresso.CANCELADO,
            "Não é possível remarcar um ingresso cancelado.");

        System.out.println("Ingresso encontrado: ID=" + ingresso.getIngressoId().getId() + 
            ", Sessão Atual=" + ingresso.getSessaoId().getId() + 
            ", Assento Atual=" + ingresso.getAssentoId().getValor());

        // Buscar sessões
        Sessao sessaoAntiga = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
        Sessao novaSessao = sessaoRepositorio.obterPorId(novaSessaoId);
        exigirNaoNulo(sessaoAntiga, "Sessão antiga não encontrada");
        exigirNaoNulo(novaSessao, "Nova sessão não encontrada");

        // Permitir remarcação para qualquer filme (regra de negócio flexibilizada)
        // O funcionário pode remarcar para qualquer sessão disponível
        System.out.println("Remarcando de sessão " + sessaoAntiga.getSessaoId() + 
            " (filme: " + sessaoAntiga.getFilmeId() + ") para sessão " + novaSessao.getSessaoId() +
            " (filme: " + novaSessao.getFilmeId() + ")");

        // Liberar assento da sessão antiga PRIMEIRO
        AssentoId assentoAntigo = ingresso.getAssentoId();
        sessaoAntiga.liberarAssento(assentoAntigo);
        sessaoRepositorio.salvar(sessaoAntiga);
        System.out.println("Assento antigo " + assentoAntigo + " liberado na sessão " + sessaoAntiga.getSessaoId());

        // Verificar disponibilidade do assento na nova sessão
        exigirEstado(novaSessao.assentoDisponivel(novoAssentoId),
            "O assento não está disponível na nova sessão");

        // Reservar assento na nova sessão
        novaSessao.reservarAssento(novoAssentoId);
        sessaoRepositorio.salvar(novaSessao);
        System.out.println("Novo assento " + novoAssentoId + " reservado na sessão " + novaSessao.getSessaoId());

        // Remarcar ingresso
        try {
            System.out.println("Chamando ingresso.remarcarSessao()...");
            ingresso.remarcarSessao(novaSessaoId, novoAssentoId);
            System.out.println("remarcarSessao() concluído. Chamando atualizarIngresso()...");
            compraRepositorio.atualizarIngresso(ingresso);
            System.out.println("✅ Ingresso atualizado no repositório com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ ERRO ao atualizar ingresso: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
