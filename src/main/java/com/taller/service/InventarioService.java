package com.taller.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.taller.database.MongoDBConnection;
import com.taller.model.ItemInventario;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventarioService {
    private final MongoCollection<Document> collection;

    public InventarioService() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.collection = database.getCollection("inventario");
    }

    public List<ItemInventario> obtenerTodos() {
        List<ItemInventario> items = new ArrayList<>();
        for (Document doc : collection.find()) {
            items.add(ItemInventario.fromDocument(doc));
        }
        return items;
    }

    public ItemInventario obtenerPorId(String id) {
        Document doc = collection.find(new Document("_id", id)).first();
        return doc != null ? ItemInventario.fromDocument(doc) : null;
    }

    public ItemInventario obtenerPorCodigo(String codigo) {
        Document doc = collection.find(new Document("codigo", codigo)).first();
        return doc != null ? ItemInventario.fromDocument(doc) : null;
    }

    public boolean crear(ItemInventario item) {
        try {
            item.setFechaUltimaActualizacion(LocalDate.now());
            collection.insertOne(item.toDocument());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(ItemInventario item) {
        try {
            item.setFechaUltimaActualizacion(LocalDate.now());
            Document filter = new Document("_id", item.getId());
            collection.replaceOne(filter, item.toDocument());
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

    public boolean actualizarStock(String id, int nuevoStock) {
        try {
            Document filter = new Document("_id", id);
            Document update = new Document("$set", new Document()
                .append("stock_actual", nuevoStock)
                .append("fecha_actualizacion", new java.util.Date()));
            collection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ItemInventario> obtenerBajoStock() {
        List<ItemInventario> items = new ArrayList<>();
        for (Document doc : collection.find()) {
            ItemInventario item = ItemInventario.fromDocument(doc);
            if (item.necesitaReposicion()) {
                items.add(item);
            }
        }
        return items;
    }

    public List<ItemInventario> buscarPorCategoria(String categoria) {
        List<ItemInventario> items = new ArrayList<>();
        Document filter = new Document("categoria", categoria);
        for (Document doc : collection.find(filter)) {
            items.add(ItemInventario.fromDocument(doc));
        }
        return items;
    }

    public List<ItemInventario> buscar(String texto) {
        List<ItemInventario> items = new ArrayList<>();
        Document filter = new Document("$or", List.of(
            new Document("codigo", new Document("$regex", texto).append("$options", "i")),
            new Document("nombre", new Document("$regex", texto).append("$options", "i")),
            new Document("descripcion", new Document("$regex", texto).append("$options", "i"))
        ));
        
        for (Document doc : collection.find(filter)) {
            items.add(ItemInventario.fromDocument(doc));
        }
        return items;
    }

    public double obtenerValorTotalInventario() {
        double total = 0.0;
        for (Document doc : collection.find()) {
            ItemInventario item = ItemInventario.fromDocument(doc);
            total += item.getStockActual() * item.getPrecioCompra();
        }
        return total;
    }

    public long contarTotal() {
        return collection.countDocuments();
    }

    public long contarBajoStock() {
        return obtenerBajoStock().size();
    }

    public long contarStockBajoMinimo() {
        return contarBajoStock();
    }

    public java.util.Map<String, Integer> obtenerStockPorCategoria() {
        java.util.Map<String, Integer> resultado = new java.util.HashMap<>();
        
        for (Document doc : collection.find()) {
            String categoria = doc.getString("categoria");
            Integer stock = doc.getInteger("stock_actual", 0);
            
            if (categoria != null && !categoria.isEmpty()) {
                resultado.merge(categoria, stock, Integer::sum);
            }
        }
        
        return resultado;
    }
}
