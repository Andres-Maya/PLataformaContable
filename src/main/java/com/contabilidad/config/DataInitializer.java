package com.contabilidad.config;

import com.contabilidad.model.Rol;
import com.contabilidad.model.Usuario;
import com.contabilidad.repository.UsuarioRepository;
import com.contabilidad.service.RolService;
import com.contabilidad.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            rolService.inicializarRoles();
            System.out.println("Roles base inicializados: ADMIN, CONTADOR");

            if (usuarioRepository.findAll().isEmpty()) {
                Usuario admin = usuarioService.crearUsuario(
                        "Administrador",
                        "andresmaya@gmail.com",
                        "123456"
                );
                Rol rolAdmin = rolService.buscarPorNombre("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
                usuarioService.asignarRol(admin.getId(), rolAdmin.getId());
                System.out.println("Usuario admin creado con rol ADMIN");

                Usuario contador = usuarioService.crearUsuario(
                        "Contador",
                        "maya@gmail.com",
                        "12345"
                );
                Rol rolContador = rolService.buscarPorNombre("CONTADOR")
                        .orElseThrow(() -> new RuntimeException("Rol CONTADOR no encontrado"));
                usuarioService.asignarRol(contador.getId(), rolContador.getId());
                System.out.println("Usuario contador creado con rol CONTADOR");
            }
        };
    }
}