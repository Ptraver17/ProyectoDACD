package Common;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:football_news.db";

    // Conectar a la base de datos
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("❌ Error conectando a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    // Crear tablas si no existen
    public static void createTables() {
        String createMatchesTable = "CREATE TABLE IF NOT EXISTS matches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "match_id TEXT UNIQUE, " +
                "home_team TEXT, " +
                "away_team TEXT, " +
                "match_date TEXT, " +
                "matchday INTEGER)"; // Jornada actual

        String createNewsTable = "CREATE TABLE IF NOT EXISTS news (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "match_id TEXT, " +
                "title TEXT, " +
                "description TEXT, " +
                "url TEXT, " +
                "FOREIGN KEY (match_id) REFERENCES matches (match_id))";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createMatchesTable);
            stmt.execute(createNewsTable);
            System.out.println("✅ Tablas creadas o verificadas correctamente.");
        } catch (SQLException e) {
            System.out.println("❌ Error creando tablas: " + e.getMessage());
        }
    }

    // Insertar partido (con jornada)
    public static void insertMatch(String matchId, String homeTeam, String awayTeam, String matchDate, int matchday) {
        String sql = "INSERT OR IGNORE INTO matches (match_id, home_team, away_team, match_date, matchday) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matchId);
            pstmt.setString(2, homeTeam);
            pstmt.setString(3, awayTeam);
            pstmt.setString(4, matchDate);
            pstmt.setInt(5, matchday);
            pstmt.executeUpdate();
            System.out.println("✅ Partido insertado: " + homeTeam + " vs " + awayTeam + " (Jornada " + matchday + ")");
        } catch (SQLException e) {
            System.out.println("❌ Error insertando partido: " + e.getMessage());
        }
    }

    // Insertar noticia
    public static void insertNews(String matchId, String title, String description, String url) {
        String sql = "INSERT INTO news (match_id, title, description, url) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matchId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, url);
            pstmt.executeUpdate();
            System.out.println("📰 Noticia insertada: " + title);
        } catch (SQLException e) {
            System.out.println("❌ Error insertando noticia: " + e.getMessage());
        }
    }

    // Obtener todos los partidos de una jornada
    public static List<Match> getMatchesByMatchday(int matchday) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT match_id, home_team, away_team, match_date FROM matches WHERE matchday = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matchday);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String matchId = rs.getString("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getString("match_date");

                matches.add(new Match(matchId, homeTeam, awayTeam, matchDate));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error obteniendo partidos: " + e.getMessage());
        }
        return matches;
    }

    // Obtener todas las noticias (podrías modificar para filtrar por matchday si es necesario)
    public static List<NewsItem> getAllNews() {
        List<NewsItem> newsList = new ArrayList<>();
        String sql = "SELECT match_id, title, description, url FROM news";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String matchId = rs.getString("match_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String url = rs.getString("url");

                newsList.add(new NewsItem(matchId, title, description, url));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error obteniendo noticias: " + e.getMessage());
        }
        return newsList;
    }
}
