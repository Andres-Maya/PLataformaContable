package com.contabilidad.sistema_contable.service;

import com.contabilidad.sistema_contable.model.Tercero;
import com.contabilidad.sistema_contable.repository.TerceroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TerceroService {

    @Autowired
    private TerceroRepository terceroRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public Tercero crearTercero(String nombre, String identificacion, String usuarioId) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (identificacion == null || identificacion.isBlank()) throw new IllegalArgumentException("La identificación es obligatoria.");

        if (terceroRepository.existsByIdentificacion(identificacion)) {
            throw new IllegalArgumentException("Ya existe un tercero con identificación: " + identificacion);
        }

        Tercero tercero = new Tercero();
        tercero.setId(UUID.randomUUID().toString());
        tercero.setNombre(nombre);
        tercero.setIdentificacion(identificacion);

        Tercero guardado = terceroRepository.save(tercero);
        auditoriaService.registrarAuditoria("Creación de tercero: " + nombre + " (" + identificacion + ")", usuarioId);
        return guardado;
    }

    public List<Tercero> listarTodos() {
        return terceroRepository.findAll();
    }

    public Optional<Tercero> buscarPorId(String id) {
        return terceroRepository.findById(id);
    }

    public Optional<Tercero> buscarPorIdentificacion(String identificacion) {
        return terceroRepository.findByIdentificacion(identificacion);
    }

    public Tercero actualizarTercero(String id, String nuevoNombre, String nuevaIdentificacion, String usuarioId) {
        Tercero tercero = terceroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tercero no encontrado: " + id));

        if (nuevoNombre != null && !nuevoNombre.isBlank()) tercero.setNombre(nuevoNombre);
        if (nuevaIdentificacion != null && !nuevaIdentificacion.isBlank()) tercero.setIdentificacion(nuevaIdentificacion);

        Tercero actualizado = terceroRepository.save(tercero);
        auditoriaService.registrarAuditoria("Actualización de tercero: " + id, usuarioId);
        return actualizado;
    }
}