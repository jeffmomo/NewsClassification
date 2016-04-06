package Scraper;

import FeatureExtraction.FeatureDocument;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JJ on 21-10-2015.
 */
public class ShareTrends {
    public static Set<FeatureDocument> extractShareTrends(Set<NewsDocument> articles){
        List<String> categories = Arrays.asList("singapore", "politics", "asia", "world", "lifestyle", "business", "sport", "tech");
        return articles.stream()
                .map(article -> {
                    Map<String, Double> features = new HashMap<String, Double>();

                    for (String category : categories){
                        if (article.getSection().equalsIgnoreCase(category))
                            features.put("CAT" + category, 1.);
                        else
                            features.put("CAT" + category, 0.);
                    }

                    features.put("age (hours)", (double) article.getAge() / (1000 * 3600));
                    return new FeatureDocument(features,article.getShares());
                })
                .collect(Collectors.toSet());
    }
}
