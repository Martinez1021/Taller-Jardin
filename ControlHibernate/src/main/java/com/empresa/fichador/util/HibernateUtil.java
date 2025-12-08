package com.empresa.fichador.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static boolean connectionError = false;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
            System.out.println("✓ Hibernate SessionFactory creado correctamente");
        } catch (Throwable ex) {
            System.err.println("✗ Error creando SessionFactory: " + ex.getMessage());
            System.err.println("La aplicación funcionará en modo demo sin base de datos.");
            connectionError = true;
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static boolean hasConnectionError() {
        return connectionError || sessionFactory == null;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

