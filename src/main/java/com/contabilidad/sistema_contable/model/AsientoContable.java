package com.contabilidad.sistema_contable.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "asientos_contables")
public class AsientoContable implements Auditable {

    @Id
    private String id;
    private Date fecha;
    private String descripcion;
    private EstadoAsiento estado;

    @DBRef
    private List<LineaAsiento> lineas = new ArrayList<>();

    private String usuarioCreadorId;

    private List<Auditoria> historialAuditorias = new ArrayList<>();

    public AsientoContable(String id, Date fecha, String descripcion, EstadoAsiento estado) {
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public AsientoContable() {
    }

    public boolean validarBalance() {
        double totalDebitos = lineas.stream().mapToDouble(LineaAsiento::getDebito).sum();
        double totalCreditos = lineas.stream().mapToDouble(LineaAsiento::getCredito).sum();
        return Math.abs(totalDebitos - totalCreditos) < 0.001;
    }

    @Override
    public void registrarCambio(String accion, String usuarioId) {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion("[Usuario: " + usuarioId + "] " + accion);
        auditoria.setFecha(new Date());
        this.historialAuditorias.add(auditoria);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoAsiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }

    public List<LineaAsiento> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaAsiento> lineas) {
        this.lineas = lineas;
    }

    public String getUsuarioCreadorId() {
        return usuarioCreadorId;
    }

    public void setUsuarioCreadorId(String usuarioCreadorId) {
        this.usuarioCreadorId = usuarioCreadorId;
    }

    public List<Auditoria> getHistorialAuditorias() {
        return historialAuditorias;
    }

    public void setHistorialAuditorias(List<Auditoria> historialAuditorias) {
        this.historialAuditorias = historialAuditorias;
    }

    @Override
    public String toString() {
        return "AsientoContable{" +
                "id='" + id + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                ", lineas=" + lineas.size() +
                '}';
    }
}
