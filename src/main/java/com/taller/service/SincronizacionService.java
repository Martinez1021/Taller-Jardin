package com.taller.service;

import com.taller.model.Maquina;
import com.taller.model.OrdenReparacion;
import com.taller.model.Reparacion;

import java.time.LocalDate;

/**
 * Servicio para sincronización bidireccional entre MongoDB y Odoo
 */
public class SincronizacionService {

    private MaquinaService maquinaService;
    private OdooService odooService;

    public SincronizacionService() {
        this.maquinaService = new MaquinaService();
        this.odooService = new OdooService();
    }

    /**
     * Sincroniza todo: verifica que las máquinas tengan sus clientes y productos en Odoo
     */
    public void sincronizarTodo() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  INICIANDO SINCRONIZACIÓN MONGODB ↔ ODOO");
        System.out.println("═══════════════════════════════════════════\n");

        if (!odooService.isOdooDisponible()) {
            System.err.println("⚠ Odoo no disponible. Sincronización cancelada.");
            return;
        }

        try {
            int maquinasSincronizadas = 0;
            int errores = 0;

            for (Maquina maquina : maquinaService.obtenerTodas()) {
                try {
                    boolean cambios = false;

                    // Verificar cliente en Odoo
                    if (maquina.getClienteId() == null && maquina.getClienteNombre() != null) {
                        Integer clienteId = odooService.verificarCliente(
                                maquina.getClienteNombre(),
                                null,
                                null
                        );

                        if (clienteId != null) {
                            maquina.setClienteId(clienteId);
                            cambios = true;
                            System.out.println("  ✓ Cliente sincronizado para máquina: " + maquina.getNumeroSerie());
                        }
                    }

                    // Verificar producto en Odoo
                    if (maquina.getOdooProductId() == null) {
                        Integer productoId = odooService.crearProductoMaquina(maquina);

                        if (productoId != null) {
                            maquina.setOdooProductId(productoId);
                            cambios = true;
                            System.out.println("  ✓ Producto creado para máquina: " + maquina.getNumeroSerie());
                        }
                    }

                    // Actualizar en MongoDB si hubo cambios
                    if (cambios) {
                        maquinaService.actualizar(maquina);
                        maquinasSincronizadas++;
                    }

                } catch (Exception e) {
                    System.err.println("  ✗ Error al sincronizar máquina " + maquina.getNumeroSerie() + ": " + e.getMessage());
                    errores++;
                }
            }

            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("  SINCRONIZACIÓN COMPLETADA");
            System.out.println("  Máquinas sincronizadas: " + maquinasSincronizadas);
            System.out.println("  Errores: " + errores);
            System.out.println("═══════════════════════════════════════════\n");

        } catch (Exception e) {
            System.err.println("✗ Error en sincronización: " + e.getMessage());
        }
    }

    /**
     * Crea una orden de reparación completa (MongoDB + Odoo)
     */
    public boolean crearOrdenReparacionCompleta(OrdenReparacion orden) {
        try {
            // Buscar máquina en MongoDB
            Maquina maquina = maquinaService.buscarPorNumeroSerie(orden.getNumeroSerieMaquina());

            if (maquina == null) {
                throw new Exception("No se encontró la máquina con número de serie: " + orden.getNumeroSerieMaquina());
            }

            // Verificar que tenga cliente y producto en Odoo
            if (maquina.getClienteId() == null) {
                Integer clienteId = odooService.verificarCliente(maquina.getClienteNombre(), null, null);
                if (clienteId != null) {
                    maquina.setClienteId(clienteId);
                    maquinaService.actualizar(maquina);
                } else {
                    throw new Exception("No se pudo sincronizar el cliente en Odoo");
                }
            }

            if (maquina.getOdooProductId() == null) {
                Integer productoId = odooService.crearProductoMaquina(maquina);
                if (productoId != null) {
                    maquina.setOdooProductId(productoId);
                    maquinaService.actualizar(maquina);
                } else {
                    throw new Exception("No se pudo crear el producto en Odoo");
                }
            }

            // Crear orden en Odoo
            Integer odooRepairId = odooService.crearReparacionEnOdoo(maquina, orden);

            if (odooRepairId == null && odooService.isOdooDisponible()) {
                throw new Exception("No se pudo crear la orden en Odoo");
            }

            // Añadir reparación a MongoDB
            Reparacion reparacion = new Reparacion();
            reparacion.setFecha(LocalDate.now());
            reparacion.setDescripcion(orden.getDescripcionProblema());
            reparacion.setTecnico(orden.getTecnicoAsignado());
            reparacion.setCoste(orden.getPresupuestoEstimado());

            maquinaService.añadirReparacion(
                    orden.getNumeroSerieMaquina(),
                    reparacion,
                    odooRepairId
            );

            System.out.println("\n✓ Orden de reparación creada exitosamente en ambos sistemas");
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error al crear orden de reparación: " + e.getMessage());
            return false;
        }
    }
}

