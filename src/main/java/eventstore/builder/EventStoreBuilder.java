package eventstore.builder;

import javax.jms.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class EventStoreBuilder {

    public static void startListener(String topicName) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("EventStore-" + topicName); // durable subscriber
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, topicName + "-sub");

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        String json = ((TextMessage) message).getText();
                        saveEventToFile(topicName, json);
                    } catch (Exception e) {
                        System.out.println("❌ Error procesando mensaje: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("❌ Error en suscripción: " + e.getMessage());
        }
    }

    private static void saveEventToFile(String topic, String json) throws IOException {
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(json, JsonObject.class);
        String ss = obj.get("ss").getAsString();
        String ts = obj.get("ts").getAsString();

        String date = LocalDate.parse(ts.substring(0, 10)).format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD

        Path dir = Paths.get("eventstore", topic, ss);
        Files.createDirectories(dir);

        Path file = dir.resolve(date + ".events");
        Files.write(file, (json + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        System.out.println("📝 Evento guardado: " + file);
    }
}
