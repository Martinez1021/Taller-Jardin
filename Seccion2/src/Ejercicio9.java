import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ejercicio9 {
    public static void main(String[] args) {

        File file = new File("C:\\ejemplos\\hola.txt");
        try (FileReader fr = new FileReader(file)) {
            int character;
            while ((character = fr.read()) != -1) {
                System.out.print((char) character);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}