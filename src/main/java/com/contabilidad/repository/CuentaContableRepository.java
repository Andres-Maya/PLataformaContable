package com.contabilidad.repository;

import com.contabilidad.model.CuentaContable;
import com.contabilidad.model.TipoCuenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de CuentaContable en MongoDB.
 * RF-03: Gestión del Plan de Cuentas.
 */
@Repository
public interface CuentaContableRepository extends MongoRepository<CuentaContable, String> {
    Optional<CuentaContable> findByCodigo(String codigo);
    List<CuentaContable> findByActivoTrue();
    List<CuentaContable> findByTipo(TipoCuenta tipo);
    List<CuentaContable> findByCuentaPadreId(String cuentaPadreId);
    boolean existsByCodigo(String codigo);
}
