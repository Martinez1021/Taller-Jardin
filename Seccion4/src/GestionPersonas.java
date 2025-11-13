import java.sql.*;
import java.util.Scanner;

public class GestionPersonas {

    private static final String URL = "jdbc:mysql://localhost:3306/ejercicios_db";
    private static final String USER = "root";
    private static final String PASSWORD = "RootPass123!";
    private static Scanner scanner = new Scanner(System.in);

    // Método para obtener conexión
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // EJERCICIO 28: Mostrar todas las personas
    public static void mostrarPersonas() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sqlQuery = "SELECT * FROM Persona";
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            // Encabezado
            System.out.println("\n" + "=".repeat(110));
            System.out.println("                              LISTADO DE PERSONAS");
            System.out.println("=".repeat(110));
            String headerFormat = "%-4s %-15s %-15s %-14s %-35s %-12s%n";
            System.out.printf(headerFormat, "ID", "NOMBRE", "APELLIDO", "FECHA NAC.", "EMAIL", "TELÉFONO");
            System.out.println("-".repeat(110));

            // Datos
            int contador = 0;
            String rowFormat = "%-4d %-15s %-15s %-14s %-35s %-12s%n";

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

            System.out.println("=".repeat(110));
            System.out.println("✓ Total de personas recuperadas: " + contador);
            System.out.println("✓ Todas las personas se han recuperado correctamente\n");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✗ Error al mostrar personas: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // EJERCICIO 29: Insertar nueva persona
    public static void insertarPersona() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            System.out.println("\n=== INSERTAR NUEVA PERSONA ===");

            // Leer datos por teclado
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();

            System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
            String fechaNacimiento = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine();

