package com.contabilidad.sistema_contable.controller;

import com.contabilidad.sistema_contable.model.CuentaContable;
import com.contabilidad.sistema_contable.model.TipoCuenta;
import com.contabilidad.sistema_contable.service.CuentaContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaContableController {

    @Autowired
    private CuentaContableService cuentaService;

    @PostMapping
    public ResponseEntity<?> crearCuenta(@RequestBody Map<String, String> body) {
        try {
            TipoCuenta tipo = TipoCuenta.valueOf(body.get("tipo").toUpperCase());
            CuentaContable cuenta = cuentaService.crearCuenta(
                    body.get("codigo"), body.get("nombre"), tipo,
                    body.get("cuentaPadreId"), body.get("usuarioId"));
            return ResponseEntity.ok(cuenta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<CuentaContable>> listar(
            @RequestParam(required = false) String tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(cuentaService.listarPorTipo(TipoCuenta.valueOf(tipo.toUpperCase())));
        }
        return ResponseEntity.ok(cuentaService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        return cuentaService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        return cuentaService.buscarPorCodigo(codigo)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            TipoCuenta tipo = body.get("tipo") != null
                    ? TipoCuenta.valueOf(body.get("tipo").toUpperCase()) : null;
            CuentaContable actualizada = cuentaService.actualizarCuenta(
                    id, body.get("nombre"), tipo, body.getOrDefault("usuarioId", "sistema"));
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable String id,
                                        @RequestParam(defaultValue = "sistema") String usuarioId) {
        try {
            cuentaService.desactivarCuenta(id, usuarioId);
            return ResponseEntity.ok(Map.of("mensaje", "Cuenta desactivada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/subcuentas")
    public ResponseEntity<List<CuentaContable>> subcuentas(@PathVariable String id) {
        return ResponseEntity.ok(cuentaService.listarSubcuentas(id));
    }
}