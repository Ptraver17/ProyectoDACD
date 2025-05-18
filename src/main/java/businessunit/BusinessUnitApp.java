package businessunit;

import Common.Match;
import Common.NewsItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class BusinessUnitApp {
    private static final String MATCH_FILE = "eventstore/matches.events";
    private static final String NEWS_FILE = "eventstore/news.events";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            showMenu();
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> showMatchesByLeagueAndMatchday(scanner);
                case "2" -> showNewsByMatchId(scanner);
                case "3" -> {
                    running = false;
                    System.out.println("👋 Cerrando aplicación...");
                }
                default -> System.out.println("❌ Opción inválida.");
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\n📋 Menú:");
        System.out.println("1. Ver partidos por liga y jornada");
        System.out.println("2. Ver noticias por Match ID");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void showMatchesByLeagueAndMatchday(Scanner scanner) {
        System.out.print("🏆 Ingrese código de liga (PD, PL, SA, BL1, FL1): ");
        String league = scanner.nextLine().toUpperCase();

        System.out.print("🔢 Ingrese número de jornada: ");
        try {
            int matchday = Integer.parseInt(scanner.nextLine());
            List<Match> matches = readMatchesFromFile().stream()
                    .filter(m -> m.getLeague().equalsIgnoreCase(league) && m.getMatchday() == matchday)
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                System.out.println("⚠️ No hay partidos disponibles.");
            } else {
                System.out.println("\n📅 Partidos:");
                for (Match m : matches) {
                    System.out.println(" - " + m.getMatchId() + ": " + m.getHomeTeam() + " vs " + m.getAwayTeam() + " [" + m.getMatchDate() + "]");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Jornada inválida.");
        }
    }

    private static void showNewsByMatchId(Scanner scanner) {
        System.out.print("🆔 Ingrese el Match ID: ");
        String matchId = scanner.nextLine();

        List<NewsItem> newsList = readNewsFromFile().stream()
                .filter(n -> n.getMatchId().equals(matchId))
                .collect(Collectors.toList());

        if (newsList.isEmpty()) {
            System.out.println("⚠️ No hay noticias para este partido.");
        } else {
            System.out.println("\n📰 Noticias:");
            for (NewsItem n : newsList) {
                System.out.println(" - " + n.getTitle());
                System.out.println("   " + n.getDescription());
                System.out.println("   " + n.getUrl());
            }
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
            System.out.println("❌ Error leyendo archivo de partidos: " + e.getMessage());
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
            System.out.println("❌ Error leyendo archivo de noticias: " + e.getMessage());
        }

        return news;
    }
}
