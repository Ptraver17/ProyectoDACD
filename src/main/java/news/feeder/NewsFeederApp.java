public class NewsFeederApp {
    public static void main(String[] args) {
        int matchday = 30; // Valor por defecto

        if (args.length > 0) {
            try {
                matchday = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Jornada inválida, usando jornada por defecto: " + matchday);
            }
        }

        System.out.println("🚀 Enviando noticias de la jornada " + matchday + "...");
        NewsFeeder.sendNewsForMatchday(matchday);
        System.out.println("✅ Envío completado.");
    }
}
