package com.taller.model;

import java.time.LocalDate;

/**
 * Modelo de datos para una Orden de Reparación
 * Se crea tanto en MongoDB (dentro de Máquina) como en Odoo (repair.order)
 */
public class OrdenReparacion {

    private String numeroSerieMaquina;
    private String descripcionProblema;
    private String tecnicoAsignado;
    private LocalDate fechaEstimadaEntrega;
    private Double presupuestoEstimado;
    private Integer odooRepairId; // ID en Odoo
    private String estado; // draft, confirmed, done, cancel

    public OrdenReparacion() {
        this.estado = "draft";
    }

    public OrdenReparacion(String numeroSerieMaquina, String descripcionProblema,
                          String tecnicoAsignado, LocalDate fechaEstimadaEntrega,
                          Double presupuestoEstimado) {
        this.numeroSerieMaquina = numeroSerieMaquina;
        this.descripcionProblema = descripcionProblema;
        this.tecnicoAsignado = tecnicoAsignado;
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
        this.presupuestoEstimado = presupuestoEstimado;
        this.estado = "draft";
    }

    // Getters y Setters
    public String getNumeroSerieMaquina() {
        return numeroSerieMaquina;
    }

    public void setNumeroSerieMaquina(String numeroSerieMaquina) {
        this.numeroSerieMaquina = numeroSerieMaquina;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }

    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(String tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }

    public LocalDate getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }

    public void setFechaEstimadaEntrega(LocalDate fechaEstimadaEntrega) {
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
    }

    public Double getPresupuestoEstimado() {
        return presupuestoEstimado;
    }

    public void setPresupuestoEstimado(Double presupuestoEstimado) {
        this.presupuestoEstimado = presupuestoEstimado;
    }

    public Integer getOdooRepairId() {
        return odooRepairId;
    }

    public void setOdooRepairId(Integer odooRepairId) {
        this.odooRepairId = odooRepairId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("Orden #%d - %s - %s",
                odooRepairId != null ? odooRepairId : 0,
                numeroSerieMaquina,
                descripcionProblema);
    }
}

