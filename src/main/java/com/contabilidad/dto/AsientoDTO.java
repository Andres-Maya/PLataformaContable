package com.contabilidad.dto;

import java.util.List;

public class AsientoDTO {

    private String fecha;
    private String descripcion;
    private String usuarioId;
    private List<LineaAsientoDTO> lineas;

    public AsientoDTO() {}

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public List<LineaAsientoDTO> getLineas() { return lineas; }
    public void setLineas(List<LineaAsientoDTO> lineas) { this.lineas = lineas; }

    public static class LineaAsientoDTO {
        private String cuentaId;
        private double debito;
        private double credito;
        private String terceroId;

        public LineaAsientoDTO() {}

        public String getCuentaId() { return cuentaId; }
        public void setCuentaId(String cuentaId) { this.cuentaId = cuentaId; }

        public double getDebito() { return debito; }
        public void setDebito(double debito) { this.debito = debito; }

        public double getCredito() { return credito; }
        public void setCredito(double credito) { this.credito = credito; }

        public String getTerceroId() { return terceroId; }
        public void setTerceroId(String terceroId) { this.terceroId = terceroId; }
    }
}