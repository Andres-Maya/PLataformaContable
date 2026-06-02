package com.contabilidad.repository;

import com.contabilidad.model.Auditoria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AuditoriaRepository extends MongoRepository<Auditoria, String> {
    List<Auditoria> findByFechaBetween(Date desde, Date hasta);
    List<Auditoria> findByAccionContaining(String texto);
}
