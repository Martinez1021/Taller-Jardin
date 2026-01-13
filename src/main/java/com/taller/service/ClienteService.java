package com.taller.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.taller.database.MongoDBConnection;
import com.taller.model.Cliente;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ClienteService {
    private final MongoCollection<Document> collection;

    public ClienteService() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.collection = database.getCollection("clientes");
    }

    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        for (Document doc : collection.find()) {
            clientes.add(Cliente.fromDocument(doc));
        }
        return clientes;
    }

    public Cliente obtenerPorId(String id) {
        try {
            // Intentar con ObjectId primero
            Document filter;
            try {
                filter = new Document("_id", new org.bson.types.ObjectId(id));
            } catch (IllegalArgumentException e) {
                // Si falla, usar el id como String
                filter = new Document("_id", id);
            }
            Document doc = collection.find(filter).first();
            return doc != null ? Cliente.fromDocument(doc) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean crear(Cliente cliente) {
        try {
            collection.insertOne(cliente.toDocument());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Cliente cliente) {
        try {
            // Intentar con ObjectId primero
            Document filter;
            try {
                filter = new Document("_id", new org.bson.types.ObjectId(cliente.getId()));
            } catch (IllegalArgumentException e) {
                // Si falla, usar el id como String
                filter = new Document("_id", cliente.getId());
            }
            collection.replaceOne(filter, cliente.toDocument());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(String id) {
        try {
            // Intentar con ObjectId primero
            Document filter;
            try {
                filter = new Document("_id", new org.bson.types.ObjectId(id));
            } catch (IllegalArgumentException e) {
                // Si falla, usar el id como String
                filter = new Document("_id", id);
            }
            collection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cliente> buscar(String texto) {
        List<Cliente> clientes = new ArrayList<>();
        Document filter = new Document("$or", List.of(
            new Document("nombre", new Document("$regex", texto).append("$options", "i")),
            new Document("dni", new Document("$regex", texto).append("$options", "i")),
            new Document("email", new Document("$regex", texto).append("$options", "i"))
        ));
        
        for (Document doc : collection.find(filter)) {
            clientes.add(Cliente.fromDocument(doc));
        }
        return clientes;
    }

    public long contarTotal() {
        return collection.countDocuments();
    }

    public double obtenerSaldoTotalPendiente() {
        double total = 0.0;
        for (Document doc : collection.find()) {
            Object saldo = doc.get("saldoPendiente");
            if (saldo == null) {
                saldo = doc.get("saldo_pendiente");
            }
            if (saldo instanceof Number) {
                total += ((Number) saldo).doubleValue();
            }
        }
        return total;
    }

    public long contarNuevosEsteMes() {
        java.time.LocalDate inicioMes = java.time.LocalDate.now().withDayOfMonth(1);
        java.time.LocalDate finMes = java.time.LocalDate.now();
        
        Document filter = new Document("fecha_registro", new Document("$gte", 
            java.util.Date.from(inicioMes.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()))
            .append("$lte", java.util.Date.from(finMes.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())));
        
        return collection.countDocuments(filter);
    }
}
