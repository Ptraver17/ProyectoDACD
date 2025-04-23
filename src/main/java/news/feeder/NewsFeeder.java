package news.feeder;

import com.google.gson.Gson;

import java.time.Instant;

public class NewsFeeder {
    public static void publishNewsEvent(String matchId, String title, String description, String url) {
        NewsEvent event = new NewsEvent(
                Instant.now().toString(),
                "news.feeder.NewsFeeder",
                matchId,
                title,
                description,
                url
        );

        String json = new Gson().toJson(event);
        FeederPublisher.send(json, "prediction.News");
    }
}
