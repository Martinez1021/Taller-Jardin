package com.empresa.fichador.service;

import com.empresa.fichador.dao.FichajeDAO;
import com.empresa.fichador.model.Fichaje;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasService {

    private FichajeDAO fichajeDAO;

    public EstadisticasService() {
        this.fichajeDAO = new FichajeDAO();
    }

    public Map<String, Long> obtenerPuntualidad(LocalDate fecha) {
        Map<String, Long> resultado = new LinkedHashMap<>();

        try {
            List<Fichaje> fichajes = fichajeDAO.findByFecha(fecha);

            long puntuales = fichajes.stream()
                    .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                    .filter(f -> f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                    .count();

            long tarde = fichajes.stream()
                    .filter(f -> f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null)
                    .filter(f -> !f.getHoraEntrada().toLocalTime().isBefore(LocalTime.of(8, 15)))
                    .count();

            resultado.put("Puntuales", puntuales);
            resultado.put("Tarde", tarde);

        } catch (Exception e) {
            resultado.put("Puntuales", 0L);
            resultado.put("Tarde", 0L);
        }

        return resultado;
    }

    public Map<String, Double> obtenerMediaHorasUltimos7Dias() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            String fechaStr = fecha.format(formatter);

            try {
                List<Fichaje> fichajes = fichajeDAO.findByFecha(fecha);
                double horasTotales = 0;
                int trabajadores = 0;

                // Calcular horas por trabajador
                java.util.Map<Long, java.time.LocalDateTime> entradas = new java.util.HashMap<>();

                for (Fichaje f : fichajes) {
                    if (f.getTipoFichaje() == Fichaje.TipoFichaje.ENTRADA && f.getHoraEntrada() != null) {
                        entradas.put(f.getTrabajador().getId(), f.getHoraEntrada());
                    }
                }

                for (Fichaje f : fichajes) {
                    if (f.getTipoFichaje() == Fichaje.TipoFichaje.SALIDA && f.getHoraSalida() != null) {
                        java.time.LocalDateTime entrada = entradas.get(f.getTrabajador().getId());
                        if (entrada != null) {
                            horasTotales += java.time.Duration.between(entrada, f.getHoraSalida()).toMinutes() / 60.0;
                            trabajadores++;
                        }
                    }
                }

                double media = trabajadores > 0 ? horasTotales / trabajadores : 0;
                resultado.put(fechaStr, media);

            } catch (Exception e) {
                resultado.put(fechaStr, 0.0);
            }
        }

        return resultado;
    }
}

