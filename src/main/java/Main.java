import java.sql.Connection;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        createTables();

        FootballCall.fetchFootballData();
        NewsCall.fetchNewsData();
    }

    private static void createTables() {
        String footballTable = "CREATE TABLE IF NOT EXISTS football_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "competition_name TEXT, " +
                "area_name TEXT)";

        String newsTable = "CREATE TABLE IF NOT EXISTS news_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "url TEXT)";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(footballTable);
            stmt.execute(newsTable);
            System.out.println("Tablas creadas correctamente.");
        } catch (Exception e) {
            System.out.println("Error creando tablas: " + e.getMessage());
        }
    }
}
