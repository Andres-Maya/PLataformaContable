package com.contabilidad.repository;

import com.contabilidad.model.Tercero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TerceroRepository extends MongoRepository<Tercero, String> {
    Optional<Tercero> findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
}