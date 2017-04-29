package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.models.CustomBlock;

/**
 * Created by Stefan on 11-12-2016.
 * <p>
 * Adapter to display the grades in the grade fragment.
 */

public class BlockAdapter extends BaseAdapter {
    private final LayoutInflater layoutinflater;
    private final ArrayList<CustomBlock> blocks;
    private final Integer[] checked;
    private final Context ctx;

    public BlockAdapter(final Context ctx, final ArrayList<CustomBlock> blocks) {
        this.ctx = ctx;
        this.blocks = blocks;
        this.checked = new Integer[blocks.size()];
        Arrays.fill(checked, 0);

        layoutinflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return blocks.size();
    }

    @Override
    public Object getItem(final int i) {
        return blocks.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View view, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View v = view;

        // Initialize the viewHolder.
        if (v == null) {
            viewHolder = new ViewHolder();
            v = layoutinflater.inflate(R.layout.row_block, viewGroup, false);
            viewHolder.subject = (TextView) v.findViewById(R.id.row_block_subject);
            viewHolder.teacher = (TextView) v.findViewById(R.id.row_block_teacher);
            viewHolder.time = (TextView) v.findViewById(R.id.row_block_time);
            viewHolder.checkBox = (CheckBox) v.findViewById(R.id.row_block_checkbox);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        // Prevent the application from crashing because the list is empty.
        if (blocks.size() == 0) {
            return v;
        }

        String subject = blocks.get(position).getSubject();
        viewHolder.subject.setText(subject);
        viewHolder.subject.setTypeface(null, Typeface.BOLD);

        String teacher = blocks.get(position).getTeacherAbbr();
        viewHolder.teacher.setText(teacher);

        String startTime = blocks.get(position).getStart();
        String endTime = blocks.get(position).getEnd();
        String day = blocks.get(position).getDay();
        viewHolder.time.setText(String.format("%s: %s - %s", day, startTime, endTime));

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
                checked[position] ^= 1;
            }
        });


        return v;
    }

    public ArrayList<Integer> getSelected() {
        ArrayList<Integer> retList = new ArrayList<>();
        for (int i = 0; i < checked.length; i++) {
            if (checked[i] == 1) { //TODO: replace
                retList.add(i);
            }
        }
        return retList;
    }

    /**
     * View holder to access the components.
     */
    private static class ViewHolder {
        private TextView subject;
        private TextView teacher;
        private TextView time;
        private CheckBox checkBox;
    }
}

