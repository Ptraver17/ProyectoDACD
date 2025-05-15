import java.io.Serializable;

public class FootballEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EventType {
        MATCH,
        NEWS
    }

    private EventType type;
    private Match match;
    private NewsItem news;

    public FootballEvent(Match match) {
        this.type = EventType.MATCH;
        this.match = match;
    }

    public FootballEvent(NewsItem news) {
        this.type = EventType.NEWS;
        this.news = news;
    }

    public EventType getType() {
        return type;
    }

    public Match getMatch() {
        return match;
    }

    public NewsItem getNews() {
        return news;
    }

    @Override
    public String toString() {
        return "FootballEvent{" +
                "type=" + type +
                ", match=" + match +
                ", news=" + news +
                '}';
    }
}
