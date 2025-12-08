package com.empresa.fichador.service;

import com.empresa.fichador.model.Trabajador;
import com.empresa.fichador.model.Usuario;

/**
 * Servicio singleton para mantener la sesión del usuario actual
 */
public class SessionService {

    private static SessionService instance;

    private Usuario usuarioActual;
    private Trabajador trabajadorActual;

    private SessionService() {}

    public static SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        if (usuario != null && usuario.getTrabajadorAsociado() != null) {
            this.trabajadorActual = usuario.getTrabajadorAsociado();
        }
    }

    public void iniciarSesion(Usuario usuario, Trabajador trabajador) {
        this.usuarioActual = usuario;
        this.trabajadorActual = trabajador;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
        this.trabajadorActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Trabajador getTrabajadorActual() {
        return trabajadorActual;
    }

    public void setTrabajadorActual(Trabajador trabajador) {
        this.trabajadorActual = trabajador;
    }

    public boolean isLoggedIn() {
        return usuarioActual != null || trabajadorActual != null;
    }

    public boolean isAdmin() {
        return usuarioActual != null && usuarioActual.isAdmin();
    }

    public String getNombreUsuario() {
        if (usuarioActual != null) {
            return usuarioActual.getNombreCompleto();
        } else if (trabajadorActual != null) {
            return trabajadorActual.getNombreCompleto();
        }
        return "Administrador";
    }

    public String getRolDescripcion() {
        if (usuarioActual != null && usuarioActual.getRol() != null) {
            return usuarioActual.getRol().getDescripcion();
        } else if (trabajadorActual != null) {
            return "Trabajador";
        }
        return "Administrador";
    }
}

