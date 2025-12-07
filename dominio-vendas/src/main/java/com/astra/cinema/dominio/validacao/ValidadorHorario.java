package com.astra.cinema.dominio.validacao;

import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.Calendar;
import java.util.Date;

/**
 * PADRÃO DECORATOR - Decorator Concreto
 * Adiciona validação de horário da sessão (não pode validar ingresso de sessão futura/passada).
 */
public class ValidadorHorario extends ValidadorIngressoDecorator {

    private final SessaoRepositorio sessaoRepositorio;
    private final int minutosAntes;
    private final int minutosDepois;

    /**
     * @param validadorBase Validador base
     * @param sessaoRepositorio Repositório de sessões
     * @param minutosAntes Minutos antes do início da sessão que pode validar
     * @param minutosDepois Minutos depois do início que ainda pode validar
     */
    public ValidadorHorario(ValidadorIngresso validadorBase,
                           SessaoRepositorio sessaoRepositorio,
                           int minutosAntes,
                           int minutosDepois) {
        super(validadorBase);
        this.sessaoRepositorio = sessaoRepositorio;
        this.minutosAntes = minutosAntes;
        this.minutosDepois = minutosDepois;
    }

    @Override
    protected ResultadoValidacao validarAdicional(Ingresso ingresso) {
        Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());

        if (sessao == null) {
            return new ResultadoValidacao(false, "Sessão não encontrada");
        }

        Date horarioSessao = sessao.getHorario();
        Date agora = new Date();

        // Calcula janela de validação
        Calendar cal = Calendar.getInstance();
        cal.setTime(horarioSessao);
        cal.add(Calendar.MINUTE, -minutosAntes);
        Date inicioJanela = cal.getTime();

        cal.setTime(horarioSessao);
        cal.add(Calendar.MINUTE, minutosDepois);
        Date fimJanela = cal.getTime();

        if (agora.before(inicioJanela)) {
            long minutosRestantes = (inicioJanela.getTime() - agora.getTime()) / (60 * 1000);
            return new ResultadoValidacao(false,
                "Sessão ainda não iniciou. Faltam " + minutosRestantes + " minutos");
        }

        if (agora.after(fimJanela)) {
            return new ResultadoValidacao(false,
                "Prazo de validação expirado. Sessão iniciou há mais de " + minutosDepois + " minutos");
        }

        return new ResultadoValidacao(true, "Horário dentro da janela de validação");
    }
}
