package businessunit;

import Common.DatabaseManager;
import Common.Match;
import Common.NewsItem;

import java.util.List;
import java.util.Scanner;

public class BusinessUnitApp {
    public static void main(String[] args) {
        DatabaseManager.createTables();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n📋 Menú:");
            System.out.println("1. Ver partidos por liga y jornada");
            System.out.println("2. Ver noticias por Match ID");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
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

    private static void showMatchesByLeagueAndMatchday(Scanner scanner) {
        System.out.print("🏆 Ingrese código de liga (PD, PL, SA, BL1, FL1): ");
        String league = scanner.nextLine().toUpperCase();

        System.out.print("🔢 Ingrese el número de jornada: ");
        try {
            int matchday = Integer.parseInt(scanner.nextLine());
            List<Match> matches = DatabaseManager.getMatchesByLeagueAndMatchday(league, matchday);

            if (matches.isEmpty()) {
                System.out.println("⚠️ No hay partidos para esta liga y jornada.");
            } else {
                System.out.println("\n📅 Partidos de " + league + " - Jornada " + matchday + ":");
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

        List<NewsItem> newsList = DatabaseManager.getAllNews();
        boolean found = false;

        System.out.println("\n📰 Noticias para el partido " + matchId + ":");
        for (NewsItem news : newsList) {
            if (news.getMatchId().equals(matchId)) {
                System.out.println(" - " + news.getTitle());
                System.out.println("   " + news.getDescription());
                System.out.println("   " + news.getUrl());
                found = true;
            }
        }

        if (!found) {
            System.out.println("⚠️ No hay noticias asociadas a este partido.");
        }
    }
}
