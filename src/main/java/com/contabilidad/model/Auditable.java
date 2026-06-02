package com.contabilidad.model;

public interface Auditable {
    void registrarCambio(String accion, String usuarioId);
}
