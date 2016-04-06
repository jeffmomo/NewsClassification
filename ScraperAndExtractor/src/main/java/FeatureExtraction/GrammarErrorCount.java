package FeatureExtraction;

import java.util.HashMap;
import java.util.Map;

public class GrammarErrorCount {
    Map<String, Integer> map;
    int cnt[];

    public GrammarErrorCount() {
        map = new HashMap<String, Integer>();

        map.put("other", 0);
        map.put("Miscellaneous", 1);
        map.put("Possible Typo", 2);
        map.put("Capitalization", 3);
        map.put("Grammar", 4);

        cnt = new int[map.size()];
    }

    public int getOther() { return cnt[map.get("other")]; }
    public int getMiscellaneous() { return cnt[map.get("Miscellaneous")]; }
    public int getPossibleTypo() { return cnt[map.get("Possible Typo")]; }
    public int getCapitalization() { return cnt[map.get("Capitalization")]; }
    public int getGrammar() { return cnt[map.get("Grammar")]; }

    public void inc(String tag) {
        if (map.containsKey(tag))
            cnt[map.get(tag)]++;
        else {
            cnt[map.get("other")]++;
            System.out.println(tag);
        }
    }

    public String ToString() {
        String n = System.getProperty("line.separator");
        String text = "";
        for (String s : map.keySet()) {
            text += s + ": " + cnt[map.get(s)] + n;
        }
        return text;
    }

}
