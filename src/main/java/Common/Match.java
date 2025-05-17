package Common;

import java.io.Serializable;

public class Match implements Serializable {
    private static final long serialVersionUID = 1L;

    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private String matchDate;
    private int matchday;
    private String league;

    public Match(String matchId, String homeTeam, String awayTeam, String matchDate, int matchday, String league) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchday = matchday;
        this.league = league;
    }

    public Match(String matchId, String homeTeam, String awayTeam, String matchDate, int matchday) {
        this(matchId, homeTeam, awayTeam, matchDate, matchday, ""); // "" si no se indica
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

    public String getLeague() {
        return league;
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId='" + matchId + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", matchDate='" + matchDate + '\'' +
                ", matchday=" + matchday +
                ", league='" + league + '\'' +
                '}';
    }
}
