package com.taller.model;

import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Modelo de datos para una reparación
 */
public class Reparacion {

    private LocalDate fecha;
    private String descripcion;
    private String tecnico;
    private Double coste;
    private Integer odooRepairId; // ID de la orden de reparación en Odoo

    public Reparacion() {
    }

    public Reparacion(LocalDate fecha, String descripcion, String tecnico, Double coste) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.tecnico = tecnico;
        this.coste = coste;
    }

    /**
     * Convierte la reparación a un Document de MongoDB
     * @return Document
     */
    public Document toDocument() {
        Document doc = new Document();
        if (fecha != null) {
            doc.append("fecha", Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        doc.append("descripcion", descripcion);
        doc.append("tecnico", tecnico);
        doc.append("coste", coste);
        if (odooRepairId != null) {
            doc.append("odoo_repair_id", odooRepairId);
        }
        return doc;
    }

    /**
     * Crea una Reparacion desde un Document de MongoDB
     * @param doc Document de MongoDB
     * @return Reparacion
     */
    public static Reparacion fromDocument(Document doc) {
        Reparacion reparacion = new Reparacion();

        Date fechaDate = doc.getDate("fecha");
        if (fechaDate != null) {
            reparacion.setFecha(fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        reparacion.setDescripcion(doc.getString("descripcion"));
        reparacion.setTecnico(doc.getString("tecnico"));
        
        // Manejar coste como Integer o Double
        Object costeObj = doc.get("coste");
        if (costeObj instanceof Integer) {
            reparacion.setCoste(((Integer) costeObj).doubleValue());
        } else if (costeObj instanceof Double) {
            reparacion.setCoste((Double) costeObj);
        }
        
        reparacion.setOdooRepairId(doc.getInteger("odoo_repair_id"));

        return reparacion;
    }

    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public Double getCoste() {
        return coste;
    }

    public void setCoste(Double coste) {
        this.coste = coste;
    }

    public Integer getOdooRepairId() {
        return odooRepairId;
    }

    public void setOdooRepairId(Integer odooRepairId) {
        this.odooRepairId = odooRepairId;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - €%.2f",
                fecha != null ? fecha.toString() : "Sin fecha",
                descripcion != null ? descripcion : "Sin descripción",
                coste != null ? coste : 0.0);
    }
}

