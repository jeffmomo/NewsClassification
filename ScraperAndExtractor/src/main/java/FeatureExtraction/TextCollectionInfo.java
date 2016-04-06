package FeatureExtraction;

import Scraper.NewsDocument;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.en.EnglishTagger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by JJ on 19-10-2015.
 */
public class TextCollectionInfo {
    public static List<Map.Entry<String, Integer>> findMostCommonNouns(int amount, Set<NewsDocument> articles) {

        Map<String, Integer> frequencyOfWord = articles.parallelStream()
                .map(article -> {
                    Map<String, Integer> articleFrequencyMap = mapNounFrequency(article.getHeading());
                    mapNounFrequency(article.getText())
                            .forEach((k, v) -> articleFrequencyMap.merge(k, v, Integer::sum));

                    return articleFrequencyMap.entrySet();
                })
                .flatMap(Collection::stream)
                .collect(
                        Collectors.toConcurrentMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Integer::sum
                        ));


        //Sort and pick the amount best sorted
        List<Map.Entry<String,Integer>> sortedList = frequencyOfWord
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .skip(frequencyOfWord.size() - (frequencyOfWord.size() - amount) < amount ?0:(frequencyOfWord.size() - amount))
                .collect(Collectors.toList());

        return sortedList;
    }

    public static Map<String, Integer> mapNounFrequency(String text){
        Map<String, Integer> result = new HashMap<>();

        String wordString = text.replaceAll("[\\.,\"()]", "");
        wordString = wordString.toLowerCase();
        List<String> words = Arrays.asList(wordString.split("\\s* \\s*"));

        EnglishTagger tag = new EnglishTagger();
        List<AnalyzedTokenReadings> POS = null;
        try {
            POS = tag.tag(words);
        } catch (Exception e) {
            System.out.print("Part Of Speech tagging error: " + e);
        }

        for (int i = 0; i < POS.size(); i++) {
            String posTag = POS.get(i).getAnalyzedToken(0).getPOSTag();
            String token = POS.get(i).getAnalyzedToken(0).getToken();

            if (posTag != null && posTag.contains("NN")){
                result.put(token,result.getOrDefault(token, 0) + 1);
                    if(token.charAt(token.length() - 1) == 's'){
                        result.put(token.substring(0, token.length() -1),result.getOrDefault(token, 0) + 1);
                    }
            }
        }

        return result;
    }

}
