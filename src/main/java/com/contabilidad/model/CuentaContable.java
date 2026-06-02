package com.contabilidad.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "cuentas_contables")
public class CuentaContable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nombre;
    private TipoCuenta tipo;
    private boolean activo = true;

    private String cuentaPadreId;

    public CuentaContable(String id, String codigo, String nombre, TipoCuenta tipo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public CuentaContable() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCuenta getTipo() {
        return tipo;
    }

    public void setTipo(TipoCuenta tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCuentaPadreId() {
        return cuentaPadreId;
    }

    public void setCuentaPadreId(String cuentaPadreId) {
        this.cuentaPadreId = cuentaPadreId;
    }


    @Override
    public String toString() {
        return "CuentaContable{" +
                "id='" + id + '\'' +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", activo=" + activo +
                '}';
    }
}