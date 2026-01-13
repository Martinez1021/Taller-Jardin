package com.empresa.fichador.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trabajadores")
public class Trabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(name = "numero_tarjeta", unique = true, nullable = false)
    private String numeroTarjeta;

    @Column(nullable = false)
    private String pin;

    @Column
    private String email;

    @Column
    private String telefono;

    @Column
    private String dni;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column
    private String cargo;

    @Column
    private String direccion;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column
    private String ciudad;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "activo")
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "horario_id")
    private Horario horario;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL)
    private List<Fichaje> fichajes;

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL)
    private List<Incidencia> incidencias;

    // Constructores
    public Trabajador() {
        this.fechaAlta = LocalDate.now();
        this.activo = true;
    }

    public Trabajador(String nombre, String apellidos, String numeroTarjeta, String pin) {
        this();
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.numeroTarjeta = numeroTarjeta;
        this.pin = pin;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public Horario getHorario() { return horario; }
    public void setHorario(Horario horario) { this.horario = horario; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Fichaje> getFichajes() { return fichajes; }
    public void setFichajes(List<Fichaje> fichajes) { this.fichajes = fichajes; }

    public List<Incidencia> getIncidencias() { return incidencias; }
    public void setIncidencias(List<Incidencia> incidencias) { this.incidencias = incidencias; }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    public String getIniciales() {
        String inicialNombre = nombre != null && !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "";
        String inicialApellido = apellidos != null && !apellidos.isEmpty() ? apellidos.substring(0, 1).toUpperCase() : "";
        return inicialNombre + inicialApellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}

