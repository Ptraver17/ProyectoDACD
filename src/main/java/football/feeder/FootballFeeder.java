package football.feeder;

import Common.Match;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FootballFeeder {
    private static final String API_KEY = "943a1112a67c46758085602f97729bd9";
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "football.matches";

    public static void sendMatchesForMatchday(String leagueCode, int matchday) {
        try {
            String apiUrl = "https://api.football-data.org/v4/competitions/" + leagueCode + "/matches?matchday=" + matchday;
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", API_KEY);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) return;

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) json.append(scanner.nextLine());
            scanner.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json.toString());
            JsonNode matches = root.get("matches");

            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection mqConnection = factory.createConnection();
            mqConnection.start();

            Session session = mqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);

            for (JsonNode match : matches) {
                String matchId = String.valueOf(match.get("id").asInt());
                String homeTeam = match.get("homeTeam").get("name").asText();
                String awayTeam = match.get("awayTeam").get("name").asText();
                String date = match.get("utcDate").asText();

                Match matchObj = new Match(matchId, homeTeam, awayTeam, date, matchday, leagueCode);
                String matchJson = mapper.writeValueAsString(matchObj);
                TextMessage message = session.createTextMessage(matchJson);
                producer.send(message);
            }

            producer.close();
            session.close();
            mqConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
