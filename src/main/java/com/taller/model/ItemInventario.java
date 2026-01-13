package com.taller.model;

import org.bson.Document;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ItemInventario {
    private String id;
    private String codigo;
    private String nombre;
    private String categoria; // "repuesto", "herramienta", "consumible"
    private String descripcion;
    private int stockActual;
    private int stockMinimo;
    private double precioCompra;
    private double precioVenta;
    private String proveedor;
    private LocalDate fechaUltimaActualizacion;
    private Integer odooProductId;

    public ItemInventario() {
        this.fechaUltimaActualizacion = LocalDate.now();
        this.stockActual = 0;
        this.stockMinimo = 0;
    }

    public Document toDocument() {
        Document doc = new Document();
        if (id != null) doc.append("_id", id);
        doc.append("codigo", codigo)
           .append("nombre", nombre)
           .append("categoria", categoria)
           .append("descripcion", descripcion)
           .append("stock_actual", stockActual)
           .append("stock_minimo", stockMinimo)
           .append("precio_compra", precioCompra)
           .append("precio_venta", precioVenta)
           .append("proveedor", proveedor)
           .append("fecha_actualizacion", Date.from(fechaUltimaActualizacion.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        if (odooProductId != null) {
            doc.append("odoo_product_id", odooProductId);
        }
        
        return doc;
    }

    public static ItemInventario fromDocument(Document doc) {
        ItemInventario item = new ItemInventario();
        
        if (doc.get("_id") != null) {
            item.setId(doc.get("_id").toString());
        }
        
        item.setCodigo(doc.getString("codigo"));
        item.setNombre(doc.getString("nombre"));
        item.setCategoria(doc.getString("categoria"));
        item.setDescripcion(doc.getString("descripcion"));
        item.setStockActual(doc.getInteger("stock_actual", 0));
        item.setStockMinimo(doc.getInteger("stock_minimo", 0));
        item.setProveedor(doc.getString("proveedor"));
        
        Object precioCompraObj = doc.get("precio_compra");
        if (precioCompraObj instanceof Integer) {
            item.setPrecioCompra(((Integer) precioCompraObj).doubleValue());
        } else if (precioCompraObj instanceof Double) {
            item.setPrecioCompra((Double) precioCompraObj);
        }
        
        Object precioVentaObj = doc.get("precio_venta");
        if (precioVentaObj instanceof Integer) {
            item.setPrecioVenta(((Integer) precioVentaObj).doubleValue());
        } else if (precioVentaObj instanceof Double) {
            item.setPrecioVenta((Double) precioVentaObj);
        }
        
        Date fechaDate = doc.getDate("fecha_actualizacion");
        if (fechaDate != null) {
            item.setFechaUltimaActualizacion(fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        item.setOdooProductId(doc.getInteger("odoo_product_id"));
        
        return item;
    }

    public boolean necesitaReposicion() {
        return stockActual <= stockMinimo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public LocalDate getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDate fecha) { this.fechaUltimaActualizacion = fecha; }

    public Integer getOdooProductId() { return odooProductId; }
    public void setOdooProductId(Integer odooProductId) { this.odooProductId = odooProductId; }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
