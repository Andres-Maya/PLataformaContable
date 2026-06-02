package com.contabilidad.repository;

import com.contabilidad.model.CuentaContable;
import com.contabilidad.model.LineaAsiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaAsientoRepository extends MongoRepository<LineaAsiento, String> {
    List<LineaAsiento> findByCuentaContable(CuentaContable cuentaContable);
    boolean existsByCuentaContable(CuentaContable cuentaContable);
}
