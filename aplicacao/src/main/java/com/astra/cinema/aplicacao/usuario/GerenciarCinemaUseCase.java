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
        validarPermissaoGerencial(funcionario, "executar esta operação");

        // Executa a operação protegida
        operacao.executar();
    }

    /**
     * Valida se um funcionário possui perfil de gerente.
     *
     * @param funcionario Funcionário autenticado
     * @param contexto Texto opcional para personalizar a mensagem
     */
    public void validarPermissaoGerencial(Funcionario funcionario, String contexto) {
        if (funcionario == null) {
            throw new SecurityException("Acesso negado: usuário não autenticado");
        }

        if (!possuiPermissaoGerencial(funcionario)) {
            String mensagemContexto = contexto != null && !contexto.isBlank()
                    ? contexto
                    : "executar esta operação";
            throw new SecurityException("Acesso negado: apenas gerentes podem " + mensagemContexto);
        }
    }

    /**
     * Sobrecarga simples para validar permissão sem contexto customizado.
     */
    public void validarPermissaoGerencial(Funcionario funcionario) {
        validarPermissaoGerencial(funcionario, null);
    }

    /**
     * Valida se um funcionário pode criar sessão
     * 
     * @param funcionario Funcionário
     * @throws SecurityException se não tiver permissão
     */
    public void validarPermissaoCriarSessao(Funcionario funcionario) {
        validarPermissaoGerencial(funcionario, "criar sessões");
    }

    /**
     * Valida se um funcionário pode remover filme
     * 
     * @param funcionario Funcionário
     * @throws SecurityException se não tiver permissão
     */
    public void validarPermissaoRemoverFilme(Funcionario funcionario) {
        validarPermissaoGerencial(funcionario, "remover filmes");
    }

    /**
     * Valida se um funcionário pode gerenciar produtos
     * 
     * @param funcionario Funcionário
     * @throws SecurityException se não tiver permissão
     */
    public void validarPermissaoGerenciarProdutos(Funcionario funcionario) {
        validarPermissaoGerencial(funcionario, "gerenciar produtos");
    }
}
