import FeatureExtraction.FeatureDocument;
import FeatureExtraction.FeatureExtractor;
import Scraper.*;
import Util.DataCleaner;
import Util.DataStandardizer;
import Util.DataStandardizerResult;
import Util.LocalIO;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JJ on 18-10-2015.
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20"); //This is basically how many threads we use when multithreading.

        List<String> categories = Arrays.asList("singapore", "politics", "asia", "world", "lifestyle", "business", "sport", "tech");

        Scraper scraper = new Scraper();
        Set<NewsDocument> allArticles = scraper.getDocuments(0, 300, Arrays.asList("singapore", "politics", "asia", "world", "lifestyle", "business", "sport", "tech"));

        allArticles = DataCleaner.filterData(allArticles);

        Set<FeatureDocument> featureDocuments = FeatureExtractor.extractFeatures(allArticles, categories, LocalIO.readLinesAsStrings("ListOfNounsForFrequencyAnalyses.txt"));
        List<FeatureDocument> featureList = new ArrayList<>(featureDocuments);

        Collections.shuffle(featureList);
        LocalIO.writeFeaturesToTextFile(new HashSet<>(featureList.subList(0,5000)), "Test_set.txt");
        LocalIO.writeFeaturesToTextFile(new HashSet<>(featureList.subList(5000,featureList.size())), "Training_set.txt");

    }
}
