import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class FootballCall {
    private static final String API_KEY = "943a1112a67c46758085602f97729bd9";
    private static final String API_URL = "https://api.football-data.org/v4/competitions/PL/matches?matchday=30"; // 🔹 Obtenemos directamente la jornada 30

    public static void fetchMatchesForMatchday() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", API_KEY);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray matches = jsonResponse.getJSONArray("matches");

                System.out.println("📅 Partidos de la jornada 30:");

                // 🔹 Guardamos los partidos de esta jornada
                for (int i = 0; i < matches.length(); i++) {
                    JSONObject match = matches.getJSONObject(i);
                    String matchId = String.valueOf(match.getInt("id"));
                    String homeTeam = match.getJSONObject("homeTeam").getString("name");
                    String awayTeam = match.getJSONObject("awayTeam").getString("name");
                    String matchDate = match.getString("utcDate").substring(0, 10); // 🔹 Extraemos solo la fecha (YYYY-MM-DD)

                    DatabaseManager.insertMatch(matchId, homeTeam, awayTeam, matchDate);
                    System.out.println("✅ " + homeTeam + " vs " + awayTeam + " (" + matchDate + ")");
                }
            } else {
                System.out.println("❌ Error en la API de fútbol. Código: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
