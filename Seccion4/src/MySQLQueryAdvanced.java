import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLQueryAdvanced {

    private static final String URL = "jdbc:mysql://localhost:3306/ejercicios_db";
    private static final String USER = "root";
    private static final String PASSWORD = "RootPass123!";

    // Método para obtener conexión
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Método para consultar una tabla
    private static void consultarTabla(Connection connection, String nombreTabla)
            throws SQLException {

        String sqlQuery = "SELECT * FROM " + nombreTabla;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("\n=== REGISTROS DE LA TABLA: " + nombreTabla.toUpperCase() + " ===");

        int numColumnas = resultSet.getMetaData().getColumnCount();

        // Mostrar nombres de columnas
        for (int i = 1; i <= numColumnas; i++) {
            System.out.print(resultSet.getMetaData().getColumnName(i) + "\t");
        }
        System.out.println();
        System.out.println("=".repeat(80));

        // Mostrar datos
        int contador = 0;
        while (resultSet.next()) {
            for (int i = 1; i <= numColumnas; i++) {
                System.out.print(resultSet.getString(i) + "\t");
            }
            System.out.println();
            contador++;
        }

        System.out.println("=".repeat(80));
        System.out.println("Total de registros: " + contador);

        resultSet.close();
        preparedStatement.close();
    }

    public static void main(String[] args) {
        Connection connection = null;

        try {
            System.out.println("Conectando a la base de datos...");
            connection = getConnection();
            System.out.println("✓ Conexión exitosa");

            // Consultar diferentes tablas
            consultarTabla(connection, "Persona");
            consultarTabla(connection, "Producto");
            consultarTabla(connection, "Cliente");
            consultarTabla(connection, "Empleado");
            consultarTabla(connection, "Pedido");

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver no encontrado");
            e.printStackTrace();

        } catch (SQLException e) {
            System.err.println("✗ Error SQL: " + e.getMessage());
            e.printStackTrace();

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("\n✓ Conexión cerrada correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

