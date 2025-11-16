package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.interface_adapters.service.RelatorioDashboardService;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.RelatorioDashboardDTO;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.RelatorioFiltro;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RelatorioDashboardService relatorioDashboardService;

    public RelatorioController(RelatorioDashboardService relatorioDashboardService) {
        this.relatorioDashboardService = relatorioDashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RelatorioDashboardDTO> dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer filmeId) {
        RelatorioFiltro filtro = new RelatorioFiltro(dataInicio, dataFim, filmeId);
        return ResponseEntity.ok(relatorioDashboardService.gerar(filtro));
    }

    @GetMapping(value = "/dashboard/exportacao", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer filmeId) {
        RelatorioFiltro filtro = new RelatorioFiltro(dataInicio, dataFim, filmeId);
        RelatorioDashboardDTO relatorio = relatorioDashboardService.gerar(filtro);
        byte[] pdf = relatorioDashboardService.gerarPdf(relatorio);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio-gerencial.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
