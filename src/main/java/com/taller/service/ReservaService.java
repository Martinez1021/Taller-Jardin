package com.taller.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.taller.database.MongoDBConnection;
import com.taller.model.Reserva;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservaService {
    private final MongoCollection<Document> collection;

    public ReservaService() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.collection = database.getCollection("reservas");
    }

    public List<Reserva> obtenerTodas() {
        List<Reserva> reservas = new ArrayList<>();
        for (Document doc : collection.find()) {
            reservas.add(Reserva.fromDocument(doc));
        }
        return reservas;
    }

    public Reserva obtenerPorId(String id) {
        Document doc = collection.find(new Document("_id", id)).first();
        return doc != null ? Reserva.fromDocument(doc) : null;
    }

    public boolean crear(Reserva reserva) {
        try {
            // Generar ID si no existe
            if (reserva.getId() == null || reserva.getId().isEmpty()) {
                reserva.setId(java.util.UUID.randomUUID().toString());
            }
            collection.insertOne(reserva.toDocument());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Reserva reserva) {
        try {
            Document filter = new Document("_id", reserva.getId());
            collection.replaceOne(filter, reserva.toDocument());
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

    public List<Reserva> obtenerPorCliente(String clienteId) {
        List<Reserva> reservas = new ArrayList<>();
        Document filter = new Document("cliente_id", clienteId);
        for (Document doc : collection.find(filter)) {
            reservas.add(Reserva.fromDocument(doc));
        }
        return reservas;
    }

    public List<Reserva> obtenerPorMaquina(String maquinaId) {
        List<Reserva> reservas = new ArrayList<>();
        Document filter = new Document("maquina_id", maquinaId);
        for (Document doc : collection.find(filter)) {
            reservas.add(Reserva.fromDocument(doc));
        }
        return reservas;
    }

    public List<Reserva> obtenerPorEstado(String estado) {
        List<Reserva> reservas = new ArrayList<>();
        Document filter = new Document("estado", estado);
        for (Document doc : collection.find(filter)) {
            reservas.add(Reserva.fromDocument(doc));
        }
        return reservas;
    }

    public List<Reserva> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Reserva> reservas = new ArrayList<>();
        Document filter = new Document("fecha_inicio", 
            new Document("$gte", Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("$lte", Date.from(fin.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        );
        
        for (Document doc : collection.find(filter)) {
            reservas.add(Reserva.fromDocument(doc));
        }
        return reservas;
    }

    public boolean maquinaDisponible(String maquinaId, LocalDate inicio, LocalDate fin) {
        Document filter = new Document("maquina_id", maquinaId)
            .append("estado", new Document("$in", List.of("confirmada", "en_curso")))
            .append("$or", List.of(
                new Document("fecha_inicio", new Document("$lte", Date.from(fin.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                    .append("fecha_fin", new Document("$gte", Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant())))
            ));
        
        return collection.countDocuments(filter) == 0;
    }

    public long contarPorEstado(String estado) {
        return collection.countDocuments(new Document("estado", estado));
    }

    public double obtenerIngresosTotales() {
        double total = 0.0;
        Document filter = new Document("estado", new Document("$in", List.of("completada", "en_curso")));
        for (Document doc : collection.find(filter)) {
            Object precio = doc.get("precio_alquiler");
            if (precio instanceof Number) {
                total += ((Number) precio).doubleValue();
            }
        }
        return total;
    }
}
