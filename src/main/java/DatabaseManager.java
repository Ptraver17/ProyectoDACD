import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:football_news.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Error conectando a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    public static void insertMatch(String matchId, String homeTeam, String awayTeam, String matchDate) {
        String sql = "INSERT OR IGNORE INTO matches (match_id, home_team, away_team, match_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matchId);
            pstmt.setString(2, homeTeam);
            pstmt.setString(3, awayTeam);
            pstmt.setString(4, matchDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertando partido: " + e.getMessage());
        }
    }

    public static void insertNews(String matchId, String title, String description, String url) {
        String sql = "INSERT INTO news (match_id, title, description, url) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matchId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, url);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertando noticia: " + e.getMessage());
        }
    }

    // 🚀 NUEVA FUNCIÓN: Obtener todos los partidos guardados en la BD
    public static List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT match_id, home_team, away_team, match_date FROM matches";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String matchId = rs.getString("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getString("match_date");

                matches.add(new Match(matchId, homeTeam, awayTeam, matchDate));
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo partidos: " + e.getMessage());
        }
        return matches;
    }
}
