package com.taller.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.taller.database.MongoDBConnection;
import com.taller.model.Maquina;
import com.taller.model.Reparacion;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para operaciones CRUD de máquinas en MongoDB
 */
public class MaquinaService {

    private final MongoCollection<Document> coleccion;

    public MaquinaService() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.coleccion = database.getCollection("maquinas");
    }

    /**
     * Guarda una nueva máquina en MongoDB
     */
    public boolean guardar(Maquina maquina) throws Exception {
        try {
            // Verificar que el número de serie no exista
            if (buscarPorNumeroSerie(maquina.getNumeroSerie()) != null) {
                throw new Exception("Ya existe una máquina con el número de serie: " + maquina.getNumeroSerie());
            }

            Document doc = maquina.toDocument();
            coleccion.insertOne(doc);

            System.out.println("✓ Máquina guardada en MongoDB: " + maquina.getNumeroSerie());
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error al guardar máquina: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene todas las máquinas
     */
    public List<Maquina> obtenerTodas() {
        List<Maquina> maquinas = new ArrayList<>();

        try {
            System.out.println("🔍 Consultando MongoDB...");
            long count = coleccion.countDocuments();
            System.out.println("📦 Total de documentos en MongoDB: " + count);

            for (Document doc : coleccion.find()) {
                maquinas.add(Maquina.fromDocument(doc));
            }

            System.out.println("✓ " + maquinas.size() + " máquinas cargadas desde MongoDB");
        } catch (Exception e) {
            System.err.println("✗ Error al obtener máquinas: " + e.getMessage());
            e.printStackTrace();
        }

        return maquinas;
    }

    /**
     * Busca una máquina por número de serie
     */
    public Maquina buscarPorNumeroSerie(String numeroSerie) {
        try {
            Document doc = coleccion.find(Filters.eq("numero_serie", numeroSerie)).first();

            if (doc != null) {
                return Maquina.fromDocument(doc);
            }

        } catch (Exception e) {
            System.err.println("✗ Error al buscar máquina: " + e.getMessage());
        }

        return null;
    }

    /**
     * Busca máquinas por cliente
     */
    public List<Maquina> buscarPorCliente(String cliente) {
        List<Maquina> maquinas = new ArrayList<>();

        try {
            for (Document doc : coleccion.find(Filters.regex("cliente_nombre", ".*" + cliente + ".*", "i"))) {
                maquinas.add(Maquina.fromDocument(doc));
            }
        } catch (Exception e) {
            System.err.println("✗ Error al buscar por cliente: " + e.getMessage());
        }

        return maquinas;
    }

    /**
     * Obtiene máquinas en garantía
     */
    public List<Maquina> obtenerMaquinasEnGarantia() {
        List<Maquina> todas = obtenerTodas();
        List<Maquina> enGarantia = new ArrayList<>();

        for (Maquina maquina : todas) {
            if (maquina.isEnGarantia()) {
                enGarantia.add(maquina);
            }
        }

        return enGarantia;
    }

    /**
     * Actualiza una máquina
     */
    public boolean actualizar(Maquina maquina) {
        try {
            Document doc = maquina.toDocument();

            coleccion.replaceOne(
                    Filters.eq("numero_serie", maquina.getNumeroSerie()),
                    doc
            );

            System.out.println("✓ Máquina actualizada: " + maquina.getNumeroSerie());
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error al actualizar máquina: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una máquina
     */
    public boolean eliminar(String numeroSerie) {
        try {
            coleccion.deleteOne(Filters.eq("numero_serie", numeroSerie));
            System.out.println("✓ Máquina eliminada: " + numeroSerie);
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error al eliminar máquina: " + e.getMessage());
            return false;
        }
    }

    /**
     * Añade una reparación a una máquina existente
     */
    public boolean añadirReparacion(String numeroSerie, Reparacion reparacion, Integer odooRepairId) {
        try {
            reparacion.setOdooRepairId(odooRepairId);
            Document repDoc = reparacion.toDocument();

            coleccion.updateOne(
                    Filters.eq("numero_serie", numeroSerie),
                    Updates.push("reparaciones", repDoc)
            );

            System.out.println("✓ Reparación añadida a la máquina: " + numeroSerie);
            if (odooRepairId != null) {
                System.out.println("  Vinculada con Odoo Repair ID: " + odooRepairId);
            }
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error al añadir reparación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene máquinas por estado
     */
    public List<Maquina> obtenerPorEstado(String estado) {
        List<Maquina> maquinas = new ArrayList<>();
        try {
            for (Document doc : coleccion.find(Filters.eq("estado", estado))) {
                maquinas.add(Maquina.fromDocument(doc));
            }
        } catch (Exception e) {
            System.err.println("✗ Error al obtener máquinas por estado: " + e.getMessage());
        }
        return maquinas;
    }

    /**
     * Cuenta el total de máquinas
     */
    public long contarTotal() {
        try {
            return coleccion.countDocuments();
        } catch (Exception e) {
            System.err.println("✗ Error al contar máquinas: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Cuenta reparaciones en un rango de fechas
     */
    public long contarReparacionesEnRango(java.time.LocalDate inicio, java.time.LocalDate fin) {
        long count = 0;
        try {
            for (Maquina maquina : obtenerTodas()) {
                if (maquina.getReparaciones() != null) {
                    count += maquina.getReparaciones().stream()
                        .filter(r -> r.getFecha() != null && 
                                   !r.getFecha().isBefore(inicio) && 
                                   !r.getFecha().isAfter(fin))
                        .count();
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Error al contar reparaciones: " + e.getMessage());
        }
        return count;
    }
}
