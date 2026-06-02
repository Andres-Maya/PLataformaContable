package com.contabilidad.repository;

import com.contabilidad.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByActivoTrue();
    boolean existsByCorreo(String correo);
}
