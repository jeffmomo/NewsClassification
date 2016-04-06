package FeatureExtraction;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;

import org.w3c.dom.*;

/**
 * This class implements a starting client for Sentiment Analysis
 * Authored by: MeaningCloud
 */
public class SentimentClient {
    Node score_tag = null;
    Node agreement = null;
    Node subjectivity = null;
    Node irony = null;
    Node confidence = null;

    public SentimentClient(String txt){
        try {
            // We define the variables needed to call the API
            String api = "http://api.meaningcloud.com/sentiment-2.0";
            String key = "03e1a6f761e8a2dea20f4751bc019a91";
            String model = "general_en"; // Language

            Post post = new Post(api);
            post.addParameter("key", key);
            post.addParameter("txt", txt);
            post.addParameter("model", model);
            post.addParameter("of", "xml");
            String response = post.getResponse();

            // Prints the specific fields in the response (sentiment)
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
            doc.getDocumentElement().normalize();
            Element response_node = doc.getDocumentElement();
            try {
                NodeList status_list = response_node.getElementsByTagName("status");
                Node status = status_list.item(0);
                NamedNodeMap attributes = status.getAttributes();
                Node code = attributes.item(0);
                if (!code.getTextContent().equals("0")) {
                    System.out.println("Not found");
                } else {
                    NodeList score_tags = response_node.getElementsByTagName("score_tag");
                    NodeList agreements = response_node.getElementsByTagName("agreement");
                    NodeList subjectivities = response_node.getElementsByTagName("subjectivity");
                    NodeList ironies = response_node.getElementsByTagName("irony");
                    NodeList confidences = response_node.getElementsByTagName("confidence");

                    String output = "";
                    if (score_tags.getLength() > 0)
                        score_tag = score_tags.item(0);
                    if (agreements.getLength() > 0)
                        agreement = agreements.item(0);
                    if (subjectivities.getLength() > 0)
                        subjectivity = subjectivities.item(0);
                    if (ironies.getLength() > 0)
                        irony = ironies.item(0);
                    if (confidences.getLength() > 0)
                        confidence = confidences.item(0);
                }
            } catch (Exception e) {
                System.out.println("Not found");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public String getSentiment()
    {
        if (score_tag != null)
            return score_tag.getTextContent();
        return "NULL";
    }

    public String getSubjectivity()
    {
        return subjectivity.getTextContent();
    }

    public String getIrony()
    {
        return irony.getTextContent();
    }
}
