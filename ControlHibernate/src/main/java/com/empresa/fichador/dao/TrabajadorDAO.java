package com.empresa.fichador.dao;

import com.empresa.fichador.model.Trabajador;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class TrabajadorDAO extends GenericDAO<Trabajador> {

    public TrabajadorDAO() {
        super(Trabajador.class);
    }

    public Trabajador findByTarjetaAndPin(String numeroTarjeta, String pin) {
        try (Session session = getSession()) {
            Query<Trabajador> query = session.createQuery(
                    "FROM Trabajador WHERE numeroTarjeta = :tarjeta AND pin = :pin",
                    Trabajador.class
            );
            query.setParameter("tarjeta", numeroTarjeta);
            query.setParameter("pin", pin);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existeTarjeta(String numeroTarjeta) {
        try (Session session = getSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(t) FROM Trabajador t WHERE t.numeroTarjeta = :tarjeta",
                    Long.class
            );
            query.setParameter("tarjeta", numeroTarjeta);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

