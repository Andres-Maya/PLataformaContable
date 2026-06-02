package com.contabilidad.service;

import com.contabilidad.model.*;
import com.contabilidad.model.*;
import com.contabilidad.repository.AsientoContableRepository;
import com.contabilidad.repository.CuentaContableRepository;
import com.contabilidad.repository.LineaAsientoRepository;
import com.contabilidad.repository.TerceroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AsientoContableService {

    @Autowired
    private AsientoContableRepository asientoRepository;

    @Autowired
    private LineaAsientoRepository lineaAsientoRepository;

    @Autowired
    private CuentaContableRepository cuentaContableRepository;

    @Autowired
    private TerceroRepository terceroRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public record LineaDTO(String cuentaId, double debito, double credito, String terceroId) {}

    public AsientoContable crearAsiento(String descripcion, Date fecha,
                                        List<LineaDTO> lineasDTO, String usuarioId) {
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción del asiento es obligatoria.");
        if (fecha == null)
            throw new IllegalArgumentException("La fecha del asiento es obligatoria.");
        if (lineasDTO == null || lineasDTO.isEmpty())
            throw new IllegalArgumentException("El asiento debe tener al menos una línea.");

        List<LineaAsiento> lineas = new ArrayList<>();
        for (LineaDTO dto : lineasDTO) {
            CuentaContable cuenta = cuentaContableRepository.findById(dto.cuentaId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + dto.cuentaId()));

            if (!cuenta.isActivo())
                throw new IllegalStateException("La cuenta '" + cuenta.getCodigo() + "' está inactiva y no puede usarse.");

            LineaAsiento linea = new LineaAsiento();
            linea.setId(UUID.randomUUID().toString());
            linea.setDebito(dto.debito());
            linea.setCredito(dto.credito());
            linea.setCuentaContable(cuenta);
            if (dto.terceroId() != null && !dto.terceroId().isBlank()) {
                Tercero tercero = terceroRepository.findById(dto.terceroId())
                        .orElseThrow(() -> new IllegalArgumentException("Tercero no encontrado: " + dto.terceroId()));
                linea.setTercero(tercero);
            }

            lineas.add(lineaAsientoRepository.save(linea));
        }

        double totalDebitos = lineas.stream().mapToDouble(LineaAsiento::getDebito).sum();
        double totalCreditos = lineas.stream().mapToDouble(LineaAsiento::getCredito).sum();
        if (Math.abs(totalDebitos - totalCreditos) > 0.001) {
            lineas.forEach(l -> lineaAsientoRepository.deleteById(l.getId()));
            throw new IllegalStateException(String.format(
                    "El asiento no está balanceado. Débitos: %.2f | Créditos: %.2f", totalDebitos, totalCreditos));
        }

        AsientoContable asiento = new AsientoContable();
        asiento.setId(UUID.randomUUID().toString());
        asiento.setDescripcion(descripcion);
        asiento.setFecha(fecha);
        asiento.setEstado(EstadoAsiento.ACTIVO);
        asiento.setLineas(lineas);
        asiento.setUsuarioCreadorId(usuarioId);
        asiento.registrarCambio("Asiento creado", usuarioId);

        AsientoContable guardado = asientoRepository.save(asiento);
        auditoriaService.registrarAuditoria("Creación de asiento contable: " + descripcion, usuarioId);
        return guardado;
    }

    public List<AsientoContable> listarTodos() {
        return asientoRepository.findAll();
    }

    public List<AsientoContable> listarActivos() {
        return asientoRepository.findByEstado(EstadoAsiento.ACTIVO);
    }

    public Optional<AsientoContable> buscarPorId(String id) {
        return asientoRepository.findById(id);
    }

    public List<AsientoContable> buscarPorRangoFechas(Date desde, Date hasta) {
        return asientoRepository.findByFechaBetween(desde, hasta);
    }

    public AsientoContable anularAsiento(String id, String usuarioId) {
        AsientoContable asiento = asientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado: " + id));

        if (asiento.getEstado() == EstadoAsiento.ANULADO)
            throw new IllegalStateException("El asiento ya se encuentra anulado.");

        asiento.setEstado(EstadoAsiento.ANULADO);
        asiento.registrarCambio("Asiento anulado por usuario: " + usuarioId, usuarioId);

        AsientoContable anulado = asientoRepository.save(asiento);
        auditoriaService.registrarAuditoria("Anulación de asiento: " + id, usuarioId);
        return anulado;
    }

    public List<LineaAsiento> listarMovimientosPorCuenta(String cuentaId) {
        CuentaContable cuenta = cuentaContableRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + cuentaId));
        return lineaAsientoRepository.findByCuentaContable(cuenta);
    }
}