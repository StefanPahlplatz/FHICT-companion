package s.pahlplatz.fhict_companion.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import s.pahlplatz.fhict_companion.R;

public class NewsDetailsActivity extends Activity
{
    private String title, content, author, pubDate;
    private Bitmap image;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        Intent myIntent = getIntent();
        if (myIntent != null)
        {
            title = myIntent.getStringExtra("title");
            content = myIntent.getStringExtra("content");
            author = myIntent.getStringExtra("author");
            pubDate = myIntent.getStringExtra("pubDate");
            image = myIntent.getParcelableExtra("image");
        } else
        {
            throw new RuntimeException("Couldn't get arguments from intent, make sure you create the intent with params");
        }

        ImageView imageView = (ImageView) findViewById(R.id.news_details_image_view);
        imageView.setImageBitmap(image);

        TextView header = (TextView) findViewById(R.id.news_details_title);
        header.setText(title);

        TextView desc = (TextView) findViewById(R.id.news_details_desc);
        desc.setText(content);

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
