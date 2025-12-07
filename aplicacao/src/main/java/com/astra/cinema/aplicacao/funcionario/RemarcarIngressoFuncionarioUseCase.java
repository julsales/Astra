package com.astra.cinema.aplicacao.funcionario;

import com.astra.cinema.aplicacao.ingresso.RemarcarIngressoUseCase;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.operacao.RemarcacaoSessao;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.Date;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.*;

/**
 * Caso de uso para remarcação de ingressos por funcionários.
 *
 * Regra de negócio:
 * - Só é permitido remarcar até 2h antes do início da sessão original
 * - Pode haver cobrança de taxa de remarcação se o cinema quiser
 * - Deve registrar o motivo técnico da remarcação
 * - Clientes com contas devem ser notificados automaticamente
 */
public class RemarcarIngressoFuncionarioUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio;
    private final RemarcarIngressoUseCase remarcarIngressoUseCase;

    public RemarcarIngressoFuncionarioUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            RemarcacaoSessaoRepositorio remarcacaoSessaoRepositorio) {
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
        this.remarcacaoSessaoRepositorio = exigirNaoNulo(remarcacaoSessaoRepositorio,
            "O repositório de remarcações não pode ser nulo");
        this.remarcarIngressoUseCase = new RemarcarIngressoUseCase(compraRepositorio, sessaoRepositorio);
    }

    /**
     * Remarca um ingresso para outra sessão (por ID do ingresso).
     *
     * @param ingressoId ID do ingresso a ser remarcado
     * @param novaSessaoId ID da nova sessão
     * @param novoAssentoId ID do novo assento
     * @param funcionarioId ID do funcionário que está fazendo a remarcação
     * @param motivoTecnico Motivo técnico da remarcação
     * @return Resultado da remarcação
     */
    public ResultadoRemarcacao executar(
            IngressoId ingressoId,
            SessaoId novaSessaoId,
            AssentoId novoAssentoId,
            FuncionarioId funcionarioId,
            String motivoTecnico) {

        exigirNaoNulo(ingressoId, "O ID do ingresso não pode ser nulo");

        // Buscar ingresso e obter QR Code
        Ingresso ingresso = compraRepositorio.buscarIngressoPorId(ingressoId);
        exigirNaoNulo(ingresso, "Ingresso não encontrado");

        // Delegar para o método que usa QR Code
        return executar(ingresso.getQrCode(), novaSessaoId, novoAssentoId, funcionarioId, motivoTecnico);
    }

    /**
     * Remarca um ingresso para outra sessão (por QR Code).
     *
     * @param qrCode QR Code do ingresso a ser remarcado
     * @param novaSessaoId ID da nova sessão
     * @param novoAssentoId ID do novo assento
     * @param funcionarioId ID do funcionário que está fazendo a remarcação
     * @param motivoTecnico Motivo técnico da remarcação
     * @return Resultado da remarcação
     */
    public ResultadoRemarcacao executar(
            String qrCode,
            SessaoId novaSessaoId,
            AssentoId novoAssentoId,
            FuncionarioId funcionarioId,
            String motivoTecnico) {

        exigirTexto(qrCode, "O QR Code não pode ser nulo ou vazio");
        exigirNaoNulo(novaSessaoId, "O ID da nova sessão não pode ser nulo");
        exigirNaoNulo(novoAssentoId, "O ID do novo assento não pode ser nulo");
        exigirNaoNulo(funcionarioId, "O ID do funcionário não pode ser nulo");
        exigirTexto(motivoTecnico, "O motivo técnico da remarcação é obrigatório");

        // Buscar ingresso para validações específicas
        Ingresso ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
        exigirNaoNulo(ingresso, "Ingresso não encontrado");

        // Buscar sessão original para validação de tempo
        Sessao sessaoOriginal = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
        exigirNaoNulo(sessaoOriginal, "Sessão original não encontrada");

        // Validar prazo de 2h antes da sessão
        Date agora = new Date();
        long duasHorasEmMs = 2 * 60 * 60 * 1000;
        Date limiteRemarcacao = new Date(sessaoOriginal.getHorario().getTime() - duasHorasEmMs);

        if (agora.after(limiteRemarcacao)) {
            throw new IllegalArgumentException(
                "Não é possível remarcar com menos de 2 horas antes do início da sessão");
        }

        // Guardar dados originais para histórico
        SessaoId sessaoOriginalId = ingresso.getSessaoId();
        AssentoId assentoOriginal = ingresso.getAssentoId();
        IngressoId ingressoId = ingresso.getIngressoId();

        // Executar remarcação base (validações + lógica de assentos)
        remarcarIngressoUseCase.executar(qrCode, novaSessaoId, novoAssentoId);

        // Buscar nova sessão para retorno
        Sessao novaSessao = sessaoRepositorio.obterPorId(novaSessaoId);

        // Registrar a remarcação no histórico
        RemarcacaoSessao remarcacao = new RemarcacaoSessao(
            null, // ID será gerado pelo banco
            ingressoId,
            sessaoOriginalId,
            novaSessaoId,
            assentoOriginal,
            novoAssentoId,
            funcionarioId,
            agora,
            motivoTecnico
        );
        remarcacaoSessaoRepositorio.salvar(remarcacao);

        // Buscar ingresso atualizado
        Ingresso ingressoAtualizado = compraRepositorio.buscarIngressoPorQrCode(qrCode);

        return new ResultadoRemarcacao(
            true,
            "Ingresso remarcado com sucesso",
            ingressoAtualizado,
            sessaoOriginal,
            novaSessao
        );
    }

    /**
     * Resultado da remarcação.
     */
    public static class ResultadoRemarcacao {
        private final boolean sucesso;
        private final String mensagem;
        private final Ingresso ingresso;
        private final Sessao sessaoOriginal;
        private final Sessao novaSessao;

        public ResultadoRemarcacao(boolean sucesso, String mensagem,
                                  Ingresso ingresso, Sessao sessaoOriginal, Sessao novaSessao) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.ingresso = ingresso;
            this.sessaoOriginal = sessaoOriginal;
            this.novaSessao = novaSessao;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public Ingresso getIngresso() {
            return ingresso;
        }

        public Sessao getSessaoOriginal() {
            return sessaoOriginal;
        }

        public Sessao getNovaSessao() {
            return novaSessao;
        }
    }
}
