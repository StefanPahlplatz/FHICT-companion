package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.NewsItem;

/**
 * Created by Stefan on 1-12-2016.
 * <p>
 * Adapter for newsItems in NewsFragment
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>
{
    private ArrayList<NewsItem> newsItems;
    private Context ctx;

    public NewsAdapter(ArrayList<NewsItem> newsItems, Context ctx)
    {
        this.newsItems = newsItems;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.news_card_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        holder.title.setText(newsItems.get(position).getTitle());
        holder.thumbnail.setImageBitmap(newsItems.get(position).getThumbnail());
    }

    @Override
    public int getItemCount()
    {
        return newsItems.size();
    }

    /**
     * Created by Stefan on 1-12-2016.
     * <p>
     * Adapter for newsItems in NewsFragment
     */
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title;
        private TextView desc;
        private ImageView thumbnail;

        private MyViewHolder(View view)
        {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.news_card_image);
            title = (TextView) view.findViewById(R.id.news_card_title);
        }
    }
}
