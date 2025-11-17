package com.astra.cinema.aplicacao.usuario.funcionario;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.FuncionarioId;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import com.astra.cinema.dominio.usuario.FuncionarioRepositorio;
import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import com.astra.cinema.dominio.usuario.TipoUsuario;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Caso de uso responsável pelo CRUD de funcionários.
 */
public class GerenciarFuncionariosUseCase {

    private final FuncionarioRepositorio funcionarioRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public GerenciarFuncionariosUseCase(FuncionarioRepositorio funcionarioRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.funcionarioRepositorio = exigirNaoNulo(funcionarioRepositorio,
                "O repositório de funcionários não pode ser nulo");
        this.usuarioRepositorio = exigirNaoNulo(usuarioRepositorio,
                "O repositório de usuários não pode ser nulo");
    }

    public List<Funcionario> listar(String termoBusca, Cargo cargo) {
        Locale locale = Locale.getDefault();
        return funcionarioRepositorio.listarTodos().stream()
                .filter(func -> cargo == null || func.getCargo() == cargo)
                .filter(func -> filtroTexto(func, termoBusca, locale))
                .sorted(Comparator.comparing(Funcionario::getNome))
                .collect(Collectors.toList());
    }

    public Funcionario criar(String nome, Cargo cargo, String email, String senha) {
        Cargo cargoValidado = exigirNaoNulo(cargo, "O cargo não pode ser nulo");
        String nomeValidado = exigirTexto(nome, "O nome do funcionário é obrigatório");
        String emailValidado = exigirTexto(email, "O email do funcionário é obrigatório");
        String senhaValidada = exigirTexto(senha, "A senha do funcionário é obrigatória");
        
        // Verificar se email já existe
        if (usuarioRepositorio.buscarPorEmail(emailValidado).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        
        validarDuplicidade(nomeValidado, cargoValidado, null);
        
        // Criar funcionário
        Funcionario novoFuncionario = new Funcionario(nomeValidado, cargoValidado);
        Funcionario funcionarioSalvo = funcionarioRepositorio.salvar(novoFuncionario);
        
        // Criar usuário correspondente
        Usuario novoUsuario = new Usuario(
            null,
            emailValidado,
            senhaValidada,
            nomeValidado,
            TipoUsuario.FUNCIONARIO
        );
        usuarioRepositorio.salvar(novoUsuario);
        
        return funcionarioSalvo;
    }

    public Funcionario atualizar(FuncionarioId funcionarioId, String nome, Cargo cargo) {
        FuncionarioId id = exigirNaoNulo(funcionarioId, "O identificador do funcionário é obrigatório");
        funcionarioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        Cargo cargoValidado = exigirNaoNulo(cargo, "O cargo não pode ser nulo");
        String nomeValidado = exigirTexto(nome, "O nome do funcionário é obrigatório");
        validarDuplicidade(nomeValidado, cargoValidado, id);

        Funcionario atualizado = new Funcionario(id, nomeValidado, cargoValidado);
        return funcionarioRepositorio.salvar(atualizado);
    }

    public void remover(FuncionarioId funcionarioId) {
        FuncionarioId id = exigirNaoNulo(funcionarioId, "O identificador do funcionário é obrigatório");
        funcionarioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
        funcionarioRepositorio.remover(id);
    }

    public Funcionario detalhar(FuncionarioId funcionarioId) {
        FuncionarioId id = exigirNaoNulo(funcionarioId, "O identificador do funcionário é obrigatório");
        return funcionarioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
    }

    private boolean filtroTexto(Funcionario funcionario, String termoBusca, Locale locale) {
        if (termoBusca == null || termoBusca.isBlank()) {
            return true;
        }
        String normalizado = termoBusca.toLowerCase(locale);
        return Optional.ofNullable(funcionario.getNome())
                .map(nome -> nome.toLowerCase(locale).contains(normalizado))
                .orElse(false);
    }

    private void validarDuplicidade(String nome, Cargo cargo, FuncionarioId ignorarId) {
        if (funcionarioRepositorio.existeComNomeECargo(nome, cargo, ignorarId)) {
            throw new IllegalArgumentException("Já existe um funcionário com o mesmo nome e cargo");
        }
    }
}
