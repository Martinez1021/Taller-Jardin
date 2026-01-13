package com.empresa.fichador.dao;

import com.empresa.fichador.model.Fichaje;
import com.empresa.fichador.model.Trabajador;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FichajeDAO extends GenericDAO<Fichaje> {

    public FichajeDAO() {
        super(Fichaje.class);
    }

    public List<Fichaje> findByFecha(LocalDate fecha) {
        try (Session session = getSession()) {
            Query<Fichaje> query = session.createQuery(
                    "FROM Fichaje WHERE fecha = :fecha ORDER BY id DESC", Fichaje.class);
            query.setParameter("fecha", fecha);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Fichaje> findByTrabajador(Trabajador trabajador) {
        try (Session session = getSession()) {
            Query<Fichaje> query = session.createQuery(
                    "FROM Fichaje WHERE trabajador = :trabajador ORDER BY fecha DESC, id DESC", Fichaje.class);
            query.setParameter("trabajador", trabajador);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Fichaje> findByTrabajadorAndFecha(Trabajador trabajador, LocalDate fecha) {
        try (Session session = getSession()) {
            Query<Fichaje> query = session.createQuery(
                    "FROM Fichaje WHERE trabajador = :trabajador AND fecha = :fecha ORDER BY id ASC", Fichaje.class);
            query.setParameter("trabajador", trabajador);
            query.setParameter("fecha", fecha);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Fichaje findUltimoFichaje(Trabajador trabajador) {
        try (Session session = getSession()) {
            Query<Fichaje> query = session.createQuery(
                    "FROM Fichaje WHERE trabajador = :trabajador ORDER BY fecha DESC, id DESC", Fichaje.class);
            query.setParameter("trabajador", trabajador);
            query.setMaxResults(1);
            List<Fichaje> resultados = query.list();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double calcularMediaHoras(LocalDate fechaInicio, LocalDate fechaFin) {
        try (Session session = getSession()) {
            // Simplemente retornar un valor por defecto
            return 8.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}

