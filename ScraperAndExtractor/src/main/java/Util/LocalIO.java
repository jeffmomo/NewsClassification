package Util;

import FeatureExtraction.FeatureDocument;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JJ on 19-10-2015.
 */
public class LocalIO {
   //private static final DecimalFormat df = new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.US)); //amount of number signs after the dot determines the precession the output.

    public static Object read(String path){
        try
        {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Object readObject = in.readObject();
            in.close();
            fileIn.close();

            return readObject;
        }catch(Exception i)
        {
            i.printStackTrace();
        }

        return null;
    }

    public static List<String> readLinesAsStrings(String path){
        try(BufferedReader bufferedReader =new BufferedReader(new FileReader(path)))
        {
            List<String> linesList = new ArrayList<>();
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                linesList.add(line);
            }

            return linesList;
        }catch(Exception i)
        {
            i.printStackTrace();
        }

        return null;
    }

    public static void write(String path, Object object){
        try
        {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();

        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }



    /**
     * Assumes FeatureDocuments are well formed with the same features, and no missing values.
     * @param articleFeatures
     * @param path
     */
    public static void writeFeaturesToTextFile(Set<FeatureDocument> articleFeatures, String path){
        List<String> featureOrder = articleFeatures.iterator().next()
                .getFeatures()
                .keySet()
                .parallelStream()
                .sorted()
                .collect(Collectors.toList());

        try(BufferedWriter output = new BufferedWriter(new FileWriter(path))){

            StringBuilder stringBuilder = new StringBuilder();
            for (String feature : featureOrder){
                stringBuilder.append(feature);
                stringBuilder.append(",");
            }
            stringBuilder.append("TARGETshares,TARGETBucketID");
            output.write(stringBuilder.toString());
            output.newLine();

            articleFeatures.parallelStream()
                    .map(featureDocument -> {
                        StringBuilder sb = new StringBuilder();
                        for (String feature : featureOrder){
                            sb.append(featureDocument.getFeatures().get(feature));
                            sb.append(",");
                        }
                        sb.append(featureDocument.getShareAmount());
                        sb.append(",");
                        sb.append(featureDocument.getBucketNumber());
                        return sb.toString();
                    }).sequential()
                    .forEach(featureString -> {
                        try {
                            output.write(featureString);
                            output.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void writeMeanVarianceToFile(Map<String, Double> meanMap, Map<String, Double> varianceMap, String path){
        List<String> featureOrder = meanMap
                .keySet()
                .parallelStream()
                .sorted()
                .collect(Collectors.toList());

        StringBuilder sbMean = new StringBuilder("Mean,");
        StringBuilder sbVariance = new StringBuilder("Variance,");

        for(String key : featureOrder) {
            sbMean.append(meanMap.get(key) + ",");
            sbVariance.append(varianceMap.get(key) + ",");
        }

        writeLinesToFile(Arrays.asList(sbMean.toString(),sbVariance.toString()), path);
    }

    public static void writeLinesToFile(List<String> lines, String path){
        try {
            try(BufferedWriter output = new BufferedWriter(new FileWriter(path))){
                for(String line : lines){
                    output.write(line);
                    output.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
