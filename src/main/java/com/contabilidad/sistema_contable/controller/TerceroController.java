package com.contabilidad.sistema_contable.controller;

import com.contabilidad.sistema_contable.model.Tercero;
import com.contabilidad.sistema_contable.service.TerceroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terceros")
public class TerceroController {

    @Autowired
    private TerceroService terceroService;

    @PostMapping
    public ResponseEntity<?> crearTercero(@RequestBody Map<String, String> body) {
        try {
            Tercero tercero = terceroService.crearTercero(
                    body.get("nombre"), body.get("identificacion"),
                    body.getOrDefault("usuarioId", "sistema"));
            return ResponseEntity.ok(tercero);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Tercero>> listar() {
        return ResponseEntity.ok(terceroService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        return terceroService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            Tercero actualizado = terceroService.actualizarTercero(
                    id, body.get("nombre"), body.get("identificacion"),
                    body.getOrDefault("usuarioId", "sistema"));
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

