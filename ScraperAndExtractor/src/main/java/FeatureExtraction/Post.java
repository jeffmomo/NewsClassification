package FeatureExtraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;


/**
 * This class implements POST request to the API
 * Authored by: MeaningCloud
 */
public class Post {
    private URL url;
    String params;

    public Post (String api) throws MalformedURLException{
        url = new URL(api);
        params="";
    }

    public void addParameter (String name, String value) throws UnsupportedEncodingException{
        if (params.length()>0)
            params += "&" + URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
        else
            params += URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }

    public String getResponse() throws IOException {
        // management internal parameter
        this.addParameter("src", "sdk-java-1.2");
        String response = "";
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
        try {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(params);
            wr.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            response += line;
        }
        return response;
    }
}
