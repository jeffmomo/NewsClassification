package Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by JJ on 18-10-2015.
 */
public class Scraper {
    private int initPage;
    private int maxPages;
    private String[] keywords;
    private Random random = new Random();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    public Set<NewsDocument> getDocuments(int startPage, int amountOfPages, List<String> newsCategories){
        ConcurrentSkipListSet<ArticlePage> queue = gatherArticleLinks(startPage, amountOfPages, newsCategories);

        return queue.parallelStream()
                .map(articlePage -> {
                    String articleURL = "http://www.straitstimes.com" + articlePage.getUrl();
                    System.out.println("Fetching " + articleURL);
                    for(int repetition = 0; repetition < 3; repetition++) {
                        try {
                            Document doc = Jsoup.connect(articleURL).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36").timeout(100000).get();
                            Document shares = Jsoup.connect("http://wd.sharethis.com/api/getCount2.php?cb=stButtons.processCB&refDomain=&refQuery=&pubKey=d6899ce0-4e25-493c-a1ab-7c7b43777a37&url=" + URLEncoder.encode(articleURL)).get();

                            int shareAmount = getAmountOfShares(shares);

                            Elements contents = doc.select(".odd.field-item p");
                            Elements heading = doc.select("h1.headline.node-title");
                            Elements pubTime = doc.select("meta[property=article:published_time]");

                            Date date = sdf.parse(pubTime.attr("content"));

                            return new NewsDocument(articleURL, heading.html(), articlePage.getCategory(), contents.html(), shareAmount, date, new Date());
                        } catch (Exception e) {
                            //e.printStackTrace();
                            System.out.println("Error fetching article due to server 500. Trying again in 10 seconds. Repetition number " + (repetition + 1) + " out of 3");

                            sleepSeconds(10000);

                            //We need to repeat the removeHTMLTags
                        }
                    }
                    System.out.println("### Article that failed: " + articleURL);
                    System.err.println("### Article that failed: " + articleURL);
                    return null;
                }).filter(newsDocument -> newsDocument != null)
                .collect(Collectors.toSet());
    }

    private void sleepSeconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private ConcurrentSkipListSet<ArticlePage> gatherArticleLinks(int startPage, int amountOfPages, List<String> newsCategories){
        ConcurrentSkipListSet<ArticlePage> queue = new ConcurrentSkipListSet<>();

        for(String newsSection : newsCategories)
        {
            IntStream.range(startPage, amountOfPages + startPage)
                    .parallel()
                    .forEach(page -> {
                        System.out.println("Getting " + newsSection + " page " + page);

                        String src = "http://www.straitstimes.com/" + newsSection + "/latest?page=" + page;
                        try {
                            Document doc = Jsoup.connect(src).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36").timeout(100000).get();
                            Elements links = doc.select("h3>span>a");

                            for (Element e : links) {
                                String href = e.attr("href");
                                queue.add(new ArticlePage(href, newsSection));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Connection error when gathering article links");
                        }
                    });
        }

        return queue;
    }

    private int getAmountOfShares(Document shares) {
        String shareText = shares.html();
        StringBuilder shareString = new StringBuilder();
        int point = shareText.indexOf("\"total\"") + "\"total\"".length() + 1;
        char chr;
        while ((chr = shareText.charAt(point)) != ',') {
            shareString.append(chr);
            point++;
        }
        return Integer.parseInt(shareString.toString());
    }

}
