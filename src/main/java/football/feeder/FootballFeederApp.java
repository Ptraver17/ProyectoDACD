package football.feeder;

public class FootballFeederApp {
    public static void main(String[] args) {
        String leagueCode = "PD";
        int matchday = 30;

        if (args.length > 0) {
            leagueCode = args[0].toUpperCase();
        }

        if (args.length > 1) {
            try {
                matchday = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                matchday = 30;
            }
        }

        FootballFeeder.sendMatchesForMatchday(leagueCode, matchday);
    }
}
