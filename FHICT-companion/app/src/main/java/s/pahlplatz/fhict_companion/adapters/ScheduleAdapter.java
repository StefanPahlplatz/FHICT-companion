package s.pahlplatz.fhict_companion.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.models.Day;

/**
 * Created by Stefan on 24-12-2016.
 * <p>
 * Adapter for the schedule.
 * <p>
 * The adapter can contain 2 types of blocks, normal classes and breaks;<br>
 * ItemViewType 0: The block is a normal block.<br>
 * ItemViewType 1: The block is a break.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ScheduleAdapter.class.getSimpleName();

    private final Day day;

    /**
     * Default constructor.
     *
     * @param day the day the schedule will show.
     */
    public ScheduleAdapter(Day day) {
        this.day = day;
    }

    /**
     * Determines if the current item is a break or not.
     *
     * @param position of the block in the day.
     * @return 1 if the block is a break, return 0 if it's a normal block.
     */
    @Override
    public int getItemViewType(int position) {
        return day.getBlock(position).getSubject().equals("Break") ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolder0(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_schedule_block, parent, false));
            case 1:
                return new ViewHolder1(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_schedule_break, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String time = day.getBlock(position).getStart() + " - " + day.getBlock(position).getEnd();
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0) holder;
                viewHolder0.time.setText(time);
                viewHolder0.course.setText(day.getBlock(position).getSubject());
                viewHolder0.room.setText(day.getBlock(position).getRoom());
                viewHolder0.teacher.setText(day.getBlock(position).getTeacherAbbr());
                break;
            case 1:
                try {
                    ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                    String start = time.substring(0, 5);
                    String end = time.substring(8, 13);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Date date1 = format.parse(start);
                    Date date2 = format.parse(end);
                    long difference = (date2.getTime() - date1.getTime()) / 60000;
                    String s = String.valueOf(difference) + " minute break";
                    viewHolder1.time.setText(s);
                } catch (ParseException ex) {
                    Log.e(TAG, "onBindViewHolder: Exception occurred while parsing the string to a Date", ex);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return day.size();
    }

    /**
     * View holder for normal blocks.
     */
    private class ViewHolder0 extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView course;
        private final TextView room;
        private final TextView teacher;

        private ViewHolder0(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.block_card_times);
            course = (TextView) view.findViewById(R.id.block_card_course);
            room = (TextView) view.findViewById(R.id.block_card_room);
            teacher = (TextView) view.findViewById(R.id.block_card_teacher);
        }
    }

    /**
     * View holder for breaks.
     */
    private class ViewHolder1 extends RecyclerView.ViewHolder {
        private final TextView time;

        private ViewHolder1(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.break_card_times);
        }
    }
}
