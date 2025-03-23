import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main{
    private static final String URL = "jdbc:sqlite:C:/Users/111di/IdeaProjects/Sprint1Proyecto/src/example";
    public static void main(String[] args){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(URL);
            statement = connection.createStatement();
            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS Usuarios (" +
                    "id INTEGER PRIMARE KEY, " +
                    "nombre TEXT NOT NULL," +
                    "email TEXT NOT NULL)";
            statement.executeUpdate(sqlCreateTable);
            System.out.println("Tabla 'Usuarios' creada");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
