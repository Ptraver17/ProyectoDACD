import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class FootballCall {
    private static final String API_KEY = "943a1112a67c46758085602f97729bd9";
    private static final String API_URL = "https://api.football-data.org/v4/competitions/PD";

    public static void main(String[] args) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", API_KEY);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parseamos el JSON obtenido
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Ejemplo: extraer el nombre de la competición
                String competitionName = jsonResponse.optString("name", "Nombre no disponible");
                System.out.println("Competition Name: " + competitionName);

                // Ejemplo: extraer el objeto 'area' y su nombre, si existe
                if(jsonResponse.has("area")) {
                    JSONObject area = jsonResponse.getJSONObject("area");
                    String areaName = area.optString("name", "Área no disponible");
                    System.out.println("Area Name: " + areaName);
                }

                // Imprime el JSON completo (opcional)
                System.out.println("Response JSON: " + jsonResponse.toString(2));
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
