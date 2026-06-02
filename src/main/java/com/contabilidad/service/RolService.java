package com.contabilidad.service;

import com.contabilidad.model.Rol;
import com.contabilidad.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol crearRol(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        if (rolRepository.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con nombre: " + nombre);
        }
        Rol rol = new Rol();
        rol.setId(UUID.randomUUID().toString());
        rol.setNombre(nombre.toUpperCase());
        return rolRepository.save(rol);
    }

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(String id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public void inicializarRoles() {
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            crearRol("ADMIN");
        }
        if (rolRepository.findByNombre("CONTADOR").isEmpty()) {
            crearRol("CONTADOR");
        }
    }
}