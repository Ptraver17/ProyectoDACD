package feeder.news;

import java.util.Timer;
import java.util.TimerTask;

public class NewsFeederApp {
    private static final String LEAGUE_CODE = "PD";
    private static final int MATCHDAY = 30;

    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                NewsFeeder.sendNewsForMatchday(LEAGUE_CODE, MATCHDAY);
            }
        };

        long period = 6 * 60 * 60 * 1000;
        timer.scheduleAtFixedRate(task, 0, period);
    }
}
