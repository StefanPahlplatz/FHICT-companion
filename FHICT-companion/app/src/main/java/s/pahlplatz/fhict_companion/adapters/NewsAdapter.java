package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private final ArrayList<NewsItem> newsItems;
    private OnAdapterInteractionListener mListener;

    /**
     * Constructor for NewsAdapter
     *
     * @param newsItems ArrayList of NewsItems you want to show
     * @param ctx       context used for the Listener
     */
    public NewsAdapter(ArrayList<NewsItem> newsItems, Context ctx) {
        this.newsItems = newsItems;

        if (ctx instanceof OnAdapterInteractionListener) {
            mListener = (OnAdapterInteractionListener) ctx;
        } else {
            throw new RuntimeException(ctx.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Create a new ViewHolder that the recyclerView can reuse
     *
     * @param parent   ViewGroup
     * @param viewType int
     * @return the new ViewHolder
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.news_card_view, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * Basically the onCreateView for the adapter
     *
     * @param holder   custom viewHolder
     * @param position position in list
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // Set the text
        holder.title.setText(newsItems.get(position).getTitle());
        String authorString = "By " + newsItems.get(position).getAuthor();
        holder.author.setText(authorString);
        holder.thumbnail.setImageBitmap(newsItems.get(position).getThumbnail());
        holder.pubDate.setText(newsItems.get(position).getPubDate().substring(0, 10));

        // Send MainActivity signal to swap fragments when the user clicks on the card
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAdapterInteractionListener(newsItems.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    /**
     * Interface for when a card is clicked
     */
    public interface OnAdapterInteractionListener {

        void onAdapterInteractionListener(NewsItem newsItem);
    }

    /**
     * Created by Stefan on 1-12-2016.
     * <p>
     * View holder for the adapter
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final TextView pubDate;
        private final ImageView thumbnail;
        private final CardView cardView;

        private MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.news_card_image);
            title = (TextView) view.findViewById(R.id.news_card_title);
            author = (TextView) view.findViewById(R.id.news_card_author);
            cardView = (CardView) view.findViewById(R.id.news_card_view);
            pubDate = (TextView) view.findViewById(R.id.news_card_pubdate);
        }
    }
}
