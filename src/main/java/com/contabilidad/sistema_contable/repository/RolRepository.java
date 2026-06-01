package com.contabilidad.sistema_contable.repository;

import com.contabilidad.sistema_contable.model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends MongoRepository<Rol, String> {
    Optional<Rol> findByNombre(String nombre);
}
