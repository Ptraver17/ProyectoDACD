import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class EventStoreBuilder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "futbol.eventos";

    public static void main(String[] args) {
        try {
            // Crear conexión con ActiveMQ
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Crear sesión sin transacciones, con auto-ack
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(TOPIC_NAME);
            MessageConsumer consumer = session.createConsumer(destination);

            System.out.println("🎧 Esperando mensajes en el tópico '" + TOPIC_NAME + "'...");

            consumer.setMessageListener(message -> {
                if (message instanceof ObjectMessage) {
                    try {
                        Object payload = ((ObjectMessage) message).getObject();

                        if (payload instanceof Match) {
                            Match match = (Match) payload;
                            DatabaseManager.insertMatch(
                                    match.getMatchId(),
                                    match.getHomeTeam(),
                                    match.getAwayTeam(),
                                    match.getMatchDate()
                            );
                            System.out.println("✅ Partido almacenado: " + match);
                        } else if (payload instanceof NewsItem) {
                            NewsItem news = (NewsItem) payload;
                            DatabaseManager.insertNews(
                                    news.getMatchId(),
                                    news.getTitle(),
                                    news.getDescription(),
                                    news.getUrl()
                            );
                            System.out.println("📰 Noticia almacenada: " + news);
                        } else {
                            System.out.println("⚠️ Tipo de mensaje no reconocido.");
                        }

                    } catch (JMSException e) {
                        System.err.println("❌ Error procesando mensaje: " + e.getMessage());
                    }
                } else {
                    System.out.println("⚠️ Mensaje recibido no es de tipo ObjectMessage.");
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Error conectando al broker: " + e.getMessage());
        }
    }
}
