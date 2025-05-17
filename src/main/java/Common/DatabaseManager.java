package Common;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:football_news.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            return null;
        }
    }

    public static void createTables() {
        String createMatchesTable = "CREATE TABLE IF NOT EXISTS matches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "match_id TEXT UNIQUE, " +
                "home_team TEXT, " +
                "away_team TEXT, " +
                "match_date TEXT, " +
                "matchday INTEGER, " +
                "league TEXT)";

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertMatch(String matchId, String homeTeam, String awayTeam, String matchDate, int matchday, String league) {
        String sql = "INSERT OR IGNORE INTO matches (match_id, home_team, away_team, match_date, matchday, league) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matchId);
            pstmt.setString(2, homeTeam);
            pstmt.setString(3, awayTeam);
            pstmt.setString(4, matchDate);
            pstmt.setInt(5, matchday);
            pstmt.setString(6, league);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public static List<Match> getMatchesByLeagueAndMatchday(String league, int matchday) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT match_id, home_team, away_team, match_date, matchday, league FROM matches WHERE league = ? AND matchday = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, league);
            pstmt.setInt(2, matchday);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                matches.add(new Match(
                        rs.getString("match_id"),
                        rs.getString("home_team"),
                        rs.getString("away_team"),
                        rs.getString("match_date"),
                        rs.getInt("matchday"),
                        rs.getString("league")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matches;
    }

    public static List<NewsItem> getAllNews() {
        List<NewsItem> newsList = new ArrayList<>();
        String sql = "SELECT match_id, title, description, url FROM news";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                newsList.add(new NewsItem(
                        rs.getString("match_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newsList;
    }
}
