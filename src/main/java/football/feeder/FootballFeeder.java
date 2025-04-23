package football.feeder;

import com.google.gson.Gson;
import java.time.Instant;


public class FootballFeeder {
    public static void publishMatchEvent(Match match) {
        FootballEvent event = new FootballEvent(
                Instant.now().toString(),
                "football.feeder.FootballFeeder",
                match.getMatchId(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getMatchDate()
        );

        String json = new Gson().toJson(event);
        FeederPublisher.send(json, "prediction.Football");
    }
}
