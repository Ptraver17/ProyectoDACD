import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;
import org.json.JSONArray;

public class NewsCall {
    private static final String API_URL = "https://newsapi.org/v2/everything?q=Las+Palmas+Alav%C3%A9s&searchIn=title&language=es&apiKey=60c3cdc6607142a98ead5ec6654db6ed";

    public static void main(String[] args) {
        try {
            // Realizamos la conexión y obtenemos la respuesta en formato JSON
            Document doc = Jsoup.connect(API_URL).ignoreContentType(true).get();
            String jsonResponse = doc.body().text();

            // Parseamos la respuesta JSON
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Verificamos que el estado de la respuesta sea 'ok'
            if (jsonObject.optString("status").equalsIgnoreCase("ok")) {
                // Obtenemos el array de artículos
                JSONArray articles = jsonObject.getJSONArray("articles");

                // Recorremos el array e imprimimos algunos campos de cada noticia
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.getJSONObject(i);
                    String title = article.optString("title", "Título no disponible");
                    String description = article.optString("description", "Descripción no disponible");
                    String url = article.optString("url", "URL no disponible");

                    System.out.println("Title: " + title);
                    System.out.println("Description: " + description);
                    System.out.println("URL: " + url);
                    System.out.println("----------------------------------");
                }
            } else {
                System.out.println("Error en la respuesta de la API: " + jsonObject.optString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
