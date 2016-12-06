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

    /**
     * Constructor for NewsAdapter
     *
     * @param days ArrayList of NewsItems you want to show
     * @param ctx  context used for the Listener
     */
    public ScheduleAdapter(ArrayList<Day> days, Context ctx)
    {
        this.days = days;
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
                parent.getContext()).inflate(R.layout.block_card_view, parent, false);

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
        String time = days.get(position).getStart() + " - " + days.get(position).getEnd();
        holder.time.setText(time);

        holder.course.setText(days.get(position).getSubject());
        holder.room.setText(days.get(position).getRoom());
        holder.teacher.setText(days.get(position).getTeacherAbbr());
    }

    @Override
    public int getItemCount()
    {
        return days.size();
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
