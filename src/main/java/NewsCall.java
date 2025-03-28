import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsCall {
    private static final String API_KEY = "48b37eeb67cd4636a3d3285496f2cd98";

    public static void fetchNewsForMatch(String matchId, String homeTeam, String awayTeam) {
        String query = homeTeam + " " + awayTeam;
        String API_URL = "https://newsapi.org/v2/everything?q=" + query + "&language=es&apiKey=" + API_KEY;

        try {
            Document doc = Jsoup.connect(API_URL).ignoreContentType(true).get();
            String jsonResponse = doc.body().text();

            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.optString("status").equalsIgnoreCase("ok")) {
                JSONArray articles = jsonObject.getJSONArray("articles");
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.getJSONObject(i);
                    String title = article.optString("title", "Título no disponible");
                    String description = article.optString("description", "Descripción no disponible");
                    String url = article.optString("url", "URL no disponible");

                    DatabaseManager.insertNews(matchId, title, description, url);
                }
                System.out.println("Noticias insertadas para el partido " + homeTeam + " vs " + awayTeam);
            } else {
                System.out.println("Error en la API de noticias: " + jsonObject.optString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
