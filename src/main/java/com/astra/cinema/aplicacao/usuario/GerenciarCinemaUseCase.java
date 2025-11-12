package com.astra.cinema.aplicacao.usuario;

import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;

/**
 * Use Case: Gerenciar Cinema (Controle de Acesso)
 * 
 * Regra de Negócio: Apenas funcionários com cargo de GERENTE podem gerenciar dados de filmes, sessões e produtos
 * 
 * Padrão: Proxy (controla acesso às operações sensíveis)
 * Padrão: Strategy (através da interface funcional de operação)
 */
public class GerenciarCinemaUseCase {
    
    /**
     * Interface funcional para operações gerenciais
     */
    @FunctionalInterface
    public interface OperacaoGerencial {
        void executar();
    }

    /**
     * Verifica se o funcionário tem permissão de gerente
     * 
     * @param funcionario Funcionário a ser verificado
     * @return true se for gerente
     */
    public boolean possuiPermissaoGerencial(Funcionario funcionario) {
        if (funcionario == null) {
            return false;
        }
        return funcionario.getCargo() == Cargo.GERENTE;
    }

    /**
     * Executa uma operação gerencial com verificação de permissão
     * Padrão Proxy: controla o acesso à operação
     * 
     * @param funcionario Funcionário tentando executar a operação
     * @param operacao Operação a ser executada
     * @throws SecurityException se o funcionário não tiver permissão
     */
    public void executarOperacaoGerencial(Funcionario funcionario, OperacaoGerencial operacao) {
        if (funcionario == null) {
            throw new SecurityException("Acesso negado: usuário não autenticado");
        }

        if (!possuiPermissaoGerencial(funcionario)) {
            throw new SecurityException("Acesso negado: apenas gerentes podem executar esta operação");
        }

        // Executa a operação protegida
        operacao.executar();
    }

    /**
     * Valida se um funcionário pode criar sessão
     * 
     * @param funcionario Funcionário
     * @throws SecurityException se não tiver permissão
     */
    public void validarPermissaoCriarSessao(Funcionario funcionario) {
        if (funcionario == null) {
            throw new SecurityException("Acesso negado: usuário não autenticado");
        }

        if (!possuiPermissaoGerencial(funcionario)) {
            throw new SecurityException("Acesso negado: apenas gerentes podem criar sessões");
        }
    }

    /**
     * Valida se um funcionário pode remover filme
     * 
     * @param funcionario Funcionário
     * @throws SecurityException se não tiver permissão
     */
    public void validarPermissaoRemoverFilme(Funcionario funcionario) {
        if (funcionario == null) {
            throw new SecurityException("Acesso negado: usuário não autenticado");
        }

        if (!possuiPermissaoGerencial(funcionario)) {
            throw new SecurityException("Acesso negado: apenas gerentes podem remover filmes");
        }
    }

    /**
     * Valida se um funcionário pode gerenciar produtos
     * 
     * @param funcionario Funcionário
     * @throws SecurityException se não tiver permissão
     */
    public void validarPermissaoGerenciarProdutos(Funcionario funcionario) {
        if (funcionario == null) {
            throw new SecurityException("Acesso negado: usuário não autenticado");
        }

        if (!possuiPermissaoGerencial(funcionario)) {
            throw new SecurityException("Acesso negado: apenas gerentes podem gerenciar produtos");
        }
    }
}
