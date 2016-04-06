package FeatureExtraction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JJ on 19-10-2015.
 */
public class FeatureDocument implements Serializable {
    private Map<String, Double> features = new HashMap<>();
    private int shareAmount;
    private int bucketNumber = -1;

    public FeatureDocument(Map<String, Double> features, int shareAmount, int bucketNumber) {
        this.features = features;
        this.shareAmount = shareAmount;
        this.bucketNumber = bucketNumber;
    }

    public int getBucketNumber() {
        return bucketNumber;
    }

    public void setBucketNumber(int bucketNumber) {
        this.bucketNumber = bucketNumber;
    }

    public FeatureDocument(Map<String, Double> features, int shareAmount) {
        this.features = features;
        this.shareAmount = shareAmount;
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Double> features) {
        this.features = features;
    }

    public int getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(int shareAmount) {
        this.shareAmount = shareAmount;
    }
}
