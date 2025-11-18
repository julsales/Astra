package com.astra.cinema.aplicacao.funcionario;

import com.astra.cinema.aplicacao.ingresso.ValidarIngressoUseCase;
import com.astra.cinema.dominio.comum.FuncionarioId;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.operacao.ValidacaoIngresso;
import com.astra.cinema.dominio.operacao.ValidacaoIngressoRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.Date;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.*;

/**
 * Caso de uso para validação de ingressos por funcionários.
 * Registra o histórico de validações para auditoria.
 */
public class ValidarIngressoFuncionarioUseCase {
    private final ValidarIngressoUseCase validarIngressoUseCase;
    private final ValidacaoIngressoRepositorio validacaoIngressoRepositorio;

    public ValidarIngressoFuncionarioUseCase(
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            ValidacaoIngressoRepositorio validacaoIngressoRepositorio) {
        exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.validarIngressoUseCase = new ValidarIngressoUseCase(compraRepositorio, sessaoRepositorio);
        this.validacaoIngressoRepositorio = exigirNaoNulo(validacaoIngressoRepositorio,
            "O repositório de validações não pode ser nulo");
    }

    /**
     * Valida um ingresso e registra a operação no histórico.
     *
     * @param qrCode QR Code do ingresso
     * @param funcionarioId ID do funcionário que está validando
     * @return Resultado da validação
     */
    public ResultadoValidacaoFuncionario executar(String qrCode, FuncionarioId funcionarioId) {
        exigirTexto(qrCode, "O QR Code não pode ser nulo ou vazio");
        exigirNaoNulo(funcionarioId, "O ID do funcionário não pode ser nulo");

        // Executar validação padrão
        ValidarIngressoUseCase.ResultadoValidacao resultado = validarIngressoUseCase.executar(qrCode);

        // Registrar a validação no histórico
        if (resultado.getIngresso() != null) {
            ValidacaoIngresso validacao = new ValidacaoIngresso(
                null, // ID será gerado pelo banco
                resultado.getIngresso().getIngressoId(),
                funcionarioId,
                new Date(),
                resultado.isValido(),
                resultado.getMensagem()
            );
            validacaoIngressoRepositorio.salvar(validacao);
        }

        return new ResultadoValidacaoFuncionario(
            resultado.isValido(),
            resultado.getMensagem(),
            resultado.getIngresso(),
            resultado.getSessao()
        );
    }

    /**
     * Resultado da validação com informações completas.
     */
    public static class ResultadoValidacaoFuncionario {
        private final boolean valido;
        private final String mensagem;
        private final com.astra.cinema.dominio.compra.Ingresso ingresso;
        private final com.astra.cinema.dominio.sessao.Sessao sessao;

        public ResultadoValidacaoFuncionario(boolean valido, String mensagem,
                                            com.astra.cinema.dominio.compra.Ingresso ingresso,
                                            com.astra.cinema.dominio.sessao.Sessao sessao) {
            this.valido = valido;
            this.mensagem = mensagem;
            this.ingresso = ingresso;
            this.sessao = sessao;
        }

        public boolean isValido() {
            return valido;
        }

        public String getMensagem() {
            return mensagem;
        }

        public com.astra.cinema.dominio.compra.Ingresso getIngresso() {
            return ingresso;
        }

        public com.astra.cinema.dominio.sessao.Sessao getSessao() {
            return sessao;
        }
    }
}
