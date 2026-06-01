package com.contabilidad.sistema_contable.service;

import com.contabilidad.sistema_contable.model.Auditoria;
import com.contabilidad.sistema_contable.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public Auditoria registrarAuditoria(String accion, String usuarioId) {
        Auditoria auditoria = new Auditoria();
        auditoria.setId(UUID.randomUUID().toString());
        auditoria.setAccion("[Usuario: " + usuarioId + "] " + accion);
        auditoria.setFecha(new Date());
        return auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> listarTodas() {
        return auditoriaRepository.findAll();
    }

    public List<Auditoria> buscarPorAccion(String texto) {
        return auditoriaRepository.findByAccionContaining(texto);
    }
}
