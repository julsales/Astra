package com.astra.cinema.dominio.usuario;

import com.astra.cinema.dominio.comum.FuncionarioId;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de funcionários.
 */
public interface FuncionarioRepositorio {

    /**
     * Persiste ou atualiza um funcionário e devolve a representação salva.
     */
    Funcionario salvar(Funcionario funcionario);

    /**
     * Lista todos os funcionários conhecidos.
     */
    List<Funcionario> listarTodos();

    /**
     * Busca por identificador único.
     */
    Optional<Funcionario> buscarPorId(FuncionarioId funcionarioId);

    /**
     * Remove um funcionário existente.
     */
    void remover(FuncionarioId funcionarioId);

    /**
     * Verifica se já existe outro funcionário com o mesmo nome e cargo.
     *
     * @param nome Nome que será validado
     * @param cargo Cargo alvo
     * @param ignorarId Identificador que deve ser ignorado na verificação (para atualizações)
     */
    boolean existeComNomeECargo(String nome, Cargo cargo, FuncionarioId ignorarId);
}
