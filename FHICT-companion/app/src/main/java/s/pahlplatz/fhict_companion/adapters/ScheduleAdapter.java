package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.Day;

/**
 * Created by Stefan on 1-12-2016.
 * <p>
 * Adapter for newsItems in NewsFragment
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>
{
    private ArrayList<Day> days;
    private OnAdapterInteractionListener mListener;

    /**
     * Constructor for NewsAdapter
     *
     * @param days ArrayList of NewsItems you want to show
     * @param ctx  context used for the Listener
     */
    public ScheduleAdapter(ArrayList<Day> days, Context ctx)
    {
        this.days = days;

        if (ctx instanceof OnAdapterInteractionListener)
        {
            mListener = (OnAdapterInteractionListener) ctx;
        } else
        {
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
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
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        // Set the text
        holder.title.setText(newsItems.get(position).getTitle());
        String authorString = "By " + newsItems.get(position).getAuthor();
        holder.author.setText(authorString);
        holder.thumbnail.setImageBitmap(newsItems.get(position).getThumbnail());
        holder.pubDate.setText(newsItems.get(position).getPubDate().substring(0, 10));

        // Send MainActivity signal to swap fragments when the user clicks on the card
        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mListener.onAdapterInteractionListener(newsItems.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    /**
     * Interface for when a card is clicked
     */
    public interface OnAdapterInteractionListener
    {

        void onAdapterInteractionListener(Day day);
    }

    /**
     * Created by Stefan on 1-12-2016.
     * <p>
     * View holder for the adapter
     */
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView time;
        private TextView course;
        private TextView room;
        private TextView teacher;

        private MyViewHolder(View view)
        {
            super(view);
            time = (TextView) view.findViewById(R.id.block_card_times);
            course = (TextView) view.findViewById(R.id.block_card_course);
            room = (TextView) view.findViewById(R.id.block_card_room);
            teacher = (TextView) view.findViewById(R.id.block_card_teacher);

        }
    }
}
