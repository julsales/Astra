package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.filme.RemoverFilmeUseCase;
import com.astra.cinema.dominio.comum.FilmeId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de Filme
 */
@RestController
@RequestMapping("/api/filmes")
public class FilmeController {

    private final RemoverFilmeUseCase removerFilmeUseCase;

    public FilmeController(RemoverFilmeUseCase removerFilmeUseCase) {
        this.removerFilmeUseCase = removerFilmeUseCase;
    }

    @DeleteMapping("/{filmeId}")
    public ResponseEntity<Void> removerFilme(@PathVariable Integer filmeId) {
        removerFilmeUseCase.executar(new FilmeId(filmeId));
        return ResponseEntity.noContent().build();
    }
}
