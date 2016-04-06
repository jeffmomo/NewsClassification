package Scraper;

/**
 * Created by ael10dan on 10/8/2015.
 */
public class ArticlePage implements Comparable<ArticlePage> {
    private String url;
    private String category;

    public ArticlePage(String url, String category){
        this.url = url;
        this.category = category;
    }

    public String getUrl() { return url; }

    public String getCategory() { return category; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticlePage that = (ArticlePage) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return !(category != null ? !category.equals(that.category) : that.category != null);

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ArticlePage o) {
        return url.compareTo(o.url);
    }
}
