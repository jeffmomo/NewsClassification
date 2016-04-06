package Util;

import FeatureExtraction.FeatureDocument;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by JJ on 01-11-2015.
 */
public class DataStandardizer {
    public static DataStandardizerResult stdNormalization(Set<FeatureDocument> featureDocuments){
        Map<String, List<Double>> aggregatedMaps = aggregateMaps(featureDocuments);
        Map<String, Double> mean = calculateMean(aggregatedMaps);
        Map<String, Double> variance = calculateVariance(aggregatedMaps, mean);

        Set<FeatureDocument> standardizedSet = featureDocuments.stream()
                .map(fd -> {
                    Map<String, Double> normalizedFeatureMap = new HashMap<>();
                    fd.getFeatures().forEach((s, d) -> normalizedFeatureMap.put(s, (d - mean.get(s))/variance.get(s)));
                    return new FeatureDocument(normalizedFeatureMap,fd.getShareAmount(), fd.getBucketNumber());
                }).collect(Collectors.toSet());

        return new DataStandardizerResult(standardizedSet,mean,variance);

    }

    private static Map<String, List<Double>> aggregateMaps(Set<FeatureDocument> featureDocuments){
        return featureDocuments.parallelStream()
                .map(FeatureDocument::getFeatures)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey
                        ,Collectors.mapping(Map.Entry::getValue
                        ,Collectors.toList())));
    }

    private static Map<String, Double> calculateVariance(Map<String, List<Double>> aggregatedMap, Map<String, Double> meanMap){
        Map<String, Double> variance = new HashMap<>();
        aggregatedMap.forEach(
                (s, dList) -> variance.put(s,
                        dList.stream()
                                .mapToDouble(d -> Math.pow(d - meanMap.get(s), 2))
                                .reduce(0., Double::sum)));

        variance.forEach((s, d) -> variance.put(s, Math.sqrt(variance.get(s) / aggregatedMap.get(s).size())));

        for(Map.Entry<String, Double> entry : variance.entrySet()){
            if(Double.isNaN(entry.getValue()) || Double.isInfinite(entry.getValue())  || entry.getValue() == 0.)
                System.out.println("OMG SHIT IN VARIANCE. Most likely issue is that one of the common nouns are not represented in the data set");
        }

        return variance;
    }

    private static Map<String, Double> calculateMean(Map<String, List<Double>> aggregatedMap){
        Map<String, Double> mean = new HashMap<>();
        aggregatedMap.forEach((s, dList) -> mean.put(s, dList.stream().reduce(0., Double::sum)));
        mean.forEach((s, d) -> mean.put(s, mean.get(s) / aggregatedMap.get(s).size()));

        for(Map.Entry<String, Double> entry : mean.entrySet()){
            if(Double.isNaN(entry.getValue()) || Double.isInfinite(entry.getValue()))
                System.out.println("OMG SHIT IN MEAN");
        }
        return mean;
    }
}