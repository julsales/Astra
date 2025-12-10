package com.astra.cinema.aplicacao.ingresso;

import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

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

        Ingresso ingresso = null;
        try {
            ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
        } catch (IllegalArgumentException e) {
            // Se houver erro na busca, retorna resultado inválido
            return new ResultadoValidacao(false, "Código inválido: " + e.getMessage(), null, null);
        }

        if (ingresso == null) {
            return new ResultadoValidacao(false, "Ingresso não encontrado. Verifique se o código está correto.", null, null);
        }

        Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
        exigirNaoNulo(sessao, "Sessão não encontrada");

        // Validações
        // Regra de negócio: Ingressos CANCELADOS NÃO podem ser validados
        if (ingresso.getStatus() == StatusIngresso.CANCELADO) {
            return new ResultadoValidacao(false, "Ingresso cancelado. Este ingresso não pode ser validado.", ingresso, sessao);
        }

        // Validação liberada para qualquer horário (modo cinema flexível)

        // Buscar a compra completa para validar TODOS os ingressos juntos
        com.astra.cinema.dominio.compra.Compra compra = compraRepositorio.buscarCompraPorQrCode(qrCode);

        // Ação: ATIVO -> VALIDADO para TODOS os ingressos da compra
        if (ingresso.getStatus() == StatusIngresso.ATIVO) {
            // Se a compra tem múltiplos ingressos, valida todos
            if (compra != null && compra.getIngressos() != null && compra.getIngressos().size() > 1) {
                for (Ingresso ing : compra.getIngressos()) {
                    if (ing.getStatus() == StatusIngresso.ATIVO) {
                        ing.setStatus(StatusIngresso.VALIDADO);
                        compraRepositorio.atualizarIngresso(ing);
                    }
                }
            } else {
                // Apenas um ingresso
                ingresso.setStatus(StatusIngresso.VALIDADO);
                compraRepositorio.atualizarIngresso(ingresso);
            }
            return new ResultadoValidacao(true, "Ingresso validado - Pronto para uso", ingresso, sessao);
        } else if (ingresso.getStatus() == StatusIngresso.VALIDADO) {
            // Já está VALIDADO
            return new ResultadoValidacao(true, "Ingresso já foi validado anteriormente", ingresso, sessao);
        } else {
            // Status desconhecido
            return new ResultadoValidacao(false, "Status do ingresso inválido: " + ingresso.getStatus(), ingresso, sessao);
        }
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
