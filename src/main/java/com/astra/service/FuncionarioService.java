package com.astra.service;

import com.astra.model.Funcionario;
import com.astra.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    public Optional<Funcionario> buscarPorMatricula(String matricula) {
        return funcionarioRepository.findByMatricula(matricula);
    }

    public List<Funcionario> buscarPorSetor(String setor) {
        return funcionarioRepository.findBySetor(setor);
    }

    public Funcionario salvar(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public void deletar(Long id) {
        funcionarioRepository.deleteById(id);
    }

    public boolean existeMatricula(String matricula) {
        return funcionarioRepository.existsByMatricula(matricula);
    }
}
