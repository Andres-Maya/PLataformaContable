package com.contabilidad.sistema_contable.controller;

import com.contabilidad.sistema_contable.model.Rol;
import com.contabilidad.sistema_contable.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @PostMapping
    public ResponseEntity<?> crearRol(@RequestBody Map<String, String> body) {
        try {
            Rol rol = rolService.crearRol(body.get("nombre"));
            return ResponseEntity.ok(rol);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        return rolService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
