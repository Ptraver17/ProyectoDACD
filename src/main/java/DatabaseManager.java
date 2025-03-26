import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:football_news.db"; // Nombre de tu base de datos

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Error conectando a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    public static void insertFootballData(String competitionName, String areaName) {
        String sql = "INSERT INTO football_data (competition_name, area_name) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, competitionName);
            pstmt.setString(2, areaName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertando en football_data: " + e.getMessage());
        }
    }

    public static void insertNewsData(String title, String description, String url) {
        String sql = "INSERT INTO news_data (title, description, url) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, url);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertando en news_data: " + e.getMessage());
        }
    }
}

