package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.operacao.RemarcacaoSessao;
import com.astra.cinema.dominio.operacao.RemarcacaoSessaoRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RemarcacaoSessaoRepositorioJpa implements RemarcacaoSessaoRepositorio {

    private final RemarcacaoSessaoJpaRepository jpaRepository;

    public RemarcacaoSessaoRepositorioJpa(RemarcacaoSessaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RemarcacaoSessao salvar(RemarcacaoSessao remarcacao) {
        RemarcacaoSessaoJpa jpa = paraJpa(remarcacao);
        RemarcacaoSessaoJpa salvo = jpaRepository.save(jpa);
        return paraDominio(salvo);
    }

    @Override
    public Optional<RemarcacaoSessao> buscarPorId(RemarcacaoId id) {
        return jpaRepository.findById(id.getValor())
                .map(this::paraDominio);
    }

    @Override
    public List<RemarcacaoSessao> listarPorFuncionario(FuncionarioId funcionarioId) {
        return jpaRepository.findByFuncionarioId(funcionarioId.getValor())
                .stream()
                .map(this::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<RemarcacaoSessao> listarPorIngresso(IngressoId ingressoId) {
        return jpaRepository.findByIngressoId(ingressoId.getId())
                .stream()
                .map(this::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<RemarcacaoSessao> listarTodas() {
        return jpaRepository.findAllByOrderByDataHoraRemarcacaoDesc()
                .stream()
                .map(this::paraDominio)
                .collect(Collectors.toList());
    }

    private RemarcacaoSessaoJpa paraJpa(RemarcacaoSessao remarcacao) {
        RemarcacaoSessaoJpa jpa = new RemarcacaoSessaoJpa();
        if (remarcacao.getRemarcacaoId() != null) {
            jpa.setId(remarcacao.getRemarcacaoId().getValor());
        }
        jpa.setIngressoId(remarcacao.getIngressoId().getId());
        jpa.setSessaoOriginalId(remarcacao.getSessaoOriginal().getId());
        jpa.setSessaoNovaId(remarcacao.getSessaoNova().getId());
        jpa.setAssentoOriginalId(remarcacao.getAssentoOriginal() != null ? remarcacao.getAssentoOriginal().getValor() : null);
        jpa.setAssentoNovoId(remarcacao.getAssentoNovo() != null ? remarcacao.getAssentoNovo().getValor() : null);
        jpa.setFuncionarioId(remarcacao.getFuncionarioId().getValor());
        jpa.setDataHoraRemarcacao(remarcacao.getDataHoraRemarcacao());
        jpa.setMotivoTecnico(remarcacao.getMotivoTecnico());
        return jpa;
    }

    private RemarcacaoSessao paraDominio(RemarcacaoSessaoJpa jpa) {
        return new RemarcacaoSessao(
                new RemarcacaoId(jpa.getId()),
                new IngressoId(jpa.getIngressoId()),
                new SessaoId(jpa.getSessaoOriginalId()),
                new SessaoId(jpa.getSessaoNovaId()),
                jpa.getAssentoOriginalId() != null ? new AssentoId(jpa.getAssentoOriginalId()) : null,
                jpa.getAssentoNovoId() != null ? new AssentoId(jpa.getAssentoNovoId()) : null,
                new FuncionarioId(jpa.getFuncionarioId()),
                jpa.getDataHoraRemarcacao(),
                jpa.getMotivoTecnico()
        );
    }
}
