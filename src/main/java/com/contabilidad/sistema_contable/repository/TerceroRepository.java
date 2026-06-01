package com.contabilidad.sistema_contable.repository;

import com.contabilidad.sistema_contable.model.Tercero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TerceroRepository extends MongoRepository<Tercero, String> {
    Optional<Tercero> findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
}