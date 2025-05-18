package eventstore.builder;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EventStoreBuilder {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String FOOTBALL_TOPIC = "football.matches";
    private static final String NEWS_TOPIC = "news.events";
    private static final String MATCH_FILE = "eventstore/matches.events";
    private static final String NEWS_FILE = "eventstore/news.events";

    public static void main(String[] args) {
        ensureEventFilesExist();

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic footballTopic = session.createTopic(FOOTBALL_TOPIC);
            MessageConsumer footballConsumer = session.createConsumer(footballTopic);
            footballConsumer.setMessageListener(message -> handleMatchMessage(message));

            Topic newsTopic = session.createTopic(NEWS_TOPIC);
            MessageConsumer newsConsumer = session.createConsumer(newsTopic);
            newsConsumer.setMessageListener(message -> handleNewsMessage(message));

            System.out.println("🎧 EventStoreBuilder escuchando en:");
            System.out.println(" - " + FOOTBALL_TOPIC);
            System.out.println(" - " + NEWS_TOPIC);

        } catch (Exception e) {
            System.out.println("❌ Error en EventStoreBuilder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ensureEventFilesExist() {
        try {
            File dir = new File("eventstore");
            if (!dir.exists()) dir.mkdirs();

            File matchFile = new File(MATCH_FILE);
            if (!matchFile.exists()) matchFile.createNewFile();

            File newsFile = new File(NEWS_FILE);
            if (!newsFile.exists()) newsFile.createNewFile();

        } catch (IOException e) {
            System.out.println("❌ Error creando archivos de eventos: " + e.getMessage());
        }
    }

    private static void handleMatchMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String json = ((TextMessage) message).getText();
                appendToFile(MATCH_FILE, json);
                System.out.println("✅ Partido guardado en archivo.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error procesando partido: " + e.getMessage());
        }
    }

    private static void handleNewsMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String json = ((TextMessage) message).getText();
                appendToFile(NEWS_FILE, json);
                System.out.println("📰 Noticia guardada en archivo.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error procesando noticia: " + e.getMessage());
        }
    }

    private static void appendToFile(String filePath, String jsonLine) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(jsonLine);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("❌ Error escribiendo en archivo: " + filePath);
            e.printStackTrace();
        }
    }
}
