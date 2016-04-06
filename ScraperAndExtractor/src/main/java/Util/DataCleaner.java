package Util;

import Scraper.NewsDocument;
import org.jsoup.Jsoup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JJ on 19-10-2015.
 */
public class DataCleaner {
    public static String removeHTMLTags(String text)
    {
        return Jsoup.parse(text).text();
    }

    public static Set<NewsDocument> filterData(Set<NewsDocument> documents){
        return documents.parallelStream()
                .filter(newsDocument -> newsDocument.getAge() > 0)
                .filter(newsDocument -> newsDocument.getAge() < (long)1000 * 3600 * 24 * 110)
                .collect(Collectors.toSet());
    }

    public static BinningResult binSharesEqlFreq(int numBins, Set<NewsDocument> articles)
    {
        int binSize = (int)Math.ceil(articles.size() / numBins);

        List<NewsDocument> sortedList = articles.stream()
                .sorted((o1, o2) -> Integer.compare(o1.getShares(),o2.getShares()))
                .collect(Collectors.toList());

        HashMap<NewsDocument, Integer> binningMap = new HashMap<>();
        HashMap<NewsDocument, Integer> binningIDMap = new HashMap<>();

        int binNumber = 1;
        for (int start = 0; start < sortedList.size(); start += binSize) {

            int end = Math.min(start + binSize, sortedList.size());
            List<NewsDocument> bin = sortedList.subList(start, end);

            int midpoint = bin.size() / 2;
            int med = binSize % 2 == 0 ? (bin.get(midpoint).getShares() + bin.get(midpoint + 1).getShares()) / 2 : bin.get(midpoint).getShares();

            for(NewsDocument newsDocument : bin){
                binningMap.put(newsDocument, med);
                binningIDMap.put(newsDocument, binNumber);
            }

            binNumber++;
        }

        return new BinningResult(binningMap,binningIDMap);
    }
}
