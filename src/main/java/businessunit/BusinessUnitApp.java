package businessunit;

import common.Match;
import common.NewsItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class BusinessUnitApp {
    private static final String MATCH_FILE = "eventstore/matches.events";
    private static final String NEWS_FILE = "eventstore/news.events";
    private static final Set<String> KEYWORDS = Set.of("derbi", "crisis", "lesión", "estrella", "penalti", "remontada");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            showMenu();
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> showMatchesByLeagueAndMatchday(scanner);
                case "2" -> showNewsByMatchId(scanner);
                case "3" -> showImpactRanking(scanner);
                case "4" -> {
                    running = false;
                    System.out.println("Cerrando aplicación...");
                }
                default -> System.out.println("Opción no válida.");
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\nMenú:");
        System.out.println("1. Ver partidos por liga y jornada");
        System.out.println("2. Ver noticias por Match ID");
        System.out.println("3. Ver ranking de impacto por jornada");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void showMatchesByLeagueAndMatchday(Scanner scanner) {
        System.out.print("Ingrese código de liga (PD, PL, SA, BL1, FL1): ");
        String league = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese número de jornada: ");
        try {
            int matchday = Integer.parseInt(scanner.nextLine());
            List<Match> matches = readMatchesFromFile().stream()
                    .filter(m -> m.getLeague().equalsIgnoreCase(league) && m.getMatchday() == matchday)
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                System.out.println("No hay partidos disponibles.");
            } else {
                System.out.println("Partidos:");
                for (Match m : matches) {
                    System.out.println(" - " + m.getMatchId() + ": " + m.getHomeTeam() + " vs " + m.getAwayTeam() + " [" + m.getMatchDate() + "]");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Jornada inválida.");
        }
    }

    private static void showNewsByMatchId(Scanner scanner) {
        System.out.print("Ingrese el Match ID: ");
        String matchId = scanner.nextLine();

        List<NewsItem> newsList = readNewsFromFile().stream()
                .filter(n -> n.getMatchId().equals(matchId))
                .collect(Collectors.toList());

        if (newsList.isEmpty()) {
            System.out.println("No hay noticias para este partido.");
        } else {
            System.out.println("Noticias:");
            for (NewsItem n : newsList) {
                System.out.println(" - " + n.getTitle());
                System.out.println("   " + n.getDescription());
                System.out.println("   " + n.getUrl());
            }
        }
    }

    private static void showImpactRanking(Scanner scanner) {
        System.out.print("Ingrese código de liga: ");
        String league = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese número de jornada: ");
        try {
            int matchday = Integer.parseInt(scanner.nextLine());

            List<Match> matches = readMatchesFromFile().stream()
                    .filter(m -> m.getLeague().equalsIgnoreCase(league) && m.getMatchday() == matchday)
                    .collect(Collectors.toList());

            List<NewsItem> news = readNewsFromFile();

            Map<String, Double> impactMap = new HashMap<>();

            for (Match match : matches) {
                List<NewsItem> relatedNews = news.stream()
                        .filter(n -> n.getMatchId().equals(match.getMatchId()))
                        .toList();

                int newsCount = relatedNews.size();
                int keywordHits = 0;

                for (NewsItem item : relatedNews) {
                    String combined = (item.getTitle() + " " + item.getDescription()).toLowerCase();
                    for (String keyword : KEYWORDS) {
                        if (combined.contains(keyword)) {
                            keywordHits++;
                        }
                    }
                }

                double score = newsCount * 1.0 + keywordHits * 0.5;
                impactMap.put(match.getMatchId(), score);
            }

            List<Map.Entry<String, Double>> ranking = impactMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .toList();

            System.out.println("\nRanking de impacto (mayor a menor):");
            for (Map.Entry<String, Double> entry : ranking) {
                Match match = matches.stream()
                        .filter(m -> m.getMatchId().equals(entry.getKey()))
                        .findFirst()
                        .orElse(null);

                if (match != null) {
                    System.out.printf(" - %s vs %s (ID: %s): %.2f\n",
                            match.getHomeTeam(), match.getAwayTeam(), match.getMatchId(), entry.getValue());
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Jornada inválida.");
        }
    }

    private static List<Match> readMatchesFromFile() {
        List<Match> matches = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new FileReader(MATCH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                matches.add(mapper.readValue(line, Match.class));
            }
        } catch (Exception e) {
            System.out.println("Error leyendo archivo de partidos: " + e.getMessage());
        }

        return matches;
    }

    private static List<NewsItem> readNewsFromFile() {
        List<NewsItem> news = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new FileReader(NEWS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                news.add(mapper.readValue(line, NewsItem.class));
            }
        } catch (Exception e) {
            System.out.println("Error leyendo archivo de noticias: " + e.getMessage());
        }

        return news;
    }
}
