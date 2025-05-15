public class Match {
    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private String matchDate;
    private int matchday;

    public Match(String matchId, String homeTeam, String awayTeam, String matchDate, int matchday) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchday = matchday;
    }

    // Constructor sin matchday (por compatibilidad si lo necesitas)
    public Match(String matchId, String homeTeam, String awayTeam, String matchDate) {
        this(matchId, homeTeam, awayTeam, matchDate, -1); // -1 = no definido
    }

    public String getMatchId() {
        return matchId;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public int getMatchday() {
        return matchday;
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId='" + matchId + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", matchDate='" + matchDate + '\'' +
                ", matchday=" + matchday +
                '}';
    }
}
