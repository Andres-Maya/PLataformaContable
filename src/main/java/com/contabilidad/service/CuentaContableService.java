package com.contabilidad.service;

import com.contabilidad.model.CuentaContable;
import com.contabilidad.model.TipoCuenta;
import com.contabilidad.repository.CuentaContableRepository;
import com.contabilidad.repository.LineaAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CuentaContableService {

    @Autowired
    private CuentaContableRepository cuentaContableRepository;

    @Autowired
    private LineaAsientoRepository lineaAsientoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public CuentaContable crearCuenta(String codigo, String nombre, TipoCuenta tipo,
                                      String cuentaPadreId, String usuarioId) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código es obligatorio.");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (tipo == null) throw new IllegalArgumentException("El tipo de cuenta es obligatorio.");

        if (cuentaContableRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Ya existe una cuenta con el código: " + codigo);
        }

        if (cuentaPadreId != null && !cuentaPadreId.isBlank()) {
            cuentaContableRepository.findById(cuentaPadreId)
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta padre no encontrada: " + cuentaPadreId));
        }

        CuentaContable cuenta = new CuentaContable();
        cuenta.setId(UUID.randomUUID().toString());
        cuenta.setCodigo(codigo);
        cuenta.setNombre(nombre);
        cuenta.setTipo(tipo);
        cuenta.setCuentaPadreId(cuentaPadreId);

        CuentaContable guardada = cuentaContableRepository.save(cuenta);
        auditoriaService.registrarAuditoria("Creación de cuenta: " + codigo + " - " + nombre, usuarioId);
        return guardada;
    }

    public List<CuentaContable> listarActivas() {
        return cuentaContableRepository.findByActivoTrue();
    }

    public List<CuentaContable> listarTodas() {
        return cuentaContableRepository.findAll();
    }

    public Optional<CuentaContable> buscarPorId(String id) {
        return cuentaContableRepository.findById(id);
    }

    public Optional<CuentaContable> buscarPorCodigo(String codigo) {
        return cuentaContableRepository.findByCodigo(codigo);
    }

    public List<CuentaContable> listarPorTipo(TipoCuenta tipo) {
        return cuentaContableRepository.findByTipo(tipo);
    }

    public List<CuentaContable> listarSubcuentas(String cuentaPadreId) {
        return cuentaContableRepository.findByCuentaPadreId(cuentaPadreId);
    }

    public CuentaContable actualizarCuenta(String id, String nuevoNombre, TipoCuenta nuevoTipo, String usuarioId) {
        CuentaContable cuenta = cuentaContableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + id));

        if (nuevoNombre != null && !nuevoNombre.isBlank()) cuenta.setNombre(nuevoNombre);
        if (nuevoTipo != null) cuenta.setTipo(nuevoTipo);

        CuentaContable actualizada = cuentaContableRepository.save(cuenta);
        auditoriaService.registrarAuditoria("Actualización de cuenta: " + cuenta.getCodigo(), usuarioId);
        return actualizada;
    }

    public void desactivarCuenta(String id, String usuarioId) {
        CuentaContable cuenta = cuentaContableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + id));

        if (lineaAsientoRepository.existsByCuentaContable(cuenta)) {
            throw new IllegalStateException("No se puede desactivar la cuenta '" + cuenta.getCodigo()
                    + "' porque tiene movimientos contables asociados.");
        }

        cuenta.setActivo(false);
        cuentaContableRepository.save(cuenta);
        auditoriaService.registrarAuditoria("Desactivación de cuenta: " + cuenta.getCodigo(), usuarioId);
    }
}
