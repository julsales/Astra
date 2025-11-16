package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.FuncionarioId;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import com.astra.cinema.dominio.usuario.FuncionarioRepositorio;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioRepositorioJpa implements FuncionarioRepositorio {

    private final FuncionarioJpaRepository repository;
    private final CinemaMapeador mapeador;

    public FuncionarioRepositorioJpa(FuncionarioJpaRepository repository, CinemaMapeador mapeador) {
        this.repository = repository;
        this.mapeador = mapeador;
    }

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        FuncionarioJpa entity = mapeador.mapearParaFuncionarioJpa(funcionario);
        FuncionarioJpa salvo = repository.save(entity);
        return mapeador.mapearParaFuncionario(salvo);
    }

    @Override
    public List<Funcionario> listarTodos() {
    return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream()
                .map(mapeador::mapearParaFuncionario)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Funcionario> buscarPorId(FuncionarioId funcionarioId) {
        if (funcionarioId == null) {
            return Optional.empty();
        }
        return repository.findById(funcionarioId.getValor())
                .map(mapeador::mapearParaFuncionario);
    }

    @Override
    public void remover(FuncionarioId funcionarioId) {
        if (funcionarioId == null) {
            return;
        }
        repository.deleteById(funcionarioId.getValor());
    }

    @Override
    public boolean existeComNomeECargo(String nome, Cargo cargo, FuncionarioId ignorarId) {
        if (ignorarId != null) {
            return repository.existsByNomeIgnoreCaseAndCargoAndIdNot(nome, cargo, ignorarId.getValor());
        }
        return repository.existsByNomeIgnoreCaseAndCargo(nome, cargo);
    }
}
