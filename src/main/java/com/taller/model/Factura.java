package com.taller.model;

import org.bson.Document;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Factura {
    private String id;
    private String numeroFactura;
    private String clienteId;
    private String clienteNombre;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private List<ItemFactura> items;
    private double subtotal;
    private double iva;
    private double total;
    private String estado; // "borrador", "emitida", "pagada", "vencida", "cancelada"
    private String metodoPago;
    private String observaciones;
    private Integer odooInvoiceId;

    public Factura() {
        this.fechaEmision = LocalDate.now();
        this.fechaVencimiento = LocalDate.now().plusDays(30);
        this.items = new ArrayList<>();
        this.estado = "borrador";
        this.iva = 0.21; // 21% IVA por defecto
    }

    public void calcularTotales() {
        this.subtotal = items.stream()
            .mapToDouble(ItemFactura::getTotal)
            .sum();
        this.total = subtotal + (subtotal * iva);
    }

    public Document toDocument() {
        calcularTotales();
        
        Document doc = new Document();
        if (id != null) doc.append("_id", id);
        doc.append("numero_factura", numeroFactura)
           .append("cliente_id", clienteId)
           .append("cliente_nombre", clienteNombre)
           .append("fecha_emision", Date.from(fechaEmision.atStartOfDay(ZoneId.systemDefault()).toInstant()))
           .append("fecha_vencimiento", Date.from(fechaVencimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()))
           .append("items", items.stream().map(ItemFactura::toDocument).collect(Collectors.toList()))
           .append("subtotal", subtotal)
           .append("iva", iva)
           .append("total", total)
           .append("estado", estado)
           .append("metodo_pago", metodoPago)
           .append("observaciones", observaciones);
        
        if (odooInvoiceId != null) {
            doc.append("odoo_invoice_id", odooInvoiceId);
        }
        
        return doc;
    }

    @SuppressWarnings("unchecked")
    public static Factura fromDocument(Document doc) {
        Factura factura = new Factura();
        
        if (doc.get("_id") != null) {
            factura.setId(doc.get("_id").toString());
        }
        
        factura.setNumeroFactura(doc.getString("numero_factura"));
        
        Object clienteIdObj = doc.get("cliente_id");
        if (clienteIdObj != null) {
            factura.setClienteId(clienteIdObj.toString());
        }

        factura.setClienteNombre(doc.getString("cliente_nombre"));
        factura.setEstado(doc.getString("estado"));
        factura.setMetodoPago(doc.getString("metodo_pago"));
        factura.setObservaciones(doc.getString("observaciones"));
        
        Date fechaEmisionDate = doc.getDate("fecha_emision");
        if (fechaEmisionDate != null) {
            factura.setFechaEmision(fechaEmisionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Date fechaVencimientoDate = doc.getDate("fecha_vencimiento");
        if (fechaVencimientoDate != null) {
            factura.setFechaVencimiento(fechaVencimientoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Object subtotalObj = doc.get("subtotal");
        if (subtotalObj instanceof Number) {
            factura.setSubtotal(((Number) subtotalObj).doubleValue());
        }
        
        Object ivaObj = doc.get("iva");
        if (ivaObj instanceof Number) {
            factura.setIva(((Number) ivaObj).doubleValue());
        }
        
        Object totalObj = doc.get("total");
        if (totalObj instanceof Number) {
            factura.setTotal(((Number) totalObj).doubleValue());
        }
        
        List<Document> itemsDocs = (List<Document>) doc.get("items");
        if (itemsDocs != null) {
            factura.setItems(itemsDocs.stream()
                .map(ItemFactura::fromDocument)
                .collect(Collectors.toList()));
        }
        
        Object odooIdObj = doc.get("odoo_invoice_id");
        if (odooIdObj instanceof Number) {
            factura.setOdooInvoiceId(((Number) odooIdObj).intValue());
        }
        
        return factura;
    }

    public void agregarItem(ItemFactura item) {
        this.items.add(item);
        calcularTotales();
    }

    public void eliminarItem(ItemFactura item) {
        this.items.remove(item);
        calcularTotales();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public List<ItemFactura> getItems() { return items; }
    public void setItems(List<ItemFactura> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getOdooInvoiceId() { return odooInvoiceId; }
    public void setOdooInvoiceId(Integer odooInvoiceId) { this.odooInvoiceId = odooInvoiceId; }
}
