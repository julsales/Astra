package com.astra.cinema.infraestrutura.persistencia.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;
import com.astra.cinema.dominio.compra.StatusIngresso;
import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.comum.CompraId;
import com.astra.cinema.dominio.comum.IngressoId;
import com.astra.cinema.infraestrutura.util.QrCodeGenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Implementação JPA do CompraRepositorio
 * Padrão: Adapter (adapta interface do domínio para JPA)
 */
@Component
public class CompraRepositorioJpa implements CompraRepositorio {

    @Autowired
    private CompraJpaRepository compraJpaRepository;

    @Autowired
    private IngressoJpaRepository ingressoJpaRepository;

    @Autowired
    private CinemaMapeador mapeador;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void salvar(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("A compra não pode ser nula");
        }

        CompraJpa compraSalva;
        
        // Verifica se a compra já existe no banco (UPDATE) ou é nova (INSERT)
        boolean compraExiste = compra.getCompraId() != null && 
                              compraJpaRepository.existsById(compra.getCompraId().getId());
        
        if (compraExiste) {
            // UPDATE: Atualiza compra existente
            compraSalva = compraJpaRepository.findById(compra.getCompraId().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada para atualizar: " + compra.getCompraId().getId()));
            
            compraSalva.setStatus(compra.getStatus().name());
            compraSalva.setPagamentoId(compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null);
            compraJpaRepository.save(compraSalva);
            
            // Atualiza os ingressos existentes
            List<IngressoJpa> ingressosExistentes = ingressoJpaRepository.findByCompraId(compraSalva.getId());
            List<Ingresso> ingressosDominio = compra.getIngressos();
            
            for (Ingresso ingresso : ingressosDominio) {
                // Procura o ingresso existente pela sessão e assento
                IngressoJpa ingressoJpa = ingressosExistentes.stream()
                    .filter(i -> i.getSessaoId().equals(ingresso.getSessaoId().getId()) &&
                                i.getAssento().equals(ingresso.getAssentoId().getValor()))
                    .findFirst()
                    .orElse(null);
                
                if (ingressoJpa != null) {
                    // Atualiza o status do ingresso existente
                    ingressoJpa.setStatus(ingresso.getStatus().name());
                    ingressoJpaRepository.save(ingressoJpa);
                }
            }
        } else {
            // INSERT: Cria nova compra
            CompraJpa compraJpa = new CompraJpa();
            compraJpa.setClienteId(compra.getClienteId().getId());
            compraJpa.setStatus(compra.getStatus().name());
            compraJpa.setPagamentoId(compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null);
            compraJpa.setCriadoEm(java.time.LocalDateTime.now());
            
            entityManager.persist(compraJpa);
            entityManager.flush();
            
            compraSalva = compraJpa;

            // Salva os novos ingressos
            List<Ingresso> ingressos = compra.getIngressos();
            for (Ingresso ingresso : ingressos) {
                // Gera um QR Code único para este ingresso
                String qrCode = qrCodeGenerator.gerarQrCode();

                // Garante que o QR Code seja único
                int tentativas = 0;
                while (ingressoJpaRepository.findByQrCode(qrCode).isPresent() && tentativas < 5) {
                    qrCode = qrCodeGenerator.gerarQrCode();
                    tentativas++;
                }

                IngressoJpa ingressoJpa = new IngressoJpa();
                ingressoJpa.setCompraId(compraSalva.getId());
                ingressoJpa.setSessaoId(ingresso.getSessaoId().getId());
                ingressoJpa.setAssento(ingresso.getAssentoId().getValor());
                ingressoJpa.setTipo(ingresso.getTipo().name());
                ingressoJpa.setStatus(ingresso.getStatus().name());
                ingressoJpa.setQrCode(qrCode);

                ingressoJpaRepository.save(ingressoJpa);
            }
        }
    }

    @Override
    public Compra obterPorId(CompraId compraId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O ID da compra não pode ser nulo");
        }

        CompraJpa compraJpa = compraJpaRepository.findById(compraId.getId()).orElse(null);
        if (compraJpa == null) {
            return null;
        }

        // Busca os ingressos associados
        List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByCompraId(compraId.getId());
        List<Ingresso> ingressos = ingressosJpa.stream()
                .map(mapeador::mapearParaIngresso)
                .collect(Collectors.toList());

