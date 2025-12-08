package com.empresa.fichador.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fichajes")
public class Fichaje {

    public enum TipoFichaje {
        ENTRADA, SALIDA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fichaje", nullable = false)
    private TipoFichaje tipoFichaje;

    @Column(name = "hora_entrada")
    private LocalDateTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    private String clima;
    private Double temperatura;

    @Column(length = 500)
    private String observaciones;

    public Fichaje() {
        this.fecha = LocalDate.now();
    }

    public Fichaje(Trabajador trabajador, TipoFichaje tipoFichaje) {
        this.trabajador = trabajador;
        this.tipoFichaje = tipoFichaje;
        this.fecha = LocalDate.now();
        if (tipoFichaje == TipoFichaje.ENTRADA) {
            this.horaEntrada = LocalDateTime.now();
        } else {
            this.horaSalida = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trabajador getTrabajador() { return trabajador; }
    public void setTrabajador(Trabajador trabajador) { this.trabajador = trabajador; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public TipoFichaje getTipoFichaje() { return tipoFichaje; }
    public void setTipoFichaje(TipoFichaje tipoFichaje) { this.tipoFichaje = tipoFichaje; }

    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalDateTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    public String getClima() { return clima; }
    public void setClima(String clima) { this.clima = clima; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}

