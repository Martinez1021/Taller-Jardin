import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            List<String[]> alumnos = leerAlumnos("nombres.txt");
            List<Pregunta> rubrica = leerRubrica("rubrica.txt");

            Scanner scanner = new Scanner(System.in);

            for (String[] alumno : alumnos) {
                if (alumno.length < 3) continue;

                System.out.println("\nVas a evaluar al alumno: " + alumno[0] + " " + alumno[1] + " " + alumno[2]);
                List<Integer> notas = new ArrayList<>();

                for (Pregunta pregunta : rubrica) {
                    System.out.println("\n" + pregunta.titulo);
                    for (Opcion opcion : pregunta.opciones) {
                        System.out.println(opcion.puntos + " - " + opcion.requisitos);
                    }

                    int nota = -1;
                    while (nota == -1) {
                        System.out.print("- Introduce la nota: ");
                        try {
                            nota = Integer.parseInt(scanner.nextLine().trim());
                            if (!pregunta.esNotaValida(nota)) {
                                System.out.println("Nota no válida. Introduce una de las opciones disponibles.");
                                nota = -1;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Por favor, introduce un número válido.");
                        }
                    }
                    notas.add(nota);
                }

                guardarNotas(alumno, notas, "nombres.txt");
            }

            scanner.close();
            System.out.println("\nEvaluación completada.");

        } catch (IOException e) {
            System.err.println("Error al leer los archivos: " + e.getMessage());
        }
    }

    private static List<String[]> leerAlumnos(String archivo) throws IOException {
        List<String[]> alumnos = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                String[] partes = linea.split(";");
                alumnos.add(partes);
            }
        }

        br.close();
        return alumnos;
    }

    private static List<Pregunta> leerRubrica(String archivo) throws IOException {
        List<Pregunta> preguntas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = br.readLine()) != null) {
            String[] partes = linea.split(";");
            if (partes.length > 0) {
                Pregunta pregunta = new Pregunta(partes[0]);
                for (int i = 1; i < partes.length; i++) {
                    String[] notaRequisito = partes[i].split("-", 2);
                    if (notaRequisito.length == 2) {
                        int puntos = Integer.parseInt(notaRequisito[0].trim());
                        String requisitos = notaRequisito[1].trim();
                        pregunta.agregarOpcion(puntos, requisitos);
                    }
                }
                preguntas.add(pregunta);
            }
        }

        br.close();
        return preguntas;
    }

    private static void guardarNotas(String[] alumno, List<Integer> notas, String archivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        boolean alumnoEncontrado = false;

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(";");
            if (partes.length >= 3 && partes[0].equals(alumno[0]) &&
                partes[1].equals(alumno[1]) && partes[2].equals(alumno[2])) {

                StringBuilder sb = new StringBuilder();
                sb.append(alumno[0]).append(";").append(alumno[1]).append(";").append(alumno[2]);
                for (int nota : notas) {
                    sb.append(";").append(nota);
                }
                lineas.add(sb.toString());
                alumnoEncontrado = true;
            } else {
                lineas.add(linea);
            }
        }
        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        for (String l : lineas) {
            bw.write(l);
            bw.newLine();
        }
        bw.close();

        System.out.println("Notas guardadas para: " + alumno[0] + " " + alumno[1] + " " + alumno[2]);
    }
}

class Pregunta {
    String titulo;
    List<Opcion> opciones;

    public Pregunta(String titulo) {
        this.titulo = titulo;
        this.opciones = new ArrayList<>();
    }

    public void agregarOpcion(int puntos, String requisitos) {
        opciones.add(new Opcion(puntos, requisitos));
    }

    public boolean esNotaValida(int nota) {
        for (Opcion opcion : opciones) {
            if (opcion.puntos == nota) {
                return true;
            }
        }
        return false;
    }
}

class Opcion {
    int puntos;
    String requisitos;

    public Opcion(int puntos, String requisitos) {
        this.puntos = puntos;
        this.requisitos = requisitos;
    }
}
