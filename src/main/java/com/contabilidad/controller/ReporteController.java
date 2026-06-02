package com.contabilidad.controller;

import com.contabilidad.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/balance-general")
    public ResponseEntity<Map<String, Object>> balanceGeneral() {
        return ResponseEntity.ok(reporteService.generarBalanceGeneral());
    }

    @GetMapping("/estado-resultados")
    public ResponseEntity<Map<String, Object>> estadoResultados() {
        return ResponseEntity.ok(reporteService.generarEstadoResultados());
    }

    @GetMapping("/libro-diario")
    public ResponseEntity<List<Map<String, Object>>> libroDiario() {
        return ResponseEntity.ok(reporteService.generarLibroDiario());
    }

    @GetMapping("/libro-mayor/{cuentaId}")
    public ResponseEntity<?> libroMayor(@PathVariable String cuentaId) {
        try {
            return ResponseEntity.ok(reporteService.generarLibroMayor(cuentaId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
