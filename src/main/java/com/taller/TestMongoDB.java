package com.taller;

import com.taller.service.MaquinaService;
import com.taller.model.Maquina;

import java.util.List;

public class TestMongoDB {
    public static void main(String[] args) {
        System.out.println("=== TEST CONEXIÓN MONGODB ===\n");

        try {
            MaquinaService service = new MaquinaService();
            List<Maquina> maquinas = service.obtenerTodas();

            System.out.println("✓ Conectado a MongoDB");
            System.out.println("📦 Total de máquinas: " + maquinas.size());

            if (maquinas.isEmpty()) {
                System.out.println("\n⚠️ No se encontraron máquinas en la base de datos");
            } else {
                System.out.println("\n📋 Máquinas encontradas:");
                for (int i = 0; i < maquinas.size(); i++) {
                    Maquina m = maquinas.get(i);
                    System.out.println(String.format("  %d. %s - %s %s %s (%d reparaciones)",
                        i + 1, m.getNumeroSerie(), m.getTipo(), m.getMarca(), m.getModelo(),
                        m.getReparaciones().size()));
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

