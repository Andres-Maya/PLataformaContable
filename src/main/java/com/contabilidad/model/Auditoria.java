package com.contabilidad.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "auditorias")
public class Auditoria {

    @Id
    private String id;
    private String accion;
    private Date fecha;

    public Auditoria(String id, String accion, Date fecha) {
        this.id = id;
        this.accion = accion;
        this.fecha = fecha;
    }

    public Auditoria() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    @Override
    public String toString() {
        return "Auditoria{" +
                "id='" + id + '\'' +
                ", accion='" + accion + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}