package com.empresa.fichador.util;

import com.empresa.fichador.dao.*;
import com.empresa.fichador.model.*;

import java.time.LocalTime;
import java.util.List;

public class DataInitializer {

    public static void inicializarDatos() {
        if (HibernateUtil.hasConnectionError()) {
            System.out.println("⚠️ Sin conexión a BD - Modo demo activado");
            return;
        }

        try {
            TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
            List<Trabajador> trabajadores = trabajadorDAO.findAll();

            if (trabajadores.isEmpty()) {
                System.out.println("Inicializando datos de ejemplo...");

                // Crear departamentos
                DepartamentoDAO departamentoDAO = new DepartamentoDAO();
                Departamento deptIT = new Departamento("Informática", "Departamento de TI");
                Departamento deptRRHH = new Departamento("Recursos Humanos", "Departamento de RRHH");
                Departamento deptAdmin = new Departamento("Administración", "Departamento Administrativo");

                departamentoDAO.save(deptIT);
                departamentoDAO.save(deptRRHH);
                departamentoDAO.save(deptAdmin);

                // Crear horarios
                HorarioDAO horarioDAO = new HorarioDAO();
                Horario horarioNormal = new Horario("Jornada Completa", LocalTime.of(8, 0), LocalTime.of(17, 0));
                Horario horarioManana = new Horario("Jornada Mañana", LocalTime.of(7, 0), LocalTime.of(14, 0));

                horarioDAO.save(horarioNormal);
                horarioDAO.save(horarioManana);

                // Crear trabajadores de ejemplo
                String[][] datosEmpleados = {
                    {"Juan", "García López", "1001", "1234", "Desarrollador"},
                    {"María", "Martínez Ruiz", "1002", "1234", "Analista"},
                    {"Carlos", "Rodríguez Pérez", "1003", "1234", "Técnico"},
                    {"Ana", "López García", "1004", "1234", "Diseñadora"},
                    {"Pedro", "Sánchez Martín", "1005", "1234", "Project Manager"},
                    {"Laura", "Fernández Gil", "1006", "1234", "QA Tester"},
                    {"Miguel", "González Díaz", "1007", "1234", "DevOps"},
                    {"Elena", "Torres Ruiz", "1008", "1234", "Frontend"},
                    {"David", "Ramírez López", "1009", "1234", "Backend"},
                    {"Sara", "Jiménez Castro", "1010", "1234", "UX Designer"}
                };

                for (String[] datos : datosEmpleados) {
                    Trabajador t = new Trabajador(datos[0], datos[1], datos[2], datos[3]);
                    t.setCargo(datos[4]);
                    t.setDepartamento(deptIT);
                    t.setHorario(horarioNormal);
                    t.setEmail(datos[0].toLowerCase() + "@empresa.com");
                    trabajadorDAO.save(t);
                }

                // Crear usuario admin en BD
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario admin = new Usuario("admin", "admin123", "Administrador", "Sistema", Usuario.Rol.ADMIN);
                usuarioDAO.save(admin);

                System.out.println("✓ Datos de ejemplo creados correctamente");
            }
        } catch (Exception e) {
            System.err.println("Error inicializando datos: " + e.getMessage());
        }
    }
}

