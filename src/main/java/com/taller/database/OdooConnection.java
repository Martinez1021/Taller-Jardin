package com.taller.database;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.URL;
import java.util.*;

/**
 * Clase Singleton para manejar la conexión a Odoo mediante XML-RPC
 * Sistema de facturación y órdenes de reparación
 */
public class OdooConnection {

    private static OdooConnection instance;
    private XmlRpcClient client;
    private Integer uid;

    // Configuración de conexión Odoo
    private static final String URL_BASE = "http://localhost:8069";
    private static final String DATABASE = "taller_jardin";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    /**
     * Constructor privado para implementar patrón Singleton
     */
    private OdooConnection() {
        try {
            // Autenticar con Odoo
            authenticate();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a Odoo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la instancia única de la conexión (patrón Singleton)
     * @return Instancia de OdooConnection
     */
    public static synchronized OdooConnection getInstance() {
        if (instance == null) {
            instance = new OdooConnection();
        }
        return instance;
    }

    /**
     * Autenticar con Odoo y obtener UID
     */
    private void authenticate() throws Exception {
        try {
            XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
            commonConfig.setServerURL(new URL(URL_BASE + "/xmlrpc/2/common"));

            XmlRpcClient commonClient = new XmlRpcClient();
            commonClient.setConfig(commonConfig);

            // Autenticar
            Object[] params = new Object[]{
                    DATABASE, USERNAME, PASSWORD, Collections.emptyMap()
            };

            uid = (Integer) commonClient.execute("authenticate", Arrays.asList(params));

            if (uid == null || uid == 0) {
                throw new Exception("Autenticación fallida. Verifica credenciales.");
            }

            // Configurar cliente para llamadas a métodos
            XmlRpcClientConfigImpl objectConfig = new XmlRpcClientConfigImpl();
            objectConfig.setServerURL(new URL(URL_BASE + "/xmlrpc/2/object"));

            client = new XmlRpcClient();
            client.setConfig(objectConfig);

        } catch (Exception e) {
            throw new Exception("Error en autenticación Odoo: " + e.getMessage(), e);
        }
    }

    /**
     * Ejecuta un método en un modelo de Odoo
     * @param modelo Nombre del modelo (ej: "res.partner")
     * @param metodo Nombre del método (ej: "search", "create", "write")
     * @param parametros Parámetros del método
     * @return Resultado de la ejecución
     */
    public Object executeKw(String modelo, String metodo, Object[] parametros) throws XmlRpcException {
        List<Object> params = Arrays.asList(
                DATABASE,
                uid,
                PASSWORD,
                modelo,
                metodo,
                parametros,
                new HashMap<>()
        );

        return client.execute("execute_kw", params);
    }

    /**
     * Ejecuta un método en un modelo de Odoo con opciones adicionales
     * @param modelo Nombre del modelo
     * @param metodo Nombre del método
     * @param parametros Parámetros del método
     * @param opciones Opciones adicionales (fields, limit, etc.)
     * @return Resultado de la ejecución
     */
    public Object executeKw(String modelo, String metodo, Object[] parametros, Map<String, Object> opciones) throws XmlRpcException {
        List<Object> params = Arrays.asList(
                DATABASE,
                uid,
                PASSWORD,
                modelo,
                metodo,
                parametros,
                opciones
        );

        return client.execute("execute_kw", params);
    }

    /**
     * Crea un cliente en Odoo
     * @param nombre Nombre del cliente
     * @param telefono Teléfono
     * @param email Email
     * @return ID del cliente creado
     */
    public Integer crearCliente(String nombre, String telefono, String email) throws XmlRpcException {
        Map<String, Object> clienteData = new HashMap<>();
        clienteData.put("name", nombre);
        clienteData.put("phone", telefono != null ? telefono : "");
        clienteData.put("email", email != null ? email : "");
        clienteData.put("customer_rank", 1); // Marcar como cliente

        Object[] params = new Object[]{Arrays.asList(clienteData)};
        return (Integer) executeKw("res.partner", "create", params);
    }

    /**
     * Busca un cliente por nombre
     * @param nombre Nombre del cliente
     * @return ID del cliente o null si no existe
     */
    public Integer buscarCliente(String nombre) throws XmlRpcException {
        Object[] searchParams = new Object[]{
                Arrays.asList(Arrays.asList("name", "=", nombre))
        };

        Map<String, Object> opciones = new HashMap<>();
        opciones.put("limit", 1);

        Object[] ids = (Object[]) executeKw("res.partner", "search", searchParams, opciones);

        if (ids.length > 0) {
            return (Integer) ids[0];
        }
        return null;
    }

    /**
     * Obtiene todos los clientes
     * @return Lista de mapas con datos de clientes
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerClientes() throws XmlRpcException {
        // Buscar todos los clientes
        Object[] searchParams = new Object[]{
                Arrays.asList(Arrays.asList("customer_rank", ">", 0))
        };

        Object[] ids = (Object[]) executeKw("res.partner", "search", searchParams);

        if (ids.length == 0) {
            return new ArrayList<>();
        }

        // Leer datos de clientes
        Object[] readParams = new Object[]{ids};
        Map<String, Object> opciones = new HashMap<>();
        opciones.put("fields", Arrays.asList("id", "name", "phone", "email"));

        Object[] clientesArray = (Object[]) executeKw("res.partner", "read", readParams, opciones);

        List<Map<String, Object>> clientes = new ArrayList<>();
        for (Object obj : clientesArray) {
            clientes.add((Map<String, Object>) obj);
        }

        return clientes;
    }

    /**
     * Crea una orden de reparación en Odoo
     * @param productoId ID del producto (máquina)
     * @param clienteId ID del cliente
     * @param descripcion Descripción del problema
     * @param presupuesto Presupuesto estimado
     * @return ID de la orden de reparación creada
     */
    public Integer crearOrdenReparacion(Integer productoId, Integer clienteId, String descripcion, Double presupuesto) throws XmlRpcException {
        Map<String, Object> ordenData = new HashMap<>();
        ordenData.put("product_id", productoId);
        ordenData.put("partner_id", clienteId);
        ordenData.put("operations", descripcion);

        if (presupuesto != null) {
            ordenData.put("quotation_notes", "Presupuesto estimado: €" + presupuesto);
        }

        Object[] params = new Object[]{Arrays.asList(ordenData)};
        return (Integer) executeKw("repair.order", "create", params);
    }

    /**
     * Crea un producto en Odoo (representa una máquina)
     * @param nombre Nombre del producto
     * @param tipo Tipo de máquina
     * @param numeroSerie Número de serie
     * @return ID del producto creado
     */
    public Integer crearProducto(String nombre, String tipo, String numeroSerie) throws XmlRpcException {
        Map<String, Object> productoData = new HashMap<>();
        productoData.put("name", nombre);
        productoData.put("type", "product");
        productoData.put("default_code", numeroSerie); // Referencia interna = número serie

        Object[] params = new Object[]{Arrays.asList(productoData)};
        return (Integer) executeKw("product.product", "create", params);
    }

    public String getDatabase() {
        return DATABASE;
    }

    public String getUsername() {
        return USERNAME;
    }

    public Integer getUid() {
        return uid;
    }
}

