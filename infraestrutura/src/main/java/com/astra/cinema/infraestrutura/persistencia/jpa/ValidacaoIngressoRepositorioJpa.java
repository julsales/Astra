package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.operacao.ValidacaoIngresso;
import com.astra.cinema.dominio.operacao.ValidacaoIngressoRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ValidacaoIngressoRepositorioJpa implements ValidacaoIngressoRepositorio {

    private final ValidacaoIngressoJpaRepository jpaRepository;

    public ValidacaoIngressoRepositorioJpa(ValidacaoIngressoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ValidacaoIngresso salvar(ValidacaoIngresso validacao) {
        ValidacaoIngressoJpa jpa = paraJpa(validacao);
        ValidacaoIngressoJpa salvo = jpaRepository.save(jpa);
        return paraDominio(salvo);
    }

    @Override
    public Optional<ValidacaoIngresso> buscarPorId(ValidacaoIngressoId id) {
        return jpaRepository.findById(id.getValor())
                .map(this::paraDominio);
    }

    @Override
    public List<ValidacaoIngresso> listarPorFuncionario(FuncionarioId funcionarioId) {
        return jpaRepository.findByFuncionarioId(funcionarioId.getValor())
                .stream()
                .map(this::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<ValidacaoIngresso> listarPorIngresso(IngressoId ingressoId) {
        return jpaRepository.findByIngressoId(ingressoId.getId())
                .stream()
                .map(this::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<ValidacaoIngresso> listarTodas() {
        return jpaRepository.findAllByOrderByDataHoraValidacaoDesc()
                .stream()
                .map(this::paraDominio)
                .collect(Collectors.toList());
    }

    private ValidacaoIngressoJpa paraJpa(ValidacaoIngresso validacao) {
        ValidacaoIngressoJpa jpa = new ValidacaoIngressoJpa();
        if (validacao.getValidacaoId() != null) {
            jpa.setId(validacao.getValidacaoId().getValor());
        }
        jpa.setIngressoId(validacao.getIngressoId().getId());
        jpa.setFuncionarioId(validacao.getFuncionarioId().getValor());
        jpa.setDataHoraValidacao(validacao.getDataHoraValidacao());
        jpa.setSucesso(validacao.isSucesso());
        jpa.setMensagem(validacao.getMensagem());
        return jpa;
    }

    private ValidacaoIngresso paraDominio(ValidacaoIngressoJpa jpa) {
        return new ValidacaoIngresso(
                new ValidacaoIngressoId(jpa.getId()),
                new IngressoId(jpa.getIngressoId()),
                new FuncionarioId(jpa.getFuncionarioId()),
                jpa.getDataHoraValidacao(),
                jpa.getSucesso(),
                jpa.getMensagem()
        );
    }
}
