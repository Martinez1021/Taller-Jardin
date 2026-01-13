package com.taller.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;

/**
 * Clase Singleton para manejar la conexión a MongoDB
 * Base de datos de historial técnico y garantías
 */
public class MongoDBConnection {

    private static MongoDBConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    // Configuración de conexión MongoDB
    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DATABASE_NAME = "taller_db";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";
    private static final String AUTH_DATABASE = "admin";

    /**
     * Constructor privado para implementar patrón Singleton
     */
    private MongoDBConnection() {
        try {
            // Crear credenciales de autenticación
            MongoCredential credential = MongoCredential.createCredential(
                    USERNAME,
                    AUTH_DATABASE,
                    PASSWORD.toCharArray()
            );

            // Configurar servidor
            ServerAddress serverAddress = new ServerAddress(HOST, PORT);

            // Configurar cliente MongoDB
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder ->
                            builder.hosts(Collections.singletonList(serverAddress)))
                    .credential(credential)
                    .build();

            // Crear cliente y obtener base de datos
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);

            // Verificar conexión
            database.listCollectionNames().first();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a MongoDB: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la instancia única de la conexión (patrón Singleton)
     * @return Instancia de MongoDBConnection
     */
    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    /**
     * Obtiene la base de datos
     * @return MongoDatabase
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Cierra la conexión a MongoDB
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

