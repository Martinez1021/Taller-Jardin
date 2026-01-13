import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MostrarPersonasSimple {

    private static final String URL = "jdbc:mysql://localhost:3306/ejercicios_db";
    private static final String USER = "root";
    private static final String PASSWORD = "RootPass123!";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Conexión establecida\n");

            String sqlQuery = "SELECT * FROM Persona";
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            // Encabezado
            System.out.println("=".repeat(100));
            System.out.println("                              LISTADO DE PERSONAS");
            System.out.println("=".repeat(100));
            String headerFormat = "%-4s %-15s %-15s %-14s %-30s %-12s%n";
            System.out.printf(headerFormat, "ID", "NOMBRE", "APELLIDO", "FECHA NAC.", "EMAIL", "TELÉFONO");
            System.out.println("-".repeat(100));

            // Datos
            int contador = 0;
            String rowFormat = "%-4d %-15s %-15s %-14s %-30s %-12s%n";

            while (resultSet.next()) {
                System.out.printf(rowFormat,
                        resultSet.getInt("id"),
                        resultSet.getString("nombre") != null ? resultSet.getString("nombre") : "N/A",
                        resultSet.getString("apellido") != null ? resultSet.getString("apellido") : "N/A",
                        resultSet.getString("fecha_nacimiento") != null ? resultSet.getString("fecha_nacimiento") : "N/A",
                        resultSet.getString("email") != null ? resultSet.getString("email") : "N/A",
                        resultSet.getString("telefono") != null ? resultSet.getString("telefono") : "N/A"
                );
                contador++;
            }

            System.out.println("=".repeat(100));
            System.out.println("Total de personas: " + contador);

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver no encontrado");
            e.printStackTrace();

        } catch (SQLException e) {
            System.err.println("✗ Error SQL: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
                System.out.println("\n✓ Recursos cerrados correctamente.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

