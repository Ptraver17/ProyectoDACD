import com.fasterxml.jackson.databind.ObjectMapper;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.List;

public class NewsFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "news.events";

    public void sendNewsEvents() {
        try {
            // Obtener las noticias de la base de datos
            List<NewsItem> newsList = DatabaseManager.getAllNews();

            // Conexión con ActiveMQ
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);

            ObjectMapper mapper = new ObjectMapper();

            for (NewsItem news : newsList) {
                NewsEvent event = new NewsEvent(
                        news.getMatchId(),
                        news.getTitle(),
                        news.getDescription(),
                        news.getUrl()
                );

                String json = mapper.writeValueAsString(event);
                TextMessage message = session.createTextMessage(json);
                producer.send(message);

                System.out.println("✅ Enviada noticia: " + json);
            }

            producer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("❌ Error enviando eventos de noticias: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
