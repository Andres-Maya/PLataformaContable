package com.contabilidad.sistema_contable.model;

public interface Auditable {
    void registrarCambio(String accion, String usuarioId);
}
