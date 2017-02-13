package s.pahlplatz.fhict_companion.utils.models;

import android.graphics.Bitmap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Stefan on 1-12-2016.
 * <p>
 * Class to store details of a news item
 */

public class NewsItem {
    private final String pubDate;
    private final String title;
    private final String content;
    private final String author;
    private String thumbnailString;
    private Bitmap thumbnail;

    /**
     * Default constructor
     */
    public NewsItem(String pubDate, String title, String thumbnailString, String content, String author) {
        this.pubDate = pubDate;
        this.title = title;
        this.thumbnailString = thumbnailString;
        this.author = author;

        Document doc = Jsoup.parse(content);
        this.content = doc.body().text();
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailString() {
        return thumbnailString;
    }

    public void setThumbnailString(String thumbnailString) {
        this.thumbnailString = thumbnailString;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }
}
