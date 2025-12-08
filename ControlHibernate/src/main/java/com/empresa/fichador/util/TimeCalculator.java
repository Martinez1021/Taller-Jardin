package com.empresa.fichador.util;

import com.empresa.fichador.model.Fichaje;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TimeCalculator {

    public static double calcularHoras(LocalDateTime entrada, LocalDateTime salida) {
        if (entrada == null || salida == null) {
            return 0.0;
        }
        Duration duration = Duration.between(entrada, salida);
        return duration.toMinutes() / 60.0;
    }

    public static double calcularTotalHoras(List<Fichaje> fichajes) {
        double total = 0.0;
        for (Fichaje f : fichajes) {
            if (f.getHoraEntrada() != null && f.getHoraSalida() != null) {
                total += calcularHoras(f.getHoraEntrada(), f.getHoraSalida());
            }
        }
        return total;
    }

    public static String formatearHoras(double horasDecimales) {
        int horas = (int) horasDecimales;
        int minutos = (int) ((horasDecimales - horas) * 60);
        return String.format("%dh %02dm", horas, minutos);
    }

    public static boolean llegaTarde(LocalDateTime entrada) {
        if (entrada == null) return false;
        return entrada.toLocalTime().isAfter(LocalTime.of(8, 0));
    }
}

