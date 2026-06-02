package com.contabilidad.repository;

import com.contabilidad.model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends MongoRepository<Rol, String> {
    Optional<Rol> findByNombre(String nombre);
}
