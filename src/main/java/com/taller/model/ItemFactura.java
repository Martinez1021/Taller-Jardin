package com.taller.model;

import org.bson.Document;

public class ItemFactura {
    private String concepto;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double descuento; // porcentaje
    private double total;

    public ItemFactura() {
        this.cantidad = 1;
        this.descuento = 0.0;
    }

    public ItemFactura(String concepto, int cantidad, double precioUnitario) {
        this();
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularTotal();
    }

    public void calcularTotal() {
        double subtotal = cantidad * precioUnitario;
        double montoDescuento = subtotal * (descuento / 100.0);
        this.total = subtotal - montoDescuento;
    }

    public Document toDocument() {
        calcularTotal();
        return new Document()
            .append("concepto", concepto)
            .append("descripcion", descripcion)
            .append("cantidad", cantidad)
            .append("precio_unitario", precioUnitario)
            .append("descuento", descuento)
            .append("total", total);
    }

    public static ItemFactura fromDocument(Document doc) {
        ItemFactura item = new ItemFactura();
        
        item.setConcepto(doc.getString("concepto"));
        item.setDescripcion(doc.getString("descripcion"));
        item.setCantidad(doc.getInteger("cantidad", 1));
        
        Object precioObj = doc.get("precio_unitario");
        if (precioObj instanceof Integer) {
            item.setPrecioUnitario(((Integer) precioObj).doubleValue());
        } else if (precioObj instanceof Double) {
            item.setPrecioUnitario((Double) precioObj);
        }
        
        Object descuentoObj = doc.get("descuento");
        if (descuentoObj instanceof Integer) {
            item.setDescuento(((Integer) descuentoObj).doubleValue());
        } else if (descuentoObj instanceof Double) {
            item.setDescuento((Double) descuentoObj);
        }
        
        Object totalObj = doc.get("total");
        if (totalObj instanceof Integer) {
            item.setTotal(((Integer) totalObj).doubleValue());
        } else if (totalObj instanceof Double) {
            item.setTotal((Double) totalObj);
        }
        
        return item;
    }

    // Getters y Setters
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
        calcularTotal();
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { 
        this.precioUnitario = precioUnitario;
        calcularTotal();
    }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { 
        this.descuento = descuento;
        calcularTotal();
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