            // Conectar y preparar consulta INSERT
            connection = getConnection();
            String sqlInsert = "INSERT INTO Persona (nombre, apellido, fecha_nacimiento, email, telefono) " +
                    "VALUES (?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sqlInsert);
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apellido);
            preparedStatement.setString(3, fechaNacimiento);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, telefono);

            // Ejecutar inserción
            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("\n✓ Persona insertada correctamente!");
                System.out.println("✓ Filas afectadas: " + filasAfectadas);

                // Comprobar que se ha insertado mostrando el último registro
                System.out.println("\n--- Verificación de inserción ---");
                mostrarUltimaPersona(connection);
            } else {
                System.out.println("✗ No se pudo insertar la persona");
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✗ Error al insertar persona: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método auxiliar para verificar la inserción
    private static void mostrarUltimaPersona(Connection connection) throws SQLException {
        String sqlQuery = "SELECT * FROM Persona ORDER BY id DESC LIMIT 1";
        PreparedStatement ps = connection.prepareStatement(sqlQuery);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Nombre: " + rs.getString("nombre"));
            System.out.println("Apellido: " + rs.getString("apellido"));
            System.out.println("Fecha Nacimiento: " + rs.getString("fecha_nacimiento"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("Teléfono: " + rs.getString("telefono"));
        }

        rs.close();
        ps.close();
    }

    // EJERCICIO 30: Actualizar persona por ID (CON CONFIRMACIÓN)
    public static void actualizarPersona() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();

            System.out.println("\n=== ACTUALIZAR PERSONA ===");

            // Mostrar personas disponibles
            mostrarPersonasSimple(connection);

            System.out.print("\nIntroduce el ID de la persona a actualizar: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            // Verificar que existe
            if (!existePersona(connection, id)) {
                System.out.println("✗ No existe ninguna persona con ID: " + id);
                return;
            }

            // Mostrar datos actuales
            mostrarPersonaPorId(connection, id);

            // Confirmar antes de proceder
            System.out.print("\n¿Deseas actualizar esta persona? (S/N): ");
            String confirmacion = scanner.nextLine();

            if (!confirmacion.equalsIgnoreCase("S")) {
                System.out.println("✗ Operación cancelada");
                return;
            }

            // Leer nuevos datos
            System.out.println("\nIntroduce los nuevos datos (deja en blanco para mantener el valor actual):");

            System.out.print("Nuevo nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Nuevo apellido: ");
            String apellido = scanner.nextLine();

            System.out.print("Nuevo email: ");
            String email = scanner.nextLine();

            System.out.print("Nuevo teléfono: ");
            String telefono = scanner.nextLine();

            // Construir consulta dinámica solo con campos no vacíos
            StringBuilder sqlUpdate = new StringBuilder("UPDATE Persona SET ");
            boolean primero = true;

            if (!nombre.isEmpty()) {
                sqlUpdate.append("nombre = ?");
                primero = false;
            }
            if (!apellido.isEmpty()) {
                if (!primero) sqlUpdate.append(", ");
                sqlUpdate.append("apellido = ?");
                primero = false;
            }
            if (!email.isEmpty()) {
                if (!primero) sqlUpdate.append(", ");
                sqlUpdate.append("email = ?");
                primero = false;
            }
            if (!telefono.isEmpty()) {
                if (!primero) sqlUpdate.append(", ");
                sqlUpdate.append("telefono = ?");
            }

            sqlUpdate.append(" WHERE id = ?");

            preparedStatement = connection.prepareStatement(sqlUpdate.toString());

            // Asignar parámetros
            int paramIndex = 1;
            if (!nombre.isEmpty()) preparedStatement.setString(paramIndex++, nombre);
            if (!apellido.isEmpty()) preparedStatement.setString(paramIndex++, apellido);
            if (!email.isEmpty()) preparedStatement.setString(paramIndex++, email);
            if (!telefono.isEmpty()) preparedStatement.setString(paramIndex++, telefono);
            preparedStatement.setInt(paramIndex, id);

            // Ejecutar actualización
            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("\n✓ Persona actualizada correctamente!");
                System.out.println("✓ Filas afectadas: " + filasAfectadas);

                System.out.println("\n--- Datos actualizados ---");
                mostrarPersonaPorId(connection, id);
            } else {
                System.out.println("✗ No se pudo actualizar la persona");
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✗ Error al actualizar persona: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // EJERCICIO 31: Eliminar persona por ID (CON CONFIRMACIÓN)
    public static void eliminarPersona() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();

            System.out.println("\n=== ELIMINAR PERSONA ===");

            // Mostrar personas disponibles
            mostrarPersonasSimple(connection);

            System.out.print("\nIntroduce el ID de la persona a eliminar: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            // Verificar que existe
            if (!existePersona(connection, id)) {
                System.out.println("✗ No existe ninguna persona con ID: " + id);
                return;
            }

            // Mostrar datos de la persona a eliminar
            mostrarPersonaPorId(connection, id);

            // Confirmar antes de eliminar
            System.out.print("\n⚠️  ¿Estás seguro de que deseas ELIMINAR esta persona? (S/N): ");
            String confirmacion = scanner.nextLine();

            if (!confirmacion.equalsIgnoreCase("S")) {
                System.out.println("✗ Operación cancelada");
                return;
            }

            // Segunda confirmación
            System.out.print("⚠️  Esta acción no se puede deshacer. Confirmar eliminación (S/N): ");
            String confirmacion2 = scanner.nextLine();

            if (!confirmacion2.equalsIgnoreCase("S")) {
                System.out.println("✗ Operación cancelada");
                return;
            }

            // Ejecutar eliminación
            String sqlDelete = "DELETE FROM Persona WHERE id = ?";
            preparedStatement = connection.prepareStatement(sqlDelete);
            preparedStatement.setInt(1, id);

            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("\n✓ Persona eliminada correctamente!");
                System.out.println("✓ Filas afectadas: " + filasAfectadas);
            } else {
                System.out.println("✗ No se pudo eliminar la persona");
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✗ Error al eliminar persona: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Métodos auxiliares
    private static void mostrarPersonasSimple(Connection connection) throws SQLException {
        String sqlQuery = "SELECT id, nombre, apellido FROM Persona";
        PreparedStatement ps = connection.prepareStatement(sqlQuery);
        ResultSet rs = ps.executeQuery();

        System.out.println("\nPersonas disponibles:");
        System.out.println("-".repeat(50));
        while (rs.next()) {
            System.out.printf("ID: %d - %s %s%n",
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"));
        }
        System.out.println("-".repeat(50));

        rs.close();
        ps.close();
    }

    private static void mostrarPersonaPorId(Connection connection, int id) throws SQLException {
        String sqlQuery = "SELECT * FROM Persona WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sqlQuery);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Nombre: " + rs.getString("nombre"));
            System.out.println("Apellido: " + rs.getString("apellido"));
            System.out.println("Fecha Nacimiento: " + rs.getString("fecha_nacimiento"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("Teléfono: " + rs.getString("telefono"));
        }

        rs.close();
        ps.close();
    }

    private static boolean existePersona(Connection connection, int id) throws SQLException {
        String sqlQuery = "SELECT COUNT(*) FROM Persona WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sqlQuery);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();

        return count > 0;
    }

    // Menú principal
    public static void mostrarMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     GESTIÓN DE BASE DE DATOS MYSQL     ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Mostrar todas las personas          ║");
        System.out.println("║ 2. Insertar nueva persona              ║");
        System.out.println("║ 3. Actualizar persona                  ║");
        System.out.println("║ 4. Eliminar persona                    ║");
        System.out.println("║ 0. Salir                               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Selecciona una opción: ");
    }

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        mostrarPersonas();
                        break;
                    case 2:
                        insertarPersona();
                        break;
                    case 3:
                        actualizarPersona();
                        break;
                    case 4:
                        eliminarPersona();
                        break;
                    case 0:
                        System.out.println("\n✓ Saliendo del programa...");
                        continuar = false;
                        break;
                    default:
                        System.out.println("✗ Opción no válida");
                }

            } catch (Exception e) {
                System.err.println("✗ Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer en caso de error
            }
        }

        scanner.close();
        System.out.println("✓ Programa finalizado");
    }
}

