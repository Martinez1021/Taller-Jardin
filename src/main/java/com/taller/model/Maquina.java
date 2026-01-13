package com.taller.model;

import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Modelo de datos para una máquina de jardín
 * Se almacena en MongoDB y se vincula con Odoo mediante IDs
 */
public class Maquina {

    private String numeroSerie;
    private String tipo; // Cortacésped, Motosierra, Desbrozadora, etc.
    private String marca;
    private String modelo;
    private Integer clienteId; // ID del cliente en Odoo
    private String clienteNombre;
    private String telefono;
    private String email;
    private LocalDate fechaCompra;
    private LocalDate garantiaHasta;
    private Integer odooProductId; // ID del producto en Odoo
    private List<Reparacion> reparaciones;

    public Maquina() {
        this.reparaciones = new ArrayList<>();
    }

    public Maquina(String numeroSerie, String tipo, String marca, String modelo,
                   String clienteNombre, LocalDate fechaCompra, LocalDate garantiaHasta) {
        this.numeroSerie = numeroSerie;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.clienteNombre = clienteNombre;
        this.fechaCompra = fechaCompra;
        this.garantiaHasta = garantiaHasta;
        this.reparaciones = new ArrayList<>();
    }

    /**
     * Verifica si la máquina está en garantía
     * @return true si está en garantía, false en caso contrario
     */
    public boolean isEnGarantia() {
        if (garantiaHasta == null) {
            return false;
        }
        return LocalDate.now().isBefore(garantiaHasta) || LocalDate.now().isEqual(garantiaHasta);
    }

    /**
     * Calcula los días restantes de garantía
     * @return Días restantes (negativo si ya venció)
     */
    public long getDiasGarantiaRestantes() {
        if (garantiaHasta == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), garantiaHasta);
    }

    /**
     * Convierte la máquina a un Document de MongoDB
     * @return Document
     */
    public Document toDocument() {
        Document doc = new Document();
        doc.append("numero_serie", numeroSerie);
        doc.append("tipo", tipo);
        doc.append("marca", marca);
        doc.append("modelo", modelo);
        doc.append("cliente_id", clienteId);
        doc.append("cliente_nombre", clienteNombre);
        doc.append("telefono", telefono);
        doc.append("email", email);

        if (fechaCompra != null) {
            doc.append("fecha_compra", Date.from(fechaCompra.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (garantiaHasta != null) {
            doc.append("garantia_hasta", Date.from(garantiaHasta.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (odooProductId != null) {
            doc.append("odoo_product_id", odooProductId);
        }

        // Convertir lista de reparaciones
        List<Document> reparacionesDocs = new ArrayList<>();
        for (Reparacion reparacion : reparaciones) {
            reparacionesDocs.add(reparacion.toDocument());
        }
        doc.append("reparaciones", reparacionesDocs);

        return doc;
    }

    /**
     * Crea una Maquina desde un Document de MongoDB
     * @param doc Document de MongoDB
     * @return Maquina
     */
    @SuppressWarnings("unchecked")
    public static Maquina fromDocument(Document doc) {
        Maquina maquina = new Maquina();

        maquina.setNumeroSerie(doc.getString("numero_serie"));
        maquina.setTipo(doc.getString("tipo"));
        maquina.setMarca(doc.getString("marca"));
        maquina.setModelo(doc.getString("modelo"));
        maquina.setClienteId(doc.getInteger("cliente_id"));
        maquina.setClienteNombre(doc.getString("cliente_nombre"));
        maquina.setTelefono(doc.getString("telefono"));
        maquina.setEmail(doc.getString("email"));

        Date fechaCompraDate = doc.getDate("fecha_compra");
        if (fechaCompraDate != null) {
            maquina.setFechaCompra(fechaCompraDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        Date garantiaHastaDate = doc.getDate("garantia_hasta");
        if (garantiaHastaDate != null) {
            maquina.setGarantiaHasta(garantiaHastaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        maquina.setOdooProductId(doc.getInteger("odoo_product_id"));

        // Convertir lista de reparaciones
        List<Document> reparacionesDocs = (List<Document>) doc.get("reparaciones");
        if (reparacionesDocs != null) {
            for (Document repDoc : reparacionesDocs) {
                maquina.getReparaciones().add(Reparacion.fromDocument(repDoc));
            }
        }

        return maquina;
    }

    // Getters y Setters
    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
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

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public LocalDate getGarantiaHasta() {
        return garantiaHasta;
    }

    public void setGarantiaHasta(LocalDate garantiaHasta) {
        this.garantiaHasta = garantiaHasta;
    }

    public Integer getOdooProductId() {
        return odooProductId;
    }

    public void setOdooProductId(Integer odooProductId) {
        this.odooProductId = odooProductId;
    }

    public List<Reparacion> getReparaciones() {
        return reparaciones;
    }

    public void setReparaciones(List<Reparacion> reparaciones) {
        this.reparaciones = reparaciones;
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s %s", numeroSerie, tipo, marca, modelo);
    }
}

