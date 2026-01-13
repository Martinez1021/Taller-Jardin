import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionTest {

    // Parámetros de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/ejercicios_db";
    private static final String USER = "root";
    private static final String PASSWORD = "RootPass123!";

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Cargar el driver de MySQL (opcional en versiones recientes)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Intentar establecer la conexión
            System.out.println("Intentando conectar a la base de datos...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Si llegamos aquí, la conexión fue exitosa
            System.out.println("✓ ¡Conexión exitosa a la base de datos MySQL!");
            System.out.println("✓ Base de datos: ejercicios_db");
            System.out.println("✓ URL: " + URL);

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: No se encontró el driver de MySQL");
            System.err.println("Detalles: " + e.getMessage());

        } catch (SQLException e) {
            System.err.println("✗ Error de conexión a la base de datos");
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("Mensaje: " + e.getMessage());

        } finally {
            // Cerrar la conexión si está abierta
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Conexión cerrada correctamente.");
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}
