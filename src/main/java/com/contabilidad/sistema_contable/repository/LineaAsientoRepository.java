package com.contabilidad.sistema_contable.repository;

import com.contabilidad.sistema_contable.model.CuentaContable;
import com.contabilidad.sistema_contable.model.LineaAsiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaAsientoRepository extends MongoRepository<LineaAsiento, String> {
    List<LineaAsiento> findByCuentaContable(CuentaContable cuentaContable);
    boolean existsByCuentaContable(CuentaContable cuentaContable);
}
