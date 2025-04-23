import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class FeederPublisher {
    public static void send(String json, String topicName) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(topic);

            TextMessage message = session.createTextMessage(json);
            producer.send(message);

            System.out.println("✅ Evento enviado al topic " + topicName);

            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("❌ Error enviando evento: " + e.getMessage());
        }
    }
}
