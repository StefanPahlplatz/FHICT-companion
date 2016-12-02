package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.models.NewsItem;

public class NewsFragment extends Fragment
{
    private static final String TAG = NewsFragment.class.getSimpleName();

    // Reference to the main listView
    private RecyclerView recyclerView;

    // Store news items
    private ArrayList<NewsItem> newsItems;

    private NewsAdapter adapter;

    private SwipeRefreshLayout refreshLayout;

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

        recyclerView = (RecyclerView) view.findViewById(R.id.news_recylerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);

        new loadNews().execute();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new loadNews().execute();
            }
        });

        return view;
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
                            jArray.getJSONObject(i).getString("thumbnail"),
                            jArray.getJSONObject(i).getString("link"),
                            jArray.getJSONObject(i).getString("content")));
                }
            } catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: Couldn't fetch news", ex);
            }

            return null;
        }

        protected void onPostExecute(Void params)
        {
            for (int i = 0; i < newsItems.size(); i++)
            {
                new loadThumbnail().execute(newsItems.get(i));
            }

            adapter = new NewsAdapter(newsItems, getContext());
            recyclerView.setAdapter(adapter);
            Log.i(TAG, "onPostExecute: adapter assigned");

            refreshLayout.setRefreshing(false);
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
