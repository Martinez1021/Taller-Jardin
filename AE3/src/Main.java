import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String RUTA_ALUMNOS = "alumnos.xml";
    private static final String RUTA_ASIGNATURAS = "asignaturas.xml";
    private static final String RUTA_AUSENCIAS = "ausencias.xml";

    private static GestorXML xmlManager = new GestorXML();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            try {
                switch (opcion) {
                    case 1:
                        menuAlumnos();
                        break;
                    case 2:
                        menuAsignaturas();
                        break;
                    case 3:
                        menuAusencias();
                        break;
                    case 4:
                        registrarYMostrarAusencia();
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    private static void mostrarMenu() {

        System.out.println("1. Gestión de Alumnos");
        System.out.println("2. Gestión de Asignaturas");
        System.out.println("3. Gestión de Ausencias");
        System.out.println("4. Registrar Ausencia y Mostrar Notificación");
        System.out.println("0. Salir");
        System.out.print("\nSeleccione una opción: ");

    }

    // ========== MENÚ ALUMNOS ==========

    private static void menuAlumnos() throws Exception {

        System.out.println("1. Crear alumno");
        System.out.println("2. Listar alumnos");
        System.out.println("3. Actualizar alumno");
        System.out.println("4. Eliminar alumno");
        System.out.print("Opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                crearAlumno();
                break;
            case 2:
                listarAlumnos();
                break;
            case 3:
                actualizarAlumno();
                break;
            case 4:
                eliminarAlumno();
                break;
        }
    }

    private static void crearAlumno() throws Exception {
        System.out.print("ID del alumno: ");
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellidos: ");
        String apellidos = scanner.nextLine();
        System.out.print("Curso: ");
        String curso = scanner.nextLine();

        Alumno alumno = new Alumno(id, nombre, apellidos, curso);
        xmlManager.crearAlumno(RUTA_ALUMNOS, alumno);
        System.out.println("✓ Alumno creado correctamente");
    }

    private static void listarAlumnos() throws Exception {
        List<Alumno> alumnos = xmlManager.leerAlumnos(RUTA_ALUMNOS);
        System.out.println("\n========== LISTA DE ALUMNOS ==========");
        for (Alumno alumno : alumnos) {
            System.out.println(alumno);
        }
    }

    private static void actualizarAlumno() throws Exception {
        System.out.print("ID del alumno a actualizar: ");
        String id = scanner.nextLine();

        Alumno alumno = xmlManager.buscarAlumnoPorId(RUTA_ALUMNOS, id);
        if (alumno == null) {
            System.out.println("✗ Alumno no encontrado");
            return;
        }

        System.out.print("Nuevo nombre (actual: " + alumno.getNombre() + "): ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevos apellidos (actual: " + alumno.getApellidos() + "): ");
        String apellidos = scanner.nextLine();
        System.out.print("Nuevo curso (actual: " + alumno.getCurso() + "): ");
        String curso = scanner.nextLine();

        alumno.setNombre(nombre);
        alumno.setApellidos(apellidos);
        alumno.setCurso(curso);

        xmlManager.actualizarAlumno(RUTA_ALUMNOS, alumno);
        System.out.println("✓ Alumno actualizado correctamente");
    }

    private static void eliminarAlumno() throws Exception {
        System.out.print("ID del alumno a eliminar: ");
        String id = scanner.nextLine();

        xmlManager.eliminarAlumno(RUTA_ALUMNOS, id);
        System.out.println("✓ Alumno eliminado correctamente");
    }

    // ========== MENÚ ASIGNATURAS ==========

    private static void menuAsignaturas() throws Exception {
        System.out.println("\n--- GESTIÓN DE ASIGNATURAS ---");
        System.out.println("1. Crear asignatura");
        System.out.println("2. Listar asignaturas");
        System.out.println("3. Actualizar asignatura");
        System.out.println("4. Eliminar asignatura");
        System.out.print("Opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                crearAsignatura();
                break;
            case 2:
                listarAsignaturas();
                break;
            case 3:
                actualizarAsignatura();
                break;
            case 4:
                eliminarAsignatura();
                break;
        }
    }

    private static void crearAsignatura() throws Exception {
        System.out.print("ID de la asignatura: ");
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Curso: ");
        String curso = scanner.nextLine();
        System.out.print("Créditos: ");
        int creditos = scanner.nextInt();
        scanner.nextLine();

        Asignatura asignatura = new Asignatura(id, nombre, curso, creditos);
        xmlManager.crearAsignatura(RUTA_ASIGNATURAS, asignatura);
        System.out.println("✓ Asignatura creada correctamente");
    }

    private static void listarAsignaturas() throws Exception {
        List<Asignatura> asignaturas = xmlManager.leerAsignaturas(RUTA_ASIGNATURAS);
        System.out.println("\n========== LISTA DE ASIGNATURAS ==========");
        for (Asignatura asignatura : asignaturas) {
            System.out.println(asignatura);
        }
    }

    private static void actualizarAsignatura() throws Exception {
        System.out.print("ID de la asignatura a actualizar: ");
        String id = scanner.nextLine();

        Asignatura asignatura = xmlManager.buscarAsignaturaPorId(RUTA_ASIGNATURAS, id);
        if (asignatura == null) {
            System.out.println("✗ Asignatura no encontrada");
            return;
        }

        System.out.print("Nuevo nombre (actual: " + asignatura.getNombre() + "): ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo curso (actual: " + asignatura.getCurso() + "): ");
        String curso = scanner.nextLine();
        System.out.print("Nuevos créditos (actual: " + asignatura.getCreditos() + "): ");
        int creditos = scanner.nextInt();
        scanner.nextLine();

        asignatura.setNombre(nombre);
        asignatura.setCurso(curso);
        asignatura.setCreditos(creditos);

        xmlManager.actualizarAsignatura(RUTA_ASIGNATURAS, asignatura);
        System.out.println("✓ Asignatura actualizada correctamente");
    }

    private static void eliminarAsignatura() throws Exception {
        System.out.print("ID de la asignatura a eliminar: ");
        String id = scanner.nextLine();

        xmlManager.eliminarAsignatura(RUTA_ASIGNATURAS, id);
        System.out.println("✓ Asignatura eliminada correctamente");
    }

    // ========== MENÚ AUSENCIAS ==========

    private static void menuAusencias() throws Exception {

        System.out.println("1. Registrar ausencia");
        System.out.println("2. Listar ausencias");
        System.out.println("3. Listar ausencias por alumno");
        System.out.println("4. Actualizar ausencia");
        System.out.println("5. Eliminar ausencia");
        System.out.print("Opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                registrarAusencia();
                break;
            case 2:
                listarAusencias();
                break;
            case 3:
                listarAusenciasPorAlumno();
                break;
            case 4:
                actualizarAusencia();
                break;
            case 5:
                eliminarAusencia();
                break;
        }
    }

    private static void registrarAusencia() throws Exception {

        System.out.print("ID de la ausencia: ");
        String id = scanner.nextLine();
        System.out.print("ID del alumno: ");
        String alumnoId = scanner.nextLine();
        System.out.print("ID de la asignatura: ");
        String asignaturaId = scanner.nextLine();
        System.out.print("Tipo (falta/retraso/justificada): ");
        String tipo = scanner.nextLine();

        Ausencia ausencia = new Ausencia(id, alumnoId, asignaturaId,
                LocalDateTime.now(), tipo);
        xmlManager.crearAusencia(RUTA_AUSENCIAS, ausencia);
        System.out.println("✓ Ausencia registrada correctamente");
    }

    private static void listarAusencias() throws Exception {
        List<Ausencia> ausencias = xmlManager.leerAusencias(RUTA_AUSENCIAS);
        System.out.println("\n========== LISTA DE AUSENCIAS ==========");
        for (Ausencia ausencia : ausencias) {
            System.out.println(ausencia);
        }
    }

    private static void listarAusenciasPorAlumno() throws Exception {
        System.out.print("ID del alumno: ");
        String alumnoId = scanner.nextLine();

        List<Ausencia> ausencias = xmlManager.buscarAusenciasPorAlumno(RUTA_AUSENCIAS, alumnoId);
        System.out.println("\n========== AUSENCIAS DEL ALUMNO " + alumnoId + " ==========");
        for (Ausencia ausencia : ausencias) {
            System.out.println(ausencia);
        }
    }

    private static void actualizarAusencia() throws Exception {
        System.out.print("ID de la ausencia a actualizar: ");
        String id = scanner.nextLine();

        List<Ausencia> ausencias = xmlManager.leerAusencias(RUTA_AUSENCIAS);
        Ausencia ausencia = null;
        for (Ausencia a : ausencias) {
            if (a.getId().equals(id)) {
                ausencia = a;
                break;
            }
        }

        if (ausencia == null) {
            System.out.println("✗ Ausencia no encontrada");
            return;
        }

        System.out.print("Nuevo tipo (actual: " + ausencia.getTipo() + "): ");
        String tipo = scanner.nextLine();

        ausencia.setTipo(tipo);
        xmlManager.actualizarAusencia(RUTA_AUSENCIAS, ausencia);
        System.out.println("✓ Ausencia actualizada correctamente");
    }

    private static void eliminarAusencia() throws Exception {
        System.out.print("ID de la ausencia a eliminar: ");
        String id = scanner.nextLine();

        xmlManager.eliminarAusencia(RUTA_AUSENCIAS, id);
        System.out.println("✓ Ausencia eliminada correctamente");
    }

    // ========== REGISTRAR Y MOSTRAR NOTIFICACIÓN ==========

    private static void registrarYMostrarAusencia() throws Exception {
        System.out.println("\n--- REGISTRAR AUSENCIA CON NOTIFICACIÓN ---");

        System.out.print("ID de la ausencia: ");
        String id = scanner.nextLine();
        System.out.print("ID del alumno: ");
        String alumnoId = scanner.nextLine();
        System.out.print("ID de la asignatura: ");
        String asignaturaId = scanner.nextLine();
        System.out.print("Tipo (falta/retraso/justificada): ");
        String tipo = scanner.nextLine();

        // Buscar alumno y asignatura
        Alumno alumno = xmlManager.buscarAlumnoPorId(RUTA_ALUMNOS, alumnoId);
        Asignatura asignatura = xmlManager.buscarAsignaturaPorId(RUTA_ASIGNATURAS, asignaturaId);

        if (alumno == null) {
            System.out.println("✗ Alumno no encontrado");
            return;
        }
        if (asignatura == null) {
            System.out.println("✗ Asignatura no encontrada");
            return;
        }

        // Crear ausencia
        Ausencia ausencia = new Ausencia(id, alumnoId, asignaturaId,
                LocalDateTime.now(), tipo);
        xmlManager.crearAusencia(RUTA_AUSENCIAS, ausencia);

        // Mostrar notificación
        mostrarNotificacion(alumno, asignatura, ausencia);
    }

    private static void mostrarNotificacion(Alumno alumno, Asignatura asignatura, Ausencia ausencia) {

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("DATOS DEL ALUMNO:");
        System.out.println("────────────────────────────────────────────────────");
        System.out.println("Nombre: " + alumno.getNombre() + " " + alumno.getApellidos());
        System.out.println("Curso: " + alumno.getCurso());

        System.out.println("\nDETALLES DE LA AUSENCIA:");
        System.out.println("────────────────────────────────────────────────────");
        System.out.println("Asignatura: " + asignatura.getNombre());
        System.out.println("Fecha y hora: " + ausencia.getFecha().format(formatoFecha));
        System.out.println("Tipo: " + obtenerDescripcionTipo(ausencia.getTipo()));

        if (ausencia.getTipo().equals("falta")) {
            System.out.println("\n⚠ IMPORTANTE: Esta ausencia debe ser justificada.");
        }

        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("✓ Ausencia registrada exitosamente en el sistema");
        System.out.println("════════════════════════════════════════════════════════\n");
    }

    private static String obtenerDescripcionTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "falta":
                return "Falta de asistencia";
            case "retraso":
                return "Retraso";
            case "justificada":
                return "Ausencia justificada";
            default:
                return tipo;
        }
    }
}
