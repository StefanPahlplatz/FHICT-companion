package s.pahlplatz.fhict_companion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import s.pahlplatz.fhict_companion.R;

public class NewsDetailsActivity extends AppCompatActivity
{
    private String title, content, author, pubDate;

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
        } else
        {
            throw new RuntimeException("Couldn't get arguments from intent, make sure you create the intent with params");
        }

        TextView header = (TextView) findViewById(R.id.news_details_title);
        header.setText(title);

        TextView desc = (TextView) findViewById(R.id.news_details_desc);
        desc.setText(content);
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
