package com.taller.model;

import org.bson.Document;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Reserva {
    private String id;
    private String clienteId;
    private String clienteNombre;
    private String maquinaId;
    private String maquinaNombre;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; // "pendiente", "confirmada", "en_curso", "completada", "cancelada"
    private double precioAlquiler;
    private String observaciones;
    private Integer odooSaleOrderId;

    public Reserva() {
        this.fechaReserva = LocalDate.now();
        this.estado = "pendiente";
    }

    public Document toDocument() {
        Document doc = new Document();
        if (id != null) doc.append("_id", id);
        doc.append("cliente_id", clienteId)
           .append("cliente_nombre", clienteNombre)
           .append("maquina_id", maquinaId)
           .append("maquina_nombre", maquinaNombre)
           .append("fecha_reserva", Date.from(fechaReserva.atStartOfDay(ZoneId.systemDefault()).toInstant()))
           .append("fecha_inicio", Date.from(fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant()))
           .append("fecha_fin", Date.from(fechaFin.atStartOfDay(ZoneId.systemDefault()).toInstant()))
           .append("estado", estado)
           .append("precio_alquiler", precioAlquiler)
           .append("observaciones", observaciones);
        
        if (odooSaleOrderId != null) {
            doc.append("odoo_sale_order_id", odooSaleOrderId);
        }
        
        return doc;
    }

    public static Reserva fromDocument(Document doc) {
        Reserva reserva = new Reserva();
        
        if (doc.get("_id") != null) {
            Object idObj = doc.get("_id");
            if (idObj instanceof org.bson.types.ObjectId) {
                reserva.setId(((org.bson.types.ObjectId) idObj).toHexString());
            } else {
                reserva.setId(idObj.toString());
            }
        }
        
        reserva.setClienteId(doc.getString("cliente_id"));
        reserva.setClienteNombre(doc.getString("cliente_nombre"));
        reserva.setMaquinaId(doc.getString("maquina_id"));
        reserva.setMaquinaNombre(doc.getString("maquina_nombre"));
        reserva.setEstado(doc.getString("estado"));
        reserva.setObservaciones(doc.getString("observaciones"));
        
        Date fechaReservaDate = doc.getDate("fecha_reserva");
        if (fechaReservaDate != null) {
            reserva.setFechaReserva(fechaReservaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Date fechaInicioDate = doc.getDate("fecha_inicio");
        if (fechaInicioDate != null) {
            reserva.setFechaInicio(fechaInicioDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Date fechaFinDate = doc.getDate("fecha_fin");
        if (fechaFinDate != null) {
            reserva.setFechaFin(fechaFinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Object precioObj = doc.get("precio_alquiler");
        if (precioObj instanceof Integer) {
            reserva.setPrecioAlquiler(((Integer) precioObj).doubleValue());
        } else if (precioObj instanceof Double) {
            reserva.setPrecioAlquiler((Double) precioObj);
        }
        
        reserva.setOdooSaleOrderId(doc.getInteger("odoo_sale_order_id"));
        
        return reserva;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getMaquinaId() { return maquinaId; }
    public void setMaquinaId(String maquinaId) { this.maquinaId = maquinaId; }

    public String getMaquinaNombre() { return maquinaNombre; }
    public void setMaquinaNombre(String maquinaNombre) { this.maquinaNombre = maquinaNombre; }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getPrecioAlquiler() { return precioAlquiler; }
    public void setPrecioAlquiler(double precioAlquiler) { this.precioAlquiler = precioAlquiler; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getOdooSaleOrderId() { return odooSaleOrderId; }
    public void setOdooSaleOrderId(Integer odooSaleOrderId) { this.odooSaleOrderId = odooSaleOrderId; }
}
