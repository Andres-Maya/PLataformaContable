package com.contabilidad.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "terceros")
public class Tercero {

    @Id
    private String id;
    private String nombre;
    private String identificacion;

    public Tercero(String id, String nombre, String identificacion) {
        this.id = id;
        this.nombre = nombre;
        this.identificacion = identificacion;
    }

    public Tercero() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    @Override
    public String toString() {
        return "Tercero{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", identificacion='" + identificacion + '\'' +
                '}';
    }
}
