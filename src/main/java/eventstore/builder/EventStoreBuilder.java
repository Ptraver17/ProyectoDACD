package eventstore.builder;

import Common.DatabaseManager;
import Common.Match;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import news.feeder.NewsEvent;
import org.apache.activemq.ActiveMQConnectionFactory;

public class EventStoreBuilder {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String FOOTBALL_TOPIC = "football.matches";
    private static final String NEWS_TOPIC = "news.events";

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Listener para partidos
            Topic footballTopic = session.createTopic(FOOTBALL_TOPIC);
            MessageConsumer footballConsumer = session.createConsumer(footballTopic);
            footballConsumer.setMessageListener(message -> handleMatchMessage(message));

            // Listener para noticias
            Topic newsTopic = session.createTopic(NEWS_TOPIC);
            MessageConsumer newsConsumer = session.createConsumer(newsTopic);
            newsConsumer.setMessageListener(message -> handleNewsMessage(message));

            System.out.println("🎧 Escuchando eventos en topics:");
            System.out.println(" - " + FOOTBALL_TOPIC);
            System.out.println(" - " + NEWS_TOPIC);

        } catch (Exception e) {
            System.out.println("❌ Error en EventStoreBuilder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleMatchMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String json = ((TextMessage) message).getText();
                ObjectMapper mapper = new ObjectMapper();
                Match match = mapper.readValue(json, Match.class);

                DatabaseManager.insertMatch(
                        match.getMatchId(),
                        match.getHomeTeam(),
                        match.getAwayTeam(),
                        match.getMatchDate(),
                        match.getMatchday(),
                        match.getLeague()
                );

                System.out.println("✅ Partido guardado: " + match);
            }
        } catch (Exception e) {
            System.out.println("❌ Error procesando partido: " + e.getMessage());
        }
    }

    private static void handleNewsMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String json = ((TextMessage) message).getText();
                ObjectMapper mapper = new ObjectMapper();
                NewsEvent news = mapper.readValue(json, NewsEvent.class);

                DatabaseManager.insertNews(
                        news.getMatchId(),
                        news.getTitle(),
                        news.getDescription(),
                        news.getUrl()
                );

                System.out.println("📰 Noticia guardada: " + news.getTitle());
            }
        } catch (Exception e) {
            System.out.println("❌ Error procesando noticia: " + e.getMessage());
        }
    }
}
