package FeatureExtraction;

import java.util.HashMap;
import java.util.Map;

public class POSTagCnt {
    Map<String, Integer> map;
    Map<String, Integer> mapDublicates;
    int cnt[];

    public POSTagCnt(){
        map = new HashMap<String, Integer>();
        mapDublicates = new HashMap<String, Integer>();

        map.put("null", 0);
        map.put("other", 1);
        map.put("NN", 2);
        map.put("VB", 3);
        map.put("DT", 4);
        map.put("JJ", 5);
        map.put("RB", 6);

        mapDublicates.put("NN:UN", map.get("NN"));
        mapDublicates.put("NN:U", map.get("NN"));

        cnt = new int[map.size()];
    }

    public int getNull() { return cnt[map.get("null")]; }
    public int getOther() { return cnt[map.get("other")]; }
    public int getNN() { return cnt[map.get("NN")]; }
    public int getVB() { return cnt[map.get("VB")]; }
    public int getDT() { return cnt[map.get("DT")]; }
    public int getJJ() { return cnt[map.get("JJ")]; }
    public int getRB() { return cnt[map.get("RB")]; }

    public void inc(String tag) {
        if (map.containsKey(tag))
            cnt[map.get(tag)]++;
        else if (mapDublicates.containsKey(tag))
            cnt[mapDublicates.get(tag)]++;
        else
            cnt[map.get("other")]++;
    }

    public String ToString(){
        String n = System.getProperty("line.separator");
        String text = "";
        for (String s : map.keySet()){
            text += s + ": " + cnt[map.get(s)] + n;
        }
        return text;
    }
}
