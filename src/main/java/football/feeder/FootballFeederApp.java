package football.feeder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class FootballFeederApp {
    private static final String LEAGUE_CODE = "PD";
    private static final int MATCHDAY = 30;

    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                FootballFeeder.sendMatchesForMatchday(LEAGUE_CODE, MATCHDAY);
            }
        };

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(12).withMinute(0).withSecond(0).withNano(0);
        if (!now.isBefore(nextRun)) {
            nextRun = now.plusHours(12);
        }

        long initialDelay = ChronoUnit.MILLIS.between(now, nextRun);
        long testPeriod = 12 * 60 * 60 * 1000;

        timer.scheduleAtFixedRate(task, initialDelay, testPeriod);
    }
}