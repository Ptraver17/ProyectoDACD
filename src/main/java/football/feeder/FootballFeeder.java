import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FootballFeeder {
    private static final String API_URL = "https://api.football-data.org/v4/competitions/PD/matches?matchday=30";
    private static final String API_KEY = "943a1112a67c46758085602f97729bd9";
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "football.matches";

    public static void main(String[] args) {
        try {
            // 🔹 Llamada a la API
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", API_KEY);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("❌ Error en la llamada a la API. Código: " + responseCode);
                return;
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            // 🔹 Parsear JSON y enviar partidos
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json.toString());
            JsonNode matches = root.get("matches");

            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            try (Connection mqConnection = factory.createConnection();
                 Session session = mqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

                mqConnection.start();
                Destination queue = session.createQueue(QUEUE_NAME);
                MessageProducer producer = session.createProducer(queue);

                for (JsonNode match : matches) {
                    String matchId = String.valueOf(match.get("id").asInt());
                    String homeTeam = match.get("homeTeam").get("name").asText();
                    String awayTeam = match.get("awayTeam").get("name").asText();
                    String date = match.get("utcDate").asText();

                    Match matchObj = new Match(matchId, homeTeam, awayTeam, date);

                    // 🔹 Serializar objeto a JSON y enviar
                    String matchJson = mapper.writeValueAsString(matchObj);
                    TextMessage message = session.createTextMessage(matchJson);
                    producer.send(message);

                    System.out.println("✅ Partido enviado: " + homeTeam + " vs " + awayTeam);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error en FootballFeeder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
