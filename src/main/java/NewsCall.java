import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NewsCall {
    private static final String API_URL = "https://newsapi.org/v2/everything?q=Las Palmas Alavés&searchin=Title&language=es&apiKey=60c3cdc6607142a98ead5ec6654db6ed";
    public static void main(String[] args) {
        try{
            Document doc = Jsoup.connect(API_URL).ignoreContentType(true).get();
            String jsonResponse = doc.body().text();
            System.out.println(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



//https://newsapi.org/v2/everything?q=real_madrid&language=es&apiKey=60c3cdc6607142a98ead5ec6654db6ed