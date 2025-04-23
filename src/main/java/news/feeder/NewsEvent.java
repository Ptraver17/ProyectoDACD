package news.feeder;

public class NewsEvent {
    private String ts;
    private String ss;
    private String matchId;
    private String title;
    private String description;
    private String url;

    public NewsEvent(String ts, String ss, String matchId, String title, String description, String url) {
        this.ts = ts;
        this.ss = ss;
        this.matchId = matchId;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
