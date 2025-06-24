package businessunit;

import common.Match;
import common.NewsItem;

import java.util.*;
import java.util.stream.Collectors;

public class ImpactAnalyzer {
    private static final Set<String> KEYWORDS = Set.of("derbi", "crisis", "lesión", "estrella", "penalti", "remontada", "despedida", "fichaje", "compra", "venta");

    public static Map<Match, Double> calculateImpact(List<Match> matches, List<NewsItem> news) {
        Map<Match, Double> result = new HashMap<>();

        for (Match match : matches) {
            List<NewsItem> relatedNews = news.stream()
                    .filter(n -> n.getMatchId().equals(match.getMatchId()))
                    .collect(Collectors.toList());

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
            result.put(match, score);
        }

        return result.entrySet().stream()
                .sorted(Map.Entry.<Match, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
