import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class FootballCall {
    private static final String API_KEY = "943a1112a67c46758085602f97729bd9";
    private static final String API_URL = "https://api.football-data.org/v4/competitions/PD";

    public static void fetchFootballData() {
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
                String competitionName = jsonResponse.optString("name", "Nombre no disponible");
                String areaName = jsonResponse.getJSONObject("area").optString("name", "Área no disponible");

                DatabaseManager.insertFootballData(competitionName, areaName);
                System.out.println("Datos de fútbol insertados en la base de datos.");
            } else {
                System.out.println("Error en la llamada a la API de fútbol. Código: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
