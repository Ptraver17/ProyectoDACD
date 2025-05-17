package news.feeder;

import java.io.Serializable;

public class NewsEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String matchId;
    private String title;
    private String description;
    private String url;

    public NewsEvent(String matchId, String title, String description, String url) {
        this.matchId = matchId;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "NewsEvent{" +
                "matchId='" + matchId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
