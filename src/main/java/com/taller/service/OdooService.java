package com.taller.service;

import com.taller.database.OdooConnection;
import com.taller.model.Cliente;
import com.taller.model.Maquina;
import com.taller.model.OrdenReparacion;
import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio para operaciones con Odoo (facturación y órdenes)
 */
public class OdooService {

    private OdooConnection odooConnection;

    public OdooService() {
        try {
            this.odooConnection = OdooConnection.getInstance();
        } catch (Exception e) {
            System.err.println("⚠ Odoo no disponible: " + e.getMessage());
        }
    }

    /**
     * Verifica si Odoo está disponible
     */
    public boolean isOdooDisponible() {
        return odooConnection != null;
    }

    /**
     * Sincroniza un cliente: lo busca en Odoo o lo crea
     */
    public Integer sincronizarCliente(Cliente cliente) {
        if (!isOdooDisponible()) {
            System.err.println("⚠ No se puede sincronizar cliente: Odoo no disponible");
            return null;
        }

        try {
            // Primero buscar si existe
            Integer clienteId = odooConnection.buscarCliente(cliente.getNombre());

            if (clienteId != null) {
                System.out.println("✓ Cliente encontrado en Odoo: " + cliente.getNombre() + " (ID: " + clienteId + ")");
                return clienteId;
            }

            // Si no existe, crear
            clienteId = odooConnection.crearCliente(
                    cliente.getNombre(),
                    cliente.getTelefono(),
                    cliente.getEmail()
            );

            System.out.println("✓ Cliente creado en Odoo: " + cliente.getNombre() + " (ID: " + clienteId + ")");
            return clienteId;

        } catch (XmlRpcException e) {
            System.err.println("✗ Error al sincronizar cliente: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene todos los clientes de Odoo
     */
    public List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();

        if (!isOdooDisponible()) {
            return clientes;
        }

        try {
            List<Map<String, Object>> clientesOdoo = odooConnection.obtenerClientes();

            for (Map<String, Object> data : clientesOdoo) {
                Cliente cliente = new Cliente();
                cliente.setOdooId((Integer) data.get("id"));
                cliente.setNombre((String) data.get("name"));

                Object phone = data.get("phone");
                cliente.setTelefono(phone != null && !(phone instanceof Boolean) ? phone.toString() : "");

                Object email = data.get("email");
                cliente.setEmail(email != null && !(email instanceof Boolean) ? email.toString() : "");

                clientes.add(cliente);
            }

        } catch (Exception e) {
            System.err.println("✗ Error al obtener clientes: " + e.getMessage());
        }

        return clientes;
    }

    /**
     * Crea un producto en Odoo para una máquina
     */
    public Integer crearProductoMaquina(Maquina maquina) {
        if (!isOdooDisponible()) {
            return null;
        }

        try {
            String nombreProducto = String.format("%s %s %s",
                    maquina.getTipo(),
                    maquina.getMarca(),
                    maquina.getModelo()
            );

            Integer productoId = odooConnection.crearProducto(
                    nombreProducto,
                    maquina.getTipo(),
                    maquina.getNumeroSerie()
            );

            System.out.println("✓ Producto creado en Odoo: " + nombreProducto + " (ID: " + productoId + ")");
            return productoId;

        } catch (XmlRpcException e) {
            System.err.println("✗ Error al crear producto: " + e.getMessage());
            return null;
        }
    }

    /**
     * Crea una orden de reparación en Odoo
     */
    public Integer crearReparacionEnOdoo(Maquina maquina, OrdenReparacion orden) {
        if (!isOdooDisponible()) {
            System.err.println("⚠ No se puede crear reparación: Odoo no disponible");
            return null;
        }

        try {
            // Verificar que la máquina tenga producto en Odoo
            Integer productoId = maquina.getOdooProductId();
            if (productoId == null) {
                System.out.println("⚠ La máquina no tiene producto en Odoo, creándolo...");
                productoId = crearProductoMaquina(maquina);
                if (productoId == null) {
                    throw new Exception("No se pudo crear el producto en Odoo");
                }
                maquina.setOdooProductId(productoId);
            }

            // Verificar que el cliente exista en Odoo
            Integer clienteId = maquina.getClienteId();
            if (clienteId == null) {
                throw new Exception("La máquina no tiene cliente asignado en Odoo");
            }

            // Crear orden de reparación
            Integer repairId = odooConnection.crearOrdenReparacion(
                    productoId,
                    clienteId,
                    orden.getDescripcionProblema(),
                    orden.getPresupuestoEstimado()
            );

            System.out.println("✓ Orden de reparación creada en Odoo (ID: " + repairId + ")");
            return repairId;

        } catch (Exception e) {
            System.err.println("✗ Error al crear orden de reparación: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica el nombre de un cliente y lo sincroniza si es necesario
     */
    public Integer verificarCliente(String nombreCliente, String telefono, String email) {
        if (!isOdooDisponible()) {
            return null;
        }

        Cliente cliente = new Cliente(nombreCliente, telefono, email);
        return sincronizarCliente(cliente);
    }
}

