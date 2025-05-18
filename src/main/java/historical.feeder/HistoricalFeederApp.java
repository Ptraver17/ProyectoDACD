package historical.feeder;

import Common.Match;
import Common.NewsItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HistoricalFeederApp {
    private static final String[] LEAGUES = {"PD", "PL", "SA", "BL1", "FL1"};
    private static final int START_MATCHDAY = 1;
    private static final int END_MATCHDAY = 30;
    private static final long DELAY_MS = 5000;

    private static final String MATCH_FILE = "eventstore/matches.events";
    private static final String NEWS_FILE = "eventstore/news.events";
    private static final String API_KEY = "943a1112a67c46758085602f97729bd9";
    private static final String NEWS_API_KEY = "60c3cdc6607142a98ead5ec6654db6ed";

    public static void main(String[] args) {
        ensureFilesExist();

        for (String league : LEAGUES) {
            System.out.println("🏆 Cargando liga: " + league);

            for (int matchday = START_MATCHDAY; matchday <= END_MATCHDAY; matchday++) {
                System.out.println("📦 Jornada " + matchday + " - " + league);

                try {
                    fetchAndSaveMatches(league, matchday);
                    Thread.sleep(2000);
                    fetchAndSaveNews(league, matchday);
                } catch (Exception e) {
                    System.out.println("❌ Error en jornada " + matchday + ": " + e.getMessage());
                }

                try {
                    Thread.sleep(DELAY_MS);
                } catch (InterruptedException e) {
                    System.out.println("⏹️ Cancelado por el usuario.");
                    return;
                }
            }
        }

        System.out.println("✅ Carga histórica completa.");
    }

    private static void ensureFilesExist() {
        try {
            File dir = new File("eventstore");
            if (!dir.exists()) dir.mkdirs();

            new File(MATCH_FILE).createNewFile();
            new File(NEWS_FILE).createNewFile();
        } catch (Exception e) {
            System.out.println("❌ Error creando archivos: " + e.getMessage());
        }
    }

    private static void fetchAndSaveMatches(String leagueCode, int matchday) throws Exception {
        String apiUrl = "https://api.football-data.org/v4/competitions/" + leagueCode + "/matches?matchday=" + matchday;

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Auth-Token", API_KEY);

        if (connection.getResponseCode() != 200) return;

        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder json = new StringBuilder();
        while (scanner.hasNext()) json.append(scanner.nextLine());
        scanner.close();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode matches = mapper.readTree(json.toString()).get("matches");

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(MATCH_FILE, true)))) {
            for (JsonNode match : matches) {
                String matchId = String.valueOf(match.get("id").asInt());
                String homeTeam = match.get("homeTeam").get("name").asText();
                String awayTeam = match.get("awayTeam").get("name").asText();
                String date = match.get("utcDate").asText();

                Match matchObj = new Match(matchId, homeTeam, awayTeam, date, matchday, leagueCode);
                writer.println(mapper.writeValueAsString(matchObj));
            }
        }

        System.out.println("✅ Partidos guardados.");
    }

    private static void fetchAndSaveNews(String leagueCode, int matchday) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(MATCH_FILE));
             PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(NEWS_FILE, true)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Match match = mapper.readValue(line, Match.class);
                if (!match.getLeague().equalsIgnoreCase(leagueCode) || match.getMatchday() != matchday) continue;

                String query = match.getHomeTeam() + " " + match.getAwayTeam();
                String url = "https://newsapi.org/v2/everything?q=" + query.replace(" ", "+") + "&language=es&apiKey=" + NEWS_API_KEY;

                try {
                    Document doc = Jsoup.connect(url).ignoreContentType(true).get();
                    String jsonResponse = doc.body().text();
                    JsonNode root = mapper.readTree(jsonResponse);
                    JsonNode articles = root.get("articles");

                    for (JsonNode article : articles) {
                        String title = article.get("title").asText();
                        String description = article.get("description").asText(null);
                        String link = article.get("url").asText();

                        NewsItem item = new NewsItem(match.getMatchId(), title, description, link);
                        writer.println(mapper.writeValueAsString(item));
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error buscando noticias para " + query);
                }

                Thread.sleep(500);
            }
        }

        System.out.println("📰 Noticias guardadas.");
    }
}

