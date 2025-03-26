import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsCall {
    private static final String API_URL = "https://newsapi.org/v2/everything?q=Las+Palmas+Alav%C3%A9s&searchIn=title&language=es&apiKey=60c3cdc6607142a98ead5ec6654db6ed";

    public static void fetchNewsData() {
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

                    DatabaseManager.insertNewsData(title, description, url);
                }
                System.out.println("Noticias insertadas en la base de datos.");
            } else {
                System.out.println("Error en la respuesta de NewsAPI: " + jsonObject.optString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
