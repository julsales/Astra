package com.astra.cinema.aplicacao.ingresso;

import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

public class ValidarIngressoUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public ValidarIngressoUseCase(CompraRepositorio compraRepositorio, SessaoRepositorio sessaoRepositorio) {
        this.compraRepositorio = exigirNaoNulo(compraRepositorio, "O repositório de compras não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    public ResultadoValidacao executar(String qrCode) {
        exigirNaoNulo(qrCode, "O QR Code não pode ser nulo");

        Ingresso ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
        exigirNaoNulo(ingresso, "Ingresso não encontrado");

        Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
        exigirNaoNulo(sessao, "Sessão não encontrada");

        // Validações
        if (ingresso.getStatus() == StatusIngresso.CANCELADO) {
            return new ResultadoValidacao(false, "Ingresso cancelado", ingresso, sessao);
        }

        if (ingresso.getStatus() == StatusIngresso.UTILIZADO) {
            return new ResultadoValidacao(false, "Ingresso já foi utilizado", ingresso, sessao);
        }

        // Verificar horário da sessão (permitir entrada de 30 min antes até 30 min após início)
        Date horaInicio = sessao.getHorario();
        LocalDateTime horarioSessao = Instant.ofEpochMilli(horaInicio.getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        LocalDateTime agora = LocalDateTime.now();

        if (agora.isBefore(horarioSessao.minusMinutes(30))) {
            return new ResultadoValidacao(false, "Muito cedo para esta sessão", ingresso, sessao);
        }

        if (agora.isAfter(horarioSessao.plusHours(3))) { // Assume duração máxima de 3h
            return new ResultadoValidacao(false, "Sessão já encerrada", ingresso, sessao);
        }

        // Validar ingresso
        ingresso.utilizar();
        compraRepositorio.atualizarIngresso(ingresso);

        return new ResultadoValidacao(true, "Ingresso válido - Entrada autorizada", ingresso, sessao);
    }

    public static class ResultadoValidacao {
        private final boolean valido;
        private final String mensagem;
        private final Ingresso ingresso;
        private final Sessao sessao;

        public ResultadoValidacao(boolean valido, String mensagem, Ingresso ingresso, Sessao sessao) {
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

        public Ingresso getIngresso() {
            return ingresso;
        }

        public Sessao getSessao() {
            return sessao;
        }
    }
}
