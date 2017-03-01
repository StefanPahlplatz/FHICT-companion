package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.models.NewsItem;
import s.pahlplatz.fhict_companion.utils.FontysAPI;

/**
 * Created by Stefan on 25-2-2017.
 * <p>
 * Controller for the news fragment.
 */

public class NewsController {
    private static final String TAG = NewsController.class.getSimpleName();
    private static final int MIN_NEWS_ARTICLES = 5;
    private static final int MAX_NEWS_ARTICLES = 15;
    private static final int DEFAULT_NEWS_ARTICLES = 10;

    private NewsControllerListener controllerListener;
    private Context ctx;
    private ArrayList<NewsItem> newsItems;

    public NewsController(final Context ctx, final NewsControllerListener listener) {
        this.ctx = ctx;
        this.newsItems = new ArrayList<>();
        this.controllerListener = listener;

        new LoadNews().execute();
    }

    /**
     * Shows a number picker that will determine the amount of news articles to be shown.
     */
    public void newsAmountDialog() {
        // Create NumberPicker
        final NumberPicker picker = new NumberPicker(ctx);
        picker.setMinValue(MIN_NEWS_ARTICLES);
        picker.setMaxValue(MAX_NEWS_ARTICLES);
        picker.setValue(ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getInt("amountOfNewsItems", DEFAULT_NEWS_ARTICLES));

        // Create the FrameLayout for the NumberPicker to be hosted in.
        final FrameLayout layout = new FrameLayout(ctx);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        ));

        // Create AlertDialog for the NumberPicker.
        new AlertDialog.Builder(ctx)
                .setView(layout)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .edit()
                                .putInt("amountOfNewsItems", picker.getValue())
                                .apply();

                        // Load news with new amount of items.
                        new LoadNews().execute();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Interface implemented by the news host to communicate with the host.
     */
    public interface NewsControllerListener {
        void onAdapterChanged(NewsAdapter adapter);

        void notifyDataSetChanged();

        void onProgressbarVisibility(boolean visible);
    }

    /**
     * Async class to load the news items from the fontys API.
     */
    private class LoadNews extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            newsItems.clear();
            controllerListener.notifyDataSetChanged();
            controllerListener.onProgressbarVisibility(true);
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {
                // Get JSONArray from API.
                JSONArray jArray = new JSONObject(FontysAPI.getStream(
                        "https://api.fhict.nl/newsfeeds/Fhict?items=" + ctx
                                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .getInt("amountOfNewsItems", DEFAULT_NEWS_ARTICLES),
                        ctx.getSharedPreferences(
                                "settings", Context.MODE_PRIVATE).getString("token", "")
                )).getJSONArray("items");

                // Fill the newsItems list.
                for (int i = 0; i < jArray.length(); i++) {
                    newsItems.add(new NewsItem(
                            jArray.getJSONObject(i).getString("pubDate"),
                            jArray.getJSONObject(i).getString("title"),
                            jArray.getJSONObject(i).getString("image"),
                            jArray.getJSONObject(i).getString("content"),
                            jArray.getJSONObject(i).getString("author")));
                }
            } catch (Exception ex) {
                Log.e(TAG, "doInBackground: Couldn't fetch news");
            }
            return null;
        }

        protected void onPostExecute(final Void params) {
            controllerListener.onProgressbarVisibility(false);

            // Set the adapter.
            NewsAdapter adapter = new NewsAdapter(newsItems, ctx);
            controllerListener.onAdapterChanged(adapter);

            // Load the thumbnails.
            for (int i = 0; i < newsItems.size(); i++) {
                new LoadThumbnails().execute(newsItems.get(i));
            }
        }
    }

    /**
     * Async class to load the thumbnail for the given NewsItem.
     * Pass the news item you want the thumbnail of in the execute block.
     */
    private class LoadThumbnails extends AsyncTask<Object, Void, Bitmap> {
        private NewsItem newsItem;

        @Override
        protected Bitmap doInBackground(final Object... params) {
            SharedPreferences sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
            newsItem = (NewsItem) params[0];
            return FontysAPI.getPicture(newsItem.getThumbnailString(), sp.getString("token", ""));
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            try {
                if (newsItem != null) {
                    newsItem.setThumbnailString("");
                    newsItem.setThumbnail(result);
                    controllerListener.notifyDataSetChanged();
                }
            } catch (Exception ex) {
                Log.e(TAG, "onPostExecute: Couldn't load news thumbnails");
            }
        }
    }
}
