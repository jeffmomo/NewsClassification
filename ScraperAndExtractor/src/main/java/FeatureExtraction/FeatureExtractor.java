package FeatureExtraction;

import Util.BinningResult;
import Util.DataCleaner;
import Scraper.NewsDocument;
import opennlp.tools.postag.POSModel;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JJ on 19-10-2015.
 */
public class FeatureExtractor {

    public static Set<FeatureDocument> extractFeatures(Set<NewsDocument> articles, List<String> categories, List<String> nounsForFrequency){
        POSModel posModelTmp = null;
        try(FileInputStream modelIn = new FileInputStream("en-pos-maxent.bin")) {
            posModelTmp = new POSModel(modelIn);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        final POSModel posModelFinal = posModelTmp;

        BinningResult binningInfo = DataCleaner.binSharesEqlFreq(30, articles);

        return articles.parallelStream()
                .map(newsDocument -> extractFeaturesOfDocument(newsDocument, categories, posModelFinal, nounsForFrequency, binningInfo))
                .collect(Collectors.toSet());
    }

    private static FeatureDocument extractFeaturesOfDocument(NewsDocument newsDocument, List<String> categories, POSModel posModel, List<String> nounsForFrequency, BinningResult binningInfo) {
        Map<String, Double> featureMap = new HashMap<>();

        String textContent = DataCleaner.removeHTMLTags(newsDocument.getText());
        String heading = DataCleaner.removeHTMLTags(newsDocument.getHeading());

        // Extract grammatical and part of speech information from text body
        TextInfo ti = new TextInfo();
        featureMap.put("TEXT_CONTENTnumWords", (double) ti.getWordCount(textContent));
        featureMap.put("TEXT_CONTENTavgWordLength", (double) ti.getAvgWordLength(textContent));
        featureMap.put("TEXT_CONTENTavgSenLength", (double) ti.getAvgSentenceLength(textContent));

        featureMap.put("HEADINGnumWords", (double) ti.getWordCount(heading));

        GrammarErrorCount grammarErrorCount = ti.getGrammarErrors(textContent);
        featureMap.put("TEXT_CONTENTgeOther", (double) grammarErrorCount.getOther());
        featureMap.put("TEXT_CONTENTgeMisc", (double) grammarErrorCount.getMiscellaneous());
        featureMap.put("TEXT_CONTENTgeTypo", (double) grammarErrorCount.getPossibleTypo());
        featureMap.put("TEXT_CONTENTgeCap", (double) grammarErrorCount.getCapitalization());
        featureMap.put("TEXT_CONTENTgeGram", (double) grammarErrorCount.getGrammar());

        POSTagCnt partOfSpeech = ti.getPOSTagCnt(textContent,  posModel);
        featureMap.put("TEXT_CONTENTposNull", (double) partOfSpeech.getNull());
        featureMap.put("TEXT_CONTENTposOther", (double) partOfSpeech.getOther());
        featureMap.put("TEXT_CONTENTposNN", (double) partOfSpeech.getNN());
        featureMap.put("TEXT_CONTENTposVB", (double) partOfSpeech.getVB());
        featureMap.put("TEXT_CONTENTposJJ", (double) partOfSpeech.getJJ());
        featureMap.put("TEXT_CONTENTposDT", (double) partOfSpeech.getDT());
        featureMap.put("TEXT_CONTENTposRB", (double) partOfSpeech.getRB());

        partOfSpeech = ti.getPOSTagCnt(heading,  posModel);
        featureMap.put("HEADINGposNull", (double) partOfSpeech.getNull());
        featureMap.put("HEADINGposOther", (double) partOfSpeech.getOther());
        featureMap.put("HEADINGposNN", (double) partOfSpeech.getNN());
        featureMap.put("HEADINGposVB", (double) partOfSpeech.getVB());
        featureMap.put("HEADINGposJJ", (double) partOfSpeech.getJJ());
        featureMap.put("HEADINGposDT", (double) partOfSpeech.getDT());
        featureMap.put("HEADINGposRB", (double) partOfSpeech.getRB());

        /* The sentiment client is slow and unstable. Also cannot be used with multithreadeding
        SentimentClient sentiment = new SentimentClient(mainText + " " +  heading);
        if(sentimentToDouble(sentiment.getSentiment()) != 0)
            featureMap.put("got_sentiment",0.);
        else
            featureMap.put("got_sentiment", 1.);
        featureMap.put("sentiment", sentimentToDouble(sentiment.getSentiment()));
        featureMap.put("subjectivity", subjectiveToDouble(sentiment.getSubjectivity()));
        featureMap.put("irony", ironyToDouble(sentiment.getIrony()));
        */

        featureMap.put("age (hours)", (double) newsDocument.getAge() / (1000 * 3600));

        featureMap.putAll(nounFrequencyPercentage(heading,textContent, nounsForFrequency));

        for (String category : categories){
            if (newsDocument.getSection().equalsIgnoreCase(category))
                featureMap.put("CAT" + category, 1.);
            else
                featureMap.put("CAT" + category, 0.);
        }

        if(binningInfo == null)
            return new FeatureDocument(featureMap, newsDocument.getShares());
        else
            return new FeatureDocument(featureMap,
                    binningInfo.getBinningValueMap().get(newsDocument),
                    binningInfo.getBinningIDMap().get(newsDocument));
    }

    private static double sentimentToDouble(String text){
        double sentiment = 0;
        if (text.equals("P+"))
            sentiment = 2;
        else if (text.equals("P"))
            sentiment = 1;
        else if (text.equals("NEU"))
            sentiment = 0;
        else if (text.equals("N"))
            sentiment = -1;
        else if (text.equals("N+"))
            sentiment = -2;

        return sentiment;
    }

    private static Map<String, Double> nounFrequencyPercentage(String heading, String contentText, List<String> nounsForFrequency){
        Map<String, Double> frequencyMap = new HashMap<>();
        nounsForFrequency.forEach(noun -> frequencyMap.put("NOUN" + noun, 0.));

        int wordCount = 0;

        Scanner scanner = new Scanner(heading);
        String word;
        while(scanner.hasNext()){
            word = scanner.next();
            wordCount++;
            if(nounsForFrequency.contains(word.toLowerCase()))
                frequencyMap.put("NOUN" + word.toLowerCase(), frequencyMap.get("NOUN" + word.toLowerCase()) + 10.);
        }

        scanner = new Scanner(contentText);
        while(scanner.hasNext()){
            word = scanner.next();
            wordCount++;
            if(nounsForFrequency.contains(word.toLowerCase()))
                frequencyMap.put("NOUN" + word.toLowerCase(), frequencyMap.get("NOUN" + word.toLowerCase()) + 1.);
        }

        for(Map.Entry<String, Double> entry : frequencyMap.entrySet()){
            entry.setValue(entry.getValue()/wordCount);
        }

        return frequencyMap;
    }

    private static double subjectiveToDouble(String text){
        return text.equals("OBJECTIVE") ? 1 : -1;
    }

    private static double ironyToDouble(String text){
        return text.equals("NONIRONIC") ? 1 : -1;
    }




}
