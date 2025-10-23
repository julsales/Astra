package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.interface_adapters.dto.SessaoDTO;
import com.astra.cinema.interface_adapters.mapper.SessaoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para operações de Sessão
 */
@RestController
@RequestMapping("/api/sessoes")
public class SessaoController {

    private final CriarSessaoUseCase criarSessaoUseCase;

    public SessaoController(CriarSessaoUseCase criarSessaoUseCase) {
        this.criarSessaoUseCase = criarSessaoUseCase;
    }

    @PostMapping
    public ResponseEntity<SessaoDTO> criarSessao(@RequestBody CriarSessaoRequest request) {
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        if (request.getAssentos() != null) {
            request.getAssentos().forEach((key, value) -> 
                assentos.put(new AssentoId(key), value)
            );
        }
        
        Sessao sessao = criarSessaoUseCase.executar(
            new FilmeId(request.getFilmeId()),
            request.getHorario(),
            assentos
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(SessaoMapper.toDTO(sessao));
    }

    // Classe interna para request
    public static class CriarSessaoRequest {
        private Integer filmeId;
        private Date horario;
        private Map<String, Boolean> assentos;

        public Integer getFilmeId() {
            return filmeId;
        }

        public void setFilmeId(Integer filmeId) {
            this.filmeId = filmeId;
        }

        public Date getHorario() {
            return horario;
        }

        public void setHorario(Date horario) {
            this.horario = horario;
        }

        public Map<String, Boolean> getAssentos() {
            return assentos;
        }

        public void setAssentos(Map<String, Boolean> assentos) {
            this.assentos = assentos;
        }
    }
}
