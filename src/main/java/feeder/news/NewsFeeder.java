package feeder.news;

import common.Match;
import common.NewsItem;
import feeder.news.NewsEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class NewsFeeder {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "news.events";
    private static final String MATCH_FILE = "eventstore/matches.events";
    private static final String NEWS_FILE = "eventstore/news.events";

    public static void sendNewsForMatchday(String league, int matchday) {
        try {
            List<Match> matches = loadMatches(league, matchday);
            List<NewsItem> newsItems = loadNews();

            ObjectMapper mapper = new ObjectMapper();
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);

            for (Match match : matches) {
                for (NewsItem news : newsItems) {
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
                    }
                }
            }

            producer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Error en NewsFeeder: " + e.getMessage());
        }
    }

    private static List<Match> loadMatches(String league, int matchday) {
        List<Match> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new FileReader(MATCH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Match match = mapper.readValue(line, Match.class);
                if (match.getLeague().equalsIgnoreCase(league) && match.getMatchday() == matchday) {
                    list.add(match);
                }
            }
        } catch (Exception e) {
            System.out.println("Error leyendo partidos: " + e.getMessage());
        }

        return list;
    }

    private static List<NewsItem> loadNews() {
        List<NewsItem> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new FileReader(NEWS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                NewsItem news = mapper.readValue(line, NewsItem.class);
                list.add(news);
            }
        } catch (Exception e) {
            System.out.println("Error leyendo noticias: " + e.getMessage());
        }

        return list;
    }
}
