package news.feeder;


import Common.DatabaseManager;
import Common.Match;
import Common.NewsItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.List;

public class NewsFeeder {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "news.events";

    public static void sendNewsForMatchday(String league, int matchday) {
        try {
            List<Match> matches = DatabaseManager.getMatchesByLeagueAndMatchday(league, matchday);
            ObjectMapper mapper = new ObjectMapper();

            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);

            for (Match match : matches) {
                List<NewsItem> newsList = DatabaseManager.getAllNews(); // O filtra si es necesario

                for (NewsItem news : newsList) {
                    if (news.getMatchId().equals(match.getMatchId())) {
                        NewsEvent event = new NewsEvent(
                                news.getMatchId(),
                                news.getTitle(),
                                news.getDescription(),
                                news.getUrl()
                        );

                        String json = mapper.writeValueAsString(event);
                        TextMessage message = session.createTextMessage(json);
                        producer.send(message);

                        System.out.println("📰 Noticia enviada: " + news.getTitle());
                    }
                }
            }

            producer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("❌ Error en NewsFeeder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
