package com.contabilidad.controller;

import com.contabilidad.model.Usuario;
import com.contabilidad.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, String> body) {
        try {
            Usuario usuario = usuarioService.crearUsuario(
                    body.get("nombre"), body.get("correo"), body.get("password"));
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        return usuarioService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, body.get("nombre"), body.get("correo"));
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable String id) {
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario desactivado correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/rol/{rolId}")
    public ResponseEntity<?> asignarRol(@PathVariable String id, @PathVariable String rolId) {
        try {
            Usuario actualizado = usuarioService.asignarRol(id, rolId);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}