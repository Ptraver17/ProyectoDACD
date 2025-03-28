import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        createTables();

        // 🔹 Obtener los partidos de la jornada específica
        FootballCall.fetchMatchesForMatchday();

        // 🔹 Obtener noticias relacionadas con estos partidos
        for (Match match : DatabaseManager.getAllMatches()) {
            NewsCall.fetchNewsForMatch(match.getMatchId(), match.getHomeTeam(), match.getAwayTeam());
        }
    }

    private static void createTables() {
        String matchesTable = "CREATE TABLE IF NOT EXISTS matches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "match_id TEXT UNIQUE, " +
                "home_team TEXT, " +
                "away_team TEXT, " +
                "match_date TEXT)";

        String newsTable = "CREATE TABLE IF NOT EXISTS news (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "match_id TEXT, " +
                "title TEXT, " +
                "description TEXT, " +
                "url TEXT, " +
                "FOREIGN KEY (match_id) REFERENCES matches (match_id))";

        try (Connection conn = DatabaseManager.connect();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(matchesTable);
            stmt.execute(newsTable);
            System.out.println("✅ Tablas creadas correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error creando tablas: " + e.getMessage());
        }
    }
}
