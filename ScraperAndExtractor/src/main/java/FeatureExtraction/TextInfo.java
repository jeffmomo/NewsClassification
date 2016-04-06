package FeatureExtraction;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import org.languagetool.tagging.en.EnglishTagger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JJ on 19-10-2015.
 */
public class TextInfo {
    public GrammarErrorCount getGrammarErrors(String text){
        JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
        List<RuleMatch> matches = null;
        try {
            matches = langTool.check(text);
        } catch (Exception e) {
            System.out.print(e.toString());
        }

        GrammarErrorCount grammarCount = new GrammarErrorCount();
        for (RuleMatch match : matches) {
            grammarCount.inc(match.getRule().getCategory().getName());
        }

        return grammarCount;
    }

    public POSTagCnt getPOSTagCnt(String text, POSModel model)
    {
        String wordString = text.replaceAll("[^a-zA-Z0-9 ]", "");
        String[] words = wordString.split("\\s+");

        POSTaggerME tagger = new POSTaggerME(model);
        String[] tagOutputs = tagger.tag(words);
        POSTagCnt POSCnt = new POSTagCnt();

        for (int i = 0; i < tagOutputs.length; i++) {
            String posTag = tagOutputs[i];
            if (posTag.equals("."))
                POSCnt.inc("null");
            else
            {
                if(posTag.contains("NN"))
                    POSCnt.inc("NN");
                else if(posTag.contains("VB"))
                    POSCnt.inc("VB");
                else if (posTag.equals("DT"))
                    POSCnt.inc("DT");
                else if (posTag.contains("JJ"))
                    POSCnt.inc("JJ");
                else if (posTag.contains("RB"))
                    POSCnt.inc("RB");
                else
                    POSCnt.inc("other");
            }
        }

        return POSCnt;
    }

    public int getWordCount(String text){
        return text.split("\\s+").length;
    }

    public int getAvgWordLength(String text){
        String tempText = text.replaceAll("[\\.:,@\"()-]", "");
        String[] words = tempText.split("\\s+");
        int length = 0;
        for (String s : words){
            length += s.length();
            //System.out.println("\"" + s + "\"");
        }
        return (int)((float)length / words.length + 0.5);
    }

    public int getAvgSentenceLength(String text){
        String tempText = text.replaceAll("[,:@\"()-]", "");
        String[] words = tempText.split("\\.");
        int length = 0;
        for (String s : words){
            s = s.replaceAll("\\s+", " "); // Trim excessive blank spaces
            s = s.replaceAll("^\\s+|\\s+$", ""); // Remove space at start and end of sentences
            if (s.length()<=1)
                continue;
            length += s.length();
            //System.out.println("\"" + s + "\"");
        }
        return (int)((float)length / words.length + 0.5);
    }
}
