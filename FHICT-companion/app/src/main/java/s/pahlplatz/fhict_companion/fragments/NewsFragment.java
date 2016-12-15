package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.WrapContentLinearLayoutManager;
import s.pahlplatz.fhict_companion.utils.models.NewsItem;

/**
 * Fragment to display the recent news items
 */
public class NewsFragment extends Fragment
{
    private static final String TAG = NewsFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    // Store news items
    private ArrayList<NewsItem> newsItems;

    private NewsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        newsItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        getActivity().setTitle("News");

        setHasOptionsMenu(true);

        // Configure recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Make progressbar visible
        progressBar = (ProgressBar) view.findViewById(R.id.news_pbar);
        progressBar.setVisibility(View.VISIBLE);

        // Load the news
        new loadNews().execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handles the option menu clicks
     *
     * @param item The item that is selected
     * @return whether the item is handled or not
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        // Refresh news feed
        if (id == R.id.action_news_refresh)
        {
            new loadNews().execute();
            return true;
        }

        // News item amount
        else if (id == R.id.action_news_amount)
        {
            // Create NumberPicker
            final NumberPicker picker = new NumberPicker(getContext());
            picker.setMinValue(5);
            picker.setMaxValue(15);
            picker.setValue(getContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .getInt("amountOfNewsItems", 10));

            // Create the FrameLayout for the NumberPicker to be hosted in
            final FrameLayout layout = new FrameLayout(getContext());
            layout.addView(picker, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            ));

            // Create AlertDialog for the NumberPicker
            new AlertDialog.Builder(getContext())
                    .setView(layout)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            getContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                                    .edit()
                                    .putInt("amountOfNewsItems", picker.getValue())
                                    .apply();

                            // Load news with new amount of items
                            new loadNews().execute();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;

            // TODO: java.lang.IndexOutOfBoundsException: Inconsistency detected. - Invalid view holder adapter positionViewHolder{aba5b7b position=6 id=-1, oldPos=-1, pLpos:-1 no parent}

        }

        // Default option
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Async class to load the news items from the fontys api
     */
    public class loadNews extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            newsItems.clear();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                JSONArray jArray = new JSONObject(FhictAPI.getStream(
                        "https://api.fhict.nl/newsfeeds/Fhict?items=" + getContext()
                                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .getInt("amountOfNewsItems", 10),
                        getContext().getSharedPreferences(
                                "settings", Context.MODE_PRIVATE).getString("token", "")
                )).getJSONArray("items");

                for (int i = 0; i < jArray.length(); i++)
                {
                    newsItems.add(new NewsItem(
                            jArray.getJSONObject(i).getString("pubDate"),
                            jArray.getJSONObject(i).getString("title"),
                            jArray.getJSONObject(i).getString("image"),
                            jArray.getJSONObject(i).getString("link"),
                            jArray.getJSONObject(i).getString("content"),
                            jArray.getJSONObject(i).getString("author")));
                }
            } catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: Couldn't fetch news");
            }

            return null;
        }

        protected void onPostExecute(Void params)
        {
            try
            {
                adapter = new NewsAdapter(newsItems, getContext());
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i < newsItems.size(); i++)
                {
                    new loadThumbnail().execute(newsItems.get(i));
                }
            } catch (NullPointerException ex)
            {
                Log.e(TAG, "onPostExecute: Couldn't load news, view changed before onPostExecute triggered?");
            }
        }
    }

    /**
     * Async class to load the thumbnail for the given NewsItem
     */
    private class loadThumbnail extends AsyncTask<Object, Void, Bitmap>
    {
        private NewsItem newsItem;

        @Override
        protected Bitmap doInBackground(Object... params)
        {
            SharedPreferences sp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
            newsItem = (NewsItem) params[0];
            return FhictAPI.getPicture(newsItem.getThumbnailString(), sp.getString("token", ""));
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            try
            {
                if (newsItem != null && adapter != null)
                {
                    newsItem.setThumbnailString(null);
                    newsItem.setThumbnail(result);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception ex)
            {
                Log.e(TAG, "onPostExecute: Couldn't load news thumbnails");
            }
        }
    }
}
