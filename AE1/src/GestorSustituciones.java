import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GestorSustituciones {

    private static final String RUTA_BASE = "C:\\ejemplos";
    private static final String NOMBRE_ARCHIVO_CONTROL = "sustituciones_control.csv";
    private static final String RUTA_CONTROL = Paths.get(RUTA_BASE, NOMBRE_ARCHIVO_CONTROL).toString();
    private static final String ENCABEZADO_CONTROL = "Profesor Sustituto,Fecha/Hora,Total Acumulado\n";

    // Listas para días y horas, más fácil de entender
    private static final List<String> DIAS = Arrays.asList("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES");
    private static final List<String> HORAS = Arrays.asList("1ª", "2ª", "3ª", "4ª", "5ª", "6ª");

    public boolean existeProfesor(String nombreProfesor) {
        String archivoNombre = nombreProfesor.toLowerCase() + ".csv";
        String rutaArchivo = Paths.get(RUTA_BASE, archivoNombre).toString();
        return Files.exists(Paths.get(rutaArchivo));
    }

    // Cambiado: ahora devuelve una lista de objetos Profesor
    public List<Profesor> obtenerSustituciones() {
        List<Profesor> totales = new ArrayList<>();
        if (!Files.exists(Paths.get(RUTA_CONTROL))) {
            return totales;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(RUTA_CONTROL))) {
            lector.readLine();
            String linea;
            Set<String> nombresProcesados = new HashSet<>();
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    String nombre = datos[0].trim();
                    if (nombresProcesados.contains(nombre)) continue;
                    try {
                        int total = Integer.parseInt(datos[2].trim());
                        totales.add(new Profesor(formatearNombre(nombre), total));
                        nombresProcesados.add(nombre);
                    } catch (NumberFormatException e) {
                        // Saltamos la línea si el número es inválido
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("¡Alerta! Fallo al cargar el historial: " + e.getMessage());
        }
        return totales;
    }

    public List<Profesor> generarSustitutos(String profFalta, String dia, String hora) {
        String diaUpper = dia.toUpperCase();
        String horaUpper = hora.toUpperCase();

        int indiceDia = DIAS.indexOf(diaUpper);
        int indiceHora = HORAS.indexOf(horaUpper);

        if (indiceDia == -1 || indiceHora == -1) {
            return Collections.emptyList();
        }

        List<Profesor> acumulados = obtenerSustituciones();
        List<Profesor> listaCandidatos = new ArrayList<>();
        String profFaltaFormat = formatearNombre(profFalta);

        File carpeta = new File(RUTA_BASE);
        File[] archivosHorario = carpeta.listFiles((dir, nombre) ->
                nombre.toLowerCase().endsWith(".csv") && !nombre.equals(NOMBRE_ARCHIVO_CONTROL));

        if (archivosHorario == null || archivosHorario.length == 0) {
            System.err.println("❌ Error de configuración: No se encontraron archivos de horarios en la ruta (" + RUTA_BASE + ")");
            return Collections.emptyList();
        }

        for (File archivo : archivosHorario) {
            String nombreArchivo = archivo.getName().replace(".csv", "");
            String nombreProfesor = formatearNombre(nombreArchivo);

            if (nombreProfesor.equals(profFaltaFormat)) {
                continue;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                int filaActual = 0;

                while ((linea = br.readLine()) != null && filaActual <= indiceDia) {
                    if (filaActual == indiceDia) {
                        String[] columnas = linea.split(",");

                        if (columnas.length > indiceHora) {
                            String actividad = columnas[indiceHora].trim().toUpperCase();

                            if (actividad.equals("LIBRE") || actividad.equals("GUARDIA") || actividad.contains("ED") || actividad.contains("SX")) {
                                int total = 0;
                                for (Profesor p : acumulados) {
                                    if (p.getNombre().equals(nombreProfesor)) {
                                        total = p.getNumSustituciones();
                                        break;
                                    }
                                }
                                listaCandidatos.add(new Profesor(nombreProfesor, total));
                            }
                        }
                        break;
                    }
                    filaActual++;
                }

            } catch (IOException e) {
                System.err.println("Problema al leer el horario de " + nombreProfesor + ". Ignorando: " + e.getMessage());
            }
        }

        Collections.sort(listaCandidatos);
        return listaCandidatos;
    }

    public void registrarSustitucion(String nombreSustituto, String diaAusente, String horaAusente) {
        String nombre = formatearNombre(nombreSustituto);
        List<Profesor> acumulados = obtenerSustituciones();
        int nuevoTotal = 1;
        for (Profesor p : acumulados) {
            if (p.getNombre().equals(nombre)) {
                nuevoTotal = p.getNumSustituciones() + 1;
                break;
            }
        }

        String momento = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + " (" + formatearNombre(diaAusente) + " " + horaAusente + ")";

        String registroNuevo = String.format("%s,%s,%d\n", nombre, momento, nuevoTotal);
        boolean existeArchivo = Files.exists(Paths.get(RUTA_CONTROL));

        try (PrintWriter escritor = new PrintWriter(new FileWriter(RUTA_CONTROL, true))) {
            if (!existeArchivo) {
                escritor.write(ENCABEZADO_CONTROL);
            }
            escritor.write(registroNuevo);
            System.out.println("🎉 ¡Éxito! Sustitución GUARDADA para " + nombre + ".");
            System.out.println("📝 Total de servicios acumulados: " + nuevoTotal);
        } catch (IOException e) {
            System.err.println("Error grave: No se pudo escribir en el archivo de control: " + e.getMessage());
        }
    }

    public void mostrarRanking() {
        List<Profesor> acumulados = obtenerSustituciones();

        if (acumulados.isEmpty()) {
            System.out.println("El historial está vacío. No hay servicios de guardia registrados.");
            return;
        }

        acumulados.sort(Comparator.comparing(Profesor::getNumSustituciones).reversed());

        System.out.println("\n=== 🏆 Top Profesores con Más Carga de Sustituciones ===");
        System.out.println("   (De mayor a menor cantidad de servicios)");
        for (int i = 0; i < acumulados.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, acumulados.get(i));
        }
        System.out.println("=======================================================\n");
    }

    public void consultarProfesor(String nombreProfesor) {
        String nombre = formatearNombre(nombreProfesor);
        List<Profesor> acumulados = obtenerSustituciones();
        int total = 0;
        for (Profesor p : acumulados) {
            if (p.getNombre().equals(nombre)) {
                total = p.getNumSustituciones();
                break;
            }
        }

        System.out.println("\n--- Resumen de Sustituciones ---");
        System.out.printf("El profesor %s ha cubierto un total de %d clases.\n", nombre, total);
        System.out.println("--------------------------------\n");
    }

    public String formatearNombre(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}