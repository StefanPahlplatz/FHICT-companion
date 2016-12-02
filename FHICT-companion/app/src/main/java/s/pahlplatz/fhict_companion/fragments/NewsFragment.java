package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.WrapContentLinearLayoutManager;
import s.pahlplatz.fhict_companion.utils.models.NewsItem;

public class NewsFragment extends Fragment
{
    private static final String TAG = NewsFragment.class.getSimpleName();

    // Reference to the main listView
    private RecyclerView recyclerView;

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

        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            new loadNews().execute();
            return true;
        } else
        {
            return super.onOptionsItemSelected(item);
        }
    }

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
                        "https://api.fhict.nl/newsfeeds/Fhict?items=10",
                        getContext().getSharedPreferences(
                                "settings", Context.MODE_PRIVATE).getString("token", ""))).getJSONArray("items");

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
                Log.e(TAG, "doInBackground: Couldn't fetch news", ex);
            }

            return null;
        }

        protected void onPostExecute(Void params)
        {
            try
            {
                for (int i = 0; i < newsItems.size(); i++)
                {
                    new loadThumbnail().execute(newsItems.get(i));
                }

                adapter = new NewsAdapter(newsItems, getContext());
                recyclerView.setAdapter(adapter);
                Log.i(TAG, "onPostExecute: adapter assigned");
            } catch (NullPointerException ex)
            {
                Log.e(TAG, "onPostExecute: Couldn't load news, view changed before onPostExecute triggered?", ex);
            }
        }
    }

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
            newsItem.setThumbnailString(null);
            newsItem.setThumbnail(result);
            adapter.notifyDataSetChanged();
        }
    }
}
