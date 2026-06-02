package com.contabilidad.controller;

import com.contabilidad.model.AsientoContable;
import com.contabilidad.model.LineaAsiento;
import com.contabilidad.service.AsientoContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asientos")
public class AsientoContableController {

    @Autowired
    private AsientoContableService asientoService;

    @PostMapping
    public ResponseEntity<?> crearAsiento(@RequestBody Map<String, Object> body) {
        try {
            String descripcion = (String) body.get("descripcion");
            String usuarioId = (String) body.getOrDefault("usuarioId", "sistema");
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse((String) body.get("fecha"));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lineasRaw = (List<Map<String, Object>>) body.get("lineas");

            List<AsientoContableService.LineaDTO> lineas = lineasRaw.stream().map(l -> new AsientoContableService.LineaDTO(
                    (String) l.get("cuentaId"),
                    ((Number) l.getOrDefault("debito", 0)).doubleValue(),
                    ((Number) l.getOrDefault("credito", 0)).doubleValue(),
                    (String) l.get("terceroId")
            )).toList();

            AsientoContable asiento = asientoService.crearAsiento(descripcion, fecha, lineas, usuarioId);
            return ResponseEntity.ok(asiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AsientoContable>> listar(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        try {
            if (desde != null && hasta != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return ResponseEntity.ok(asientoService.buscarPorRangoFechas(sdf.parse(desde), sdf.parse(hasta)));
            }
            return ResponseEntity.ok(asientoService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        return asientoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable String id,
                                    @RequestParam(defaultValue = "sistema") String usuarioId) {
        try {
            AsientoContable anulado = asientoService.anularAsiento(id, usuarioId);
            return ResponseEntity.ok(anulado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/movimientos/cuenta/{cuentaId}")
    public ResponseEntity<List<LineaAsiento>> movimientosPorCuenta(@PathVariable String cuentaId) {
        return ResponseEntity.ok(asientoService.listarMovimientosPorCuenta(cuentaId));
    }
}