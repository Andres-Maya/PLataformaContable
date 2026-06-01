package com.contabilidad.sistema_contable.config;

import com.contabilidad.sistema_contable.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private RolService rolService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            rolService.inicializarRoles();
            System.out.println("✅ Roles base inicializados: ADMIN, CONTADOR");
        };
    }
}