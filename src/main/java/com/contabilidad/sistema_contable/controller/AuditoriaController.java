package com.contabilidad.sistema_contable.controller;

import com.contabilidad.sistema_contable.model.Auditoria;
import com.contabilidad.sistema_contable.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<Auditoria>> listar() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Auditoria>> buscar(@RequestParam String texto) {
        return ResponseEntity.ok(auditoriaService.buscarPorAccion(texto));
    }
}