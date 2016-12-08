package s.pahlplatz.fhict_companion.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.Day;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = ScheduleAdapter.class.getSimpleName();

    private ArrayList<Day> days;

    public ScheduleAdapter(ArrayList<Day> days)
    {
        this.days = days;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (days.get(position).getSubject().equals("Break"))
            return 1;
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case 0:
                return new ViewHolder0(LayoutInflater.from(parent.getContext()).inflate(R.layout.block_card_view, parent, false));
            case 1:
                return new ViewHolder1(LayoutInflater.from(parent.getContext()).inflate(R.layout.break_card_view, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        String time = days.get(position).getStart() + " - " + days.get(position).getEnd();
        switch (holder.getItemViewType())
        {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0) holder;
                viewHolder0.time.setText(time);
                viewHolder0.course.setText(days.get(position).getSubject());
                viewHolder0.room.setText(days.get(position).getRoom());
                viewHolder0.teacher.setText(days.get(position).getTeacherAbbr());
                break;
            case 1:
                try
                {
                    ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                    String start = time.substring(0, 5);
                    String end = time.substring(8, 13);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date date1 = format.parse(start);
                    Date date2 = format.parse(end);
                    long difference = (date2.getTime() - date1.getTime()) / 60000;
                    String s = String.valueOf(difference) + " minute break";
                    viewHolder1.time.setText(s);
                } catch (Exception ex)
                {
                    Log.e(TAG, "onBindViewHolder: Error", ex);
                }
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    private class ViewHolder0 extends RecyclerView.ViewHolder
    {
        private TextView time;
        private TextView course;
        private TextView room;
        private TextView teacher;

        private ViewHolder0(View view)
        {
            super(view);
            time = (TextView) view.findViewById(R.id.block_card_times);
            course = (TextView) view.findViewById(R.id.block_card_course);
            room = (TextView) view.findViewById(R.id.block_card_room);
            teacher = (TextView) view.findViewById(R.id.block_card_teacher);
        }
    }

    private class ViewHolder1 extends RecyclerView.ViewHolder
    {
        private TextView time;

        private ViewHolder1(View view)
        {
            super(view);
            time = (TextView) view.findViewById(R.id.break_card_times);
        }
    }
}