        return mapeador.mapearParaCompra(compraJpa, ingressos);
    }

    @Override
    public List<Compra> buscarPorCliente(ClienteId clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O ID do cliente não pode ser nulo");
        }

        // Limpar cache do EntityManager para garantir dados frescos do banco
        entityManager.clear();

        List<CompraJpa> comprasJpa = compraJpaRepository.findByClienteId(clienteId.getId());
        List<Compra> compras = new ArrayList<>();

        for (CompraJpa compraJpa : comprasJpa) {
            List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByCompraId(compraJpa.getId());
            List<Ingresso> ingressos = ingressosJpa.stream()
                    .map(mapeador::mapearParaIngresso)
                    .collect(Collectors.toList());
            compras.add(mapeador.mapearParaCompra(compraJpa, ingressos));
        }

        return compras;
    }

    @Override
    public List<Compra> listarTodas() {
        List<CompraJpa> comprasJpa = compraJpaRepository.findAll();
        List<Compra> compras = new ArrayList<>();

        for (CompraJpa compraJpa : comprasJpa) {
            List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByCompraId(compraJpa.getId());
            List<Ingresso> ingressos = ingressosJpa.stream()
                    .map(mapeador::mapearParaIngresso)
                    .collect(Collectors.toList());
            compras.add(mapeador.mapearParaCompra(compraJpa, ingressos));
        }

        return compras;
    }

    @Override
    public Ingresso buscarIngressoPorQrCode(String qrCode) {
        if (qrCode == null || qrCode.isEmpty()) {
            throw new IllegalArgumentException("O QR Code não pode ser nulo ou vazio");
        }

        // Remove espaços e converte para maiúsculo
        String codigoLimpo = qrCode.trim().toUpperCase();

        // Busca direta por QR Code no banco (solução definitiva)
        return ingressoJpaRepository.findByQrCode(codigoLimpo)
                .map(mapeador::mapearParaIngresso)
                .orElse(null);
    }

    @Override
    public Ingresso buscarIngressoPorId(IngressoId ingressoId) {
        if (ingressoId == null) {
            throw new IllegalArgumentException("O ID do ingresso não pode ser nulo");
        }

        return ingressoJpaRepository.findById(ingressoId.getId())
                .map(mapeador::mapearParaIngresso)
                .orElse(null);
    }

    @Override
    public Compra buscarCompraPorQrCode(String qrCode) {
        if (qrCode == null || qrCode.isEmpty()) {
            throw new IllegalArgumentException("O QR Code não pode ser nulo ou vazio");
        }

        // Remove espaços e converte para maiúsculo
        String codigoLimpo = qrCode.trim().toUpperCase();

        // Busca o ingresso pelo QR Code
        IngressoJpa ingressoJpa = ingressoJpaRepository.findByQrCode(codigoLimpo).orElse(null);
        if (ingressoJpa == null) {
            return null;
        }

        // Usa o compraId do ingresso para buscar a compra completa
        return obterPorId(new CompraId(ingressoJpa.getCompraId()));
    }

    @Override
    @Transactional
    public void atualizarIngresso(Ingresso ingresso) {
        if (ingresso == null) {
            throw new IllegalArgumentException("O ingresso não pode ser nulo");
        }

        IngressoJpa ingressoJpa = ingressoJpaRepository.findById(ingresso.getIngressoId().getId())
                .orElse(null);

        if (ingressoJpa == null) {
            throw new IllegalArgumentException("Ingresso não encontrado");
        }

        // Atualiza os campos do ingresso
        ingressoJpa.setSessaoId(ingresso.getSessaoId().getId());
        ingressoJpa.setAssento(ingresso.getAssentoId().getValor());
        ingressoJpa.setTipo(ingresso.getTipo().name());
        ingressoJpa.setStatus(ingresso.getStatus().name());
        // Preserva o QR Code (não atualiza se já existir)
        if (ingresso.getQrCode() != null && !ingresso.getQrCode().isEmpty()) {
            ingressoJpa.setQrCode(ingresso.getQrCode());
        }

        ingressoJpaRepository.save(ingressoJpa);
        entityManager.flush(); // Força a gravação imediata no banco
        
        System.out.println("✅ Ingresso " + ingresso.getIngressoId().getId() + 
            " atualizado: sessão=" + ingresso.getSessaoId().getId() + 
            ", assento=" + ingresso.getAssentoId().getValor());
    }

    @Override
    public List<Ingresso> buscarIngressosAtivos() {
        // Busca ingressos com status ATIVO e VALIDADO (para exibir em "Meus Ingressos")
        java.util.List<String> statuses = java.util.Arrays.asList(
            StatusIngresso.ATIVO.name(),
            StatusIngresso.VALIDADO.name()
        );
        List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByStatusIn(statuses);
        return ingressosJpa.stream()
                .map(mapeador::mapearParaIngresso)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingresso> buscarIngressosAtivosPorCliente(ClienteId clienteId) {
        // Busca ingressos com status ATIVO e VALIDADO de um cliente específico
        java.util.List<String> statuses = java.util.Arrays.asList(
            StatusIngresso.ATIVO.name(),
            StatusIngresso.VALIDADO.name()
        );
        List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByClienteIdAndStatusIn(
            clienteId.getId(), 
            statuses
        );
        return ingressosJpa.stream()
                .map(mapeador::mapearParaIngresso)
                .collect(Collectors.toList());
    }

    @Override
    public CompraId obterCompraIdPorIngresso(IngressoId ingressoId) {
        if (ingressoId == null) {
            return null;
        }

        IngressoJpa ingressoJpa = ingressoJpaRepository.findById(ingressoId.getId()).orElse(null);
        if (ingressoJpa == null || ingressoJpa.getCompraId() == null) {
            return null;
        }

        return new CompraId(ingressoJpa.getCompraId());
    }
}

