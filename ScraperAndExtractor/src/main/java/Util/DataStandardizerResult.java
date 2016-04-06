package Util;

import FeatureExtraction.FeatureDocument;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by JJ on 05-11-2015.
 */
public class DataStandardizerResult implements Serializable {
    private Set<FeatureDocument> standardizedData;
    private Map<String, Double> meanMap;
    private Map<String, Double> variance;

    public DataStandardizerResult(Set<FeatureDocument> standardizedData, Map<String, Double> meanMap, Map<String, Double> variance) {
        this.standardizedData = standardizedData;
        this.meanMap = meanMap;
        this.variance = variance;
    }

    public Set<FeatureDocument> getStandardizedData() {
        return standardizedData;
    }

    public Map<String, Double> getMeanMap() {
        return meanMap;
    }

    public Map<String, Double> getVariance() {
        return variance;
    }
}
