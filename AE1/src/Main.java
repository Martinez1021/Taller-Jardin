import java.util.List;
import java.util.Scanner;

public class Main {
    private static final GestorSustituciones GESTOR = new GestorSustituciones();
    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        iniciarPrograma();
    }

    private static void iniciarPrograma() {
        int eleccion;
        do {
            System.out.println("\n~~~ SISTEMA DE GESTIÓN DE CUBRIMIENTOS DE GUARDIA ~~~");
            System.out.println("1. Registrar una Ausencia y Asignar Sustituto");
            System.out.println("2. Consultar Historial Individual de un Profesor");
            System.out.println("3. Ver Clasificación General (Ranking)");
            System.out.println("0. Cerrar el Sistema");
            System.out.print("Introduce tu elección (0-3): ");

            try {
                eleccion = Integer.parseInt(ENTRADA.nextLine().trim());
            } catch (NumberFormatException e) {
                eleccion = -1;
            }

            switch (eleccion) {
                case 0:
                    System.out.println("Sistema apagado. ¡Nos vemos pronto! 👋");
                    break;
                case 1:
                    manejarAsignacion();
                    break;
                case 2:
                    consultarCarga();
                    break;
                case 3:
                    GESTOR.mostrarRanking();
                    break;
                default:
                    System.out.println("Opción desconocida. Por favor, selecciona un número válido.");
            }
        } while(eleccion != 0);

    }

    private static void manejarAsignacion() {
        System.out.print("\nIdentifica al profesor ausente: ");
        String nombreAusente = ENTRADA.nextLine().trim();
        String nombreAusenteFormateado = GESTOR.formatearNombre(nombreAusente);

        if (!GESTOR.existeProfesor(nombreAusente)) {
            System.out.println("❌ Aviso: No encontramos el archivo de horario para '" + nombreAusenteFormateado + "'. Revisa si existe '"+ nombreAusente.toLowerCase() + ".csv' en C:\\ejemplos.");
        } else {
            System.out.print("¿Qué día de la semana necesita cobertura? (Ej: Jueves): ");
            String dia = ENTRADA.nextLine().trim();
            System.out.print("¿A qué hora? (Ej: 5ª): ");
            String hora = ENTRADA.nextLine().trim();

            List<Profesor> candidatos = GESTOR.generarSustitutos(nombreAusenteFormateado, dia, hora);

            if (candidatos.isEmpty()) {
                System.out.println("⚠️ No hay profesores disponibles en esa franja o los datos de día/hora son incorrectos.");
            } else {
                System.out.println("\n--- Lista de Candidatos Compatibles para " + nombreAusenteFormateado + " (" + dia + " " + hora + ") ---");
                System.out.println("Nota: Se listan por menor carga acumulada (el más bajo es el ideal):");

                for(int i = 0; i < candidatos.size(); ++i) {
                    System.out.printf("Candidato %d: %s\n", i + 1, candidatos.get(i).toString());
                }

                System.out.println("---------------------------------------------------------------------------------");
                String elMejorCandidato = candidatos.get(0).getNombre();
                System.out.printf("Propuesta del sistema: %s (Tiene la menor cantidad de sustituciones).\n", elMejorCandidato);
                System.out.print("¿Deseas registrar y asignar la sustitución a " + elMejorCandidato + "? Escribe **S** para confirmar: ");
                String confirmacion = ENTRADA.nextLine();

                if (confirmacion.trim().equalsIgnoreCase("S")) {
                    GESTOR.registrarSustitucion(elMejorCandidato, dia, hora);
                } else {
                    System.out.println("Proceso cancelado. No se ha generado ningún registro.");
                }

            }
        }
    }

    private static void consultarCarga() {
        System.out.print("\nEscribe el nombre del profesor para consultar su historial: ");
        String nombre = ENTRADA.nextLine().trim();
        GESTOR.consultarProfesor(nombre);
    }
}