package com.taller.model;

import org.bson.Document;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Modelo de datos para Cliente
 * Se sincroniza con res.partner de Odoo y MongoDB
 */
public class Cliente {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String dni;
    private LocalDate fechaRegistro;
    private Integer odooId;
    private String tipoCliente; // "particular", "empresa"
    private double saldoPendiente;

    public Cliente() {
        this.fechaRegistro = LocalDate.now();
        this.saldoPendiente = 0.0;
    }

    public Cliente(String nombre, String telefono, String email) {
        this();
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public Cliente(Integer odooId, String nombre, String telefono, String email) {
        this();
        this.odooId = odooId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public Document toDocument() {
        Document doc = new Document();
        if (id != null) doc.append("_id", id);
        doc.append("nombre", nombre)
           .append("email", email)
           .append("telefono", telefono)
           .append("direccion", direccion)
           .append("dni", dni)
           .append("fecha_registro", Date.from(fechaRegistro.atStartOfDay(ZoneId.systemDefault()).toInstant()))
           .append("tipo_cliente", tipoCliente)
           .append("saldo_pendiente", saldoPendiente);
        
        if (odooId != null) {
            doc.append("odoo_id", odooId);
        }
        
        return doc;
    }

    public static Cliente fromDocument(Document doc) {
        Cliente cliente = new Cliente();
        
        if (doc.get("_id") != null) {
            Object idObj = doc.get("_id");
            if (idObj instanceof org.bson.types.ObjectId) {
                cliente.setId(((org.bson.types.ObjectId) idObj).toHexString());
            } else {
                cliente.setId(idObj.toString());
            }
        }
        
        cliente.setNombre(doc.getString("nombre"));
        cliente.setEmail(doc.getString("email"));
        cliente.setTelefono(doc.getString("telefono"));
        cliente.setDireccion(doc.getString("direccion"));
        cliente.setDni(doc.getString("dni"));
        cliente.setTipoCliente(doc.getString("tipo_cliente"));
        
        Date fechaDate = doc.getDate("fecha_registro");
        if (fechaDate != null) {
            cliente.setFechaRegistro(fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Object saldoObj = doc.get("saldo_pendiente");
        if (saldoObj instanceof Integer) {
            cliente.setSaldoPendiente(((Integer) saldoObj).doubleValue());
        } else if (saldoObj instanceof Double) {
            cliente.setSaldoPendiente((Double) saldoObj);
        }
        
        cliente.setOdooId(doc.getInteger("odoo_id"));
        
        return cliente;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getOdooId() {
        return odooId;
    }

    public void setOdooId(Integer odooId) {
        this.odooId = odooId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public double getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(double saldoPendiente) { this.saldoPendiente = saldoPendiente; }

    @Override
    public String toString() {
        return nombre + (dni != null ? " (" + dni + ")" : "");
    }
}

