package com.contabilidad.sistema_contable.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "lineas_asiento")
public class LineaAsiento {

    @Id
    private String id;
    private double debito;
    private double credito;

    @DBRef
    private CuentaContable cuentaContable;

    @DBRef
    private Tercero tercero;

    public LineaAsiento(String id, double debito, double credito) {
        this.id = id;
        this.debito = debito;
        this.credito = credito;
    }

    public LineaAsiento() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDebito() {
        return debito;
    }

    public void setDebito(double debito) {
        this.debito = debito;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public Tercero getTercero() {
        return tercero;
    }

    public void setTercero(Tercero tercero) {
        this.tercero = tercero;
    }

    @Override
    public String toString() {
        return "LineaAsiento{" +
                "id='" + id + '\'' +
                ", cuenta=" + (cuentaContable != null ? cuentaContable.getCodigo() + " " + cuentaContable.getNombre() : "N/A") +
                ", debito=" + debito +
                ", credito=" + credito +
                ", tercero=" + (tercero != null ? tercero.getNombre() : "N/A") +
                '}';
    }
}