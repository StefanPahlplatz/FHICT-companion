package s.pahlplatz.fhict_companion.utils.models;

import android.graphics.Bitmap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Stefan on 1-12-2016.
 * <p>
 * Class to store details of a news item
 */

public class NewsItem
{
    private String pubDate;
    private String title;
    private String thumbnailString;
    private Bitmap thumbnail;
    private String link;
    private String content;

    public NewsItem(String pubDate, String title, String thumbnailString, String link, String content)
    {
        this.pubDate = pubDate;
        this.title = title;
        this.thumbnailString = thumbnailString;
        this.link = link;

        Document doc = Jsoup.parse(content);
        this.content = doc.body().text();
    }

    public String getPubDate()
    {
        return pubDate;
    }

    public String getTitle()
    {
        return title;
    }

    public String getThumbnailString()
    {
        return thumbnailString;
    }

    public void setThumbnailString(String thumbnailString)
    {
        this.thumbnailString = thumbnailString;
    }

    public Bitmap getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getLink()
    {
        return link;
    }

    public String getContent()
    {
        return content;
    }
}
