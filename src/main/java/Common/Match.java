package Common;

public class Match {
    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private String matchDate;

    public Match(String matchId, String homeTeam, String awayTeam, String matchDate) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
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
}

