package com.contabilidad.service;

import com.contabilidad.model.Rol;
import com.contabilidad.model.Usuario;
import com.contabilidad.repository.RolRepository;
import com.contabilidad.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public Usuario crearUsuario(String nombre, String correo, String password) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (correo == null || correo.isBlank()) throw new IllegalArgumentException("El correo es obligatorio.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("El password es obligatorio.");

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + correo);
        }

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID().toString());
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setPassword(password);

        Usuario guardado = usuarioRepository.save(usuario);
        auditoriaService.registrarAuditoria("Creación de usuario: " + correo, guardado.getId());
        return guardado;
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario actualizarUsuario(String id, String nuevoNombre, String nuevoCorreo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        if (nuevoNombre != null && !nuevoNombre.isBlank()) usuario.setNombre(nuevoNombre);
        if (nuevoCorreo != null && !nuevoCorreo.isBlank()) usuario.setCorreo(nuevoCorreo);

        Usuario actualizado = usuarioRepository.save(usuario);
        auditoriaService.registrarAuditoria("Actualización de usuario: " + id, id);
        return actualizado;
    }

    public void desactivarUsuario(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        auditoriaService.registrarAuditoria("Desactivación de usuario: " + id, id);
    }

    public Usuario asignarRol(String usuarioId, String rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + rolId));

        usuario.setRol(rol);
        Usuario actualizado = usuarioRepository.save(usuario);
        auditoriaService.registrarAuditoria("Asignación de rol '" + rol.getNombre() + "' a usuario: " + usuarioId, usuarioId);
        return actualizado;
    }

    public boolean tieneRol(String usuarioId, String nombreRol) {
        return usuarioRepository.findById(usuarioId)
                .map(u -> u.getRol() != null && u.getRol().getNombre().equalsIgnoreCase(nombreRol))
                .orElse(false);
    }

    public boolean esAdmin(String usuarioId) {
        return tieneRol(usuarioId, "ADMIN");
    }

    public boolean esContador(String usuarioId) {
        return tieneRol(usuarioId, "CONTADOR") || tieneRol(usuarioId, "ADMIN");
    }
}
