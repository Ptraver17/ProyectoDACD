public class FootballFeederApp {
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando FootballFeederApp...");

        try {
            // Ejecutar el feeder para obtener partidos y enviarlos como mensajes
            FootballFeeder.main(args);
            System.out.println("✅ Partidos enviados correctamente.");
        } catch (Exception e) {
            System.out.println("❌ Error al ejecutar FootballFeederApp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
