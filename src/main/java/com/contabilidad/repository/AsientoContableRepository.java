package com.contabilidad.repository;

import com.contabilidad.model.AsientoContable;
import com.contabilidad.model.EstadoAsiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AsientoContableRepository extends MongoRepository<AsientoContable, String> {
    List<AsientoContable> findByEstado(EstadoAsiento estado);
    List<AsientoContable> findByFechaBetween(Date desde, Date hasta);
    List<AsientoContable> findByUsuarioCreadorId(String usuarioId);
}
