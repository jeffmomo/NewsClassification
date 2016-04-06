package Util;

import Scraper.NewsDocument;

import java.util.Map;

/**
 * Created by JJ on 05-11-2015.
 */
public class BinningResult {
    private Map<NewsDocument, Integer> binningValueMap;
    private Map<NewsDocument, Integer> binningIDMap;

    public BinningResult(Map<NewsDocument, Integer> binningValueMap, Map<NewsDocument, Integer> binningIDMap) {
        this.binningValueMap = binningValueMap;
        this.binningIDMap = binningIDMap;
    }

    public Map<NewsDocument, Integer> getBinningValueMap() {
        return binningValueMap;
    }

    public void setBinningValueMap(Map<NewsDocument, Integer> binningValueMap) {
        this.binningValueMap = binningValueMap;
    }

    public Map<NewsDocument, Integer> getBinningIDMap() {
        return binningIDMap;
    }

    public void setBinningIDMap(Map<NewsDocument, Integer> binningIDMap) {
        this.binningIDMap = binningIDMap;
    }
}
