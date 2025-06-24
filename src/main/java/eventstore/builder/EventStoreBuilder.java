package eventstore;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class EventStoreBuilder {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String FOOTBALL_TOPIC = "football.matches";
    private static final String NEWS_TOPIC = "news.events";

    private static final String MATCH_FILE = "eventstore/matches.events";
    private static final String NEWS_CSV_FILE = "eventstore/news.csv";

    private static final Set<String> newsKeys = new HashSet<>();

    public static void main(String[] args) {
        ensureStorage();

        loadExistingNewsKeys();

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic footballTopic = session.createTopic(FOOTBALL_TOPIC);
            MessageConsumer footballConsumer = session.createConsumer(footballTopic);
            footballConsumer.setMessageListener(EventStoreBuilder::handleMatchMessage);

            Topic newsTopic = session.createTopic(NEWS_TOPIC);
            MessageConsumer newsConsumer = session.createConsumer(newsTopic);
            newsConsumer.setMessageListener(EventStoreBuilder::handleNewsMessage);

            System.out.println("EventStoreBuilder escuchando eventos...");
        } catch (Exception e) {
            System.out.println("Error en EventStoreBuilder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ensureStorage() {
        try {
            File dir = new File("eventstore");
            if (!dir.exists()) dir.mkdirs();

            File matchFile = new File(MATCH_FILE);
            if (!matchFile.exists()) matchFile.createNewFile();

            File newsFile = new File(NEWS_CSV_FILE);
            if (!newsFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(newsFile))) {
                    writer.write("matchId,title,description,url");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error creando archivos: " + e.getMessage());
        }
    }

    private static void loadExistingNewsKeys() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NEWS_CSV_FILE))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length > 0) {
                    newsKeys.add(parts[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar noticias existentes.");
        }
    }

    private static void handleMatchMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String json = textMessage.getText();
                appendToFile(MATCH_FILE, json);
                System.out.println("Partido guardado.");
            }
        } catch (Exception e) {
            System.out.println("Error procesando partido: " + e.getMessage());
        }
    }

    private static void handleNewsMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String json = textMessage.getText();

                String matchId = extractMatchId(json);
                if (!newsKeys.contains(matchId)) {
                    writeNewsToCSV(json, matchId);
                    newsKeys.add(matchId);
                    System.out.println("Noticia guardada.");
                } else {
                    System.out.println("Noticia ya almacenada. Se omite.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando noticia: " + e.getMessage());
        }
    }

    private static void writeNewsToCSV(String json, String matchId) {
        try {
            String title = extractJsonField(json, "title");
            String description = extractJsonField(json, "description");
            String url = extractJsonField(json, "url");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(NEWS_CSV_FILE, true))) {
                writer.write(escape(matchId) + "," + escape(title) + "," + escape(description) + "," + escape(url));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error escribiendo noticia en CSV.");
        }
    }

    private static String escape(String text) {
        if (text == null) return "";
        return "\"" + text.replace("\"", "'") + "\"";
    }

    private static String extractMatchId(String json) {
        return extractJsonField(json, "matchId");
    }

    private static String extractJsonField(String json, String field) {
        try {
            int start = json.indexOf("\"" + field + "\"");
            if (start == -1) return "";
            int colon = json.indexOf(":", start);
            int quote1 = json.indexOf("\"", colon + 1);
            int quote2 = json.indexOf("\"", quote1 + 1);
            return json.substring(quote1 + 1, quote2);
        } catch (Exception e) {
            return "";
        }
    }

    private static void appendToFile(String path, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error escribiendo en archivo: " + path);
        }
    }
}
