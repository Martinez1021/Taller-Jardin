package com.taller.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.taller.database.MongoDBConnection;
import com.taller.model.Factura;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FacturaService {
    private final MongoCollection<Document> collection;

    public FacturaService() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.collection = database.getCollection("facturas");
    }

    public List<Factura> obtenerTodas() {
        List<Factura> facturas = new ArrayList<>();
        for (Document doc : collection.find()) {
            facturas.add(Factura.fromDocument(doc));
        }
        return facturas;
    }

    public Factura obtenerPorId(String id) {
        Document doc = collection.find(new Document("_id", id)).first();
        return doc != null ? Factura.fromDocument(doc) : null;
    }

    public Factura obtenerPorNumero(String numeroFactura) {
        Document doc = collection.find(new Document("numero_factura", numeroFactura)).first();
        return doc != null ? Factura.fromDocument(doc) : null;
    }

    public boolean crear(Factura factura) {
        try {
            // Generar número de factura si no existe
            if (factura.getNumeroFactura() == null || factura.getNumeroFactura().isEmpty()) {
                factura.setNumeroFactura(generarNumeroFactura());
            }
            collection.insertOne(factura.toDocument());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Factura factura) {
        try {
            Document filter = new Document("_id", factura.getId());
            collection.replaceOne(filter, factura.toDocument());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(String id) {
        try {
            Document filter = new Document("_id", id);
            collection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstado(String id, String nuevoEstado) {
        try {
            Document filter = new Document("_id", id);
            Document update = new Document("$set", new Document("estado", nuevoEstado));
            collection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Factura> obtenerPorCliente(String clienteId) {
        List<Factura> facturas = new ArrayList<>();
        Document filter = new Document("cliente_id", clienteId);
        for (Document doc : collection.find(filter)) {
            facturas.add(Factura.fromDocument(doc));
        }
        return facturas;
    }

    public List<Factura> obtenerPorEstado(String estado) {
        List<Factura> facturas = new ArrayList<>();
        Document filter = new Document("estado", estado);
        for (Document doc : collection.find(filter)) {
            facturas.add(Factura.fromDocument(doc));
        }
        return facturas;
    }

    public List<Factura> obtenerVencidas() {
        List<Factura> facturas = new ArrayList<>();
        Document filter = new Document("estado", new Document("$in", List.of("emitida")))
            .append("fecha_vencimiento", new Document("$lt", new Date()));
        
        for (Document doc : collection.find(filter)) {
            facturas.add(Factura.fromDocument(doc));
        }
        return facturas;
    }

    public List<Factura> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Factura> facturas = new ArrayList<>();
        // Ajustamos fin al final del día (o inicio del día siguiente) para incluir todas las fechas
        Document filter = new Document("fecha_emision", 
            new Document("$gte", Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("$lt", Date.from(fin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        );
        
        for (Document doc : collection.find(filter)) {
            facturas.add(Factura.fromDocument(doc));
        }
        return facturas;
    }

    public double obtenerTotalPorEstado(String estado) {
        double total = 0.0;
        Document filter = new Document("estado", estado);
        for (Document doc : collection.find(filter)) {
            Object totalObj = doc.get("total");
            if (totalObj instanceof Number) {
                total += ((Number) totalObj).doubleValue();
            }
        }
        return total;
    }

    public double obtenerTotalFacturado() {
        return obtenerTotalPorEstado("emitida") + obtenerTotalPorEstado("pagada");
    }

    public double obtenerTotalPagado() {
        return obtenerTotalPorEstado("pagada");
    }

    public double obtenerTotalPendiente() {
        return obtenerTotalPorEstado("emitida");
    }

    public long contarPorEstado(String estado) {
        return collection.countDocuments(new Document("estado", estado));
    }

    private String generarNumeroFactura() {
        // Formato: FAC-YYYY-NNNN
        int year = LocalDate.now().getYear();
        long count = collection.countDocuments() + 1;
        return String.format("FAC-%d-%04d", year, count);
    }

    public double obtenerTotalFacturadoMesActual() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now();
        
        Document filter = new Document("fecha_emision", new Document("$gte", 
            Date.from(inicioMes.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .append("$lte", Date.from(finMes.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        
        double total = 0.0;
        for (Document doc : collection.find(filter)) {
            Object totalObj = doc.get("total");
            if (totalObj instanceof Number) {
                total += ((Number) totalObj).doubleValue();
            }
        }
        return total;
    }

    public java.util.Map<String, Double> obtenerFacturacionUltimos6Meses() {
        java.util.Map<String, Double> resultado = new java.util.LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = hoy.minusMonths(i);
            LocalDate inicioMes = mes.withDayOfMonth(1);
            LocalDate finMes = mes.withDayOfMonth(mes.lengthOfMonth());
            
            Document filter = new Document("fecha_emision", new Document("$gte", 
                Date.from(inicioMes.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("$lte", Date.from(finMes.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            
            double total = 0.0;
            for (Document doc : collection.find(filter)) {
                Object totalObj = doc.get("total");
                if (totalObj instanceof Number) {
                    total += ((Number) totalObj).doubleValue();
                }
            }
            
            String nombreMes = mes.getMonth().toString().substring(0, 3);
            resultado.put(nombreMes + " " + mes.getYear(), total);
        }
        return resultado;
    }

    public List<java.util.Map.Entry<String, Double>> obtenerTopClientes(int limit) {
        java.util.Map<String, Double> clientesTotal = new java.util.HashMap<>();
        
        for (Document doc : collection.find()) {
            String clienteNombre = doc.getString("cliente_nombre");
            if (clienteNombre != null) {
                Object totalObj = doc.get("total");
                double total = 0.0;
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).doubleValue();
                }
                clientesTotal.merge(clienteNombre, total, Double::sum);
            }
        }
        
        return clientesTotal.entrySet().stream()
            .sorted(java.util.Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }
}
