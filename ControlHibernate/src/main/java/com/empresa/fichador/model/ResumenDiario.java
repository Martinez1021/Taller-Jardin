package com.empresa.fichador.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResumenDiario {
    private Long trabajadorId;
    private String nombreTrabajador;
    private LocalDate fecha;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private double totalHoras;
    private String clima;
    private Double temperatura;

    public ResumenDiario(Long trabajadorId, String nombreTrabajador, LocalDate fecha,
                         LocalDateTime horaEntrada, LocalDateTime horaSalida,
                         double totalHoras, String clima, Double temperatura) {
        this.trabajadorId = trabajadorId;
        this.nombreTrabajador = nombreTrabajador;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.totalHoras = totalHoras;
        this.clima = clima;
        this.temperatura = temperatura;
    }

    // Getters y Setters
    public Long getTrabajadorId() { return trabajadorId; }
    public void setTrabajadorId(Long trabajadorId) { this.trabajadorId = trabajadorId; }

    public String getNombreTrabajador() { return nombreTrabajador; }
    public void setNombreTrabajador(String nombreTrabajador) { this.nombreTrabajador = nombreTrabajador; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalDateTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    public double getTotalHoras() { return totalHoras; }
    public void setTotalHoras(double totalHoras) { this.totalHoras = totalHoras; }

    public String getClima() { return clima; }
    public void setClima(String clima) { this.clima = clima; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public String getHoraEntradaFormateada() {
        if (horaEntrada == null) return "-";
        return horaEntrada.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getHoraSalidaFormateada() {
        if (horaSalida == null) return "-";
        return horaSalida.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getTotalHorasFormateado() {
        int horas = (int) totalHoras;
        int minutos = (int) ((totalHoras - horas) * 60);
        return String.format("%dh %02dm", horas, minutos);
    }

    public String getTemperaturaFormateada() {
        if (temperatura == null) return "-";
        return String.format("%.1f°C", temperatura);
    }
}
