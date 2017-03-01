package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.models.Grade;

/**
 * Created by Stefan on 11-12-2016.
 * <p>
 * Adapter to display the grades in the grade fragment.
 */

public class GradeAdapter extends BaseAdapter {
    private static final Double INSUFFICIENT = 5.5;

    private final LayoutInflater layoutinflater;
    private final ArrayList<Grade> grades;
    private final Context ctx;

    public GradeAdapter(final Context ctx, final ArrayList<Grade> grades) {
        this.ctx = ctx;
        this.grades = grades;
        layoutinflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return grades.size();
    }

    @Override
    public Object getItem(final int i) {
        return grades.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View view, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View v = view;

        // Initialize the viewHolder.
        if (v == null) {
            viewHolder = new ViewHolder();
            v = layoutinflater.inflate(R.layout.row_grade, viewGroup, false);
            viewHolder.grade = (TextView) v.findViewById(R.id.grade_row_grade);
            viewHolder.course = (TextView) v.findViewById(R.id.grade_row_course);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        // Prevent the application from crashing because the list is empty.
        if (grades.size() == 0) {
            return v;
        }

        // Make the grade colour red if it's insufficient.
        Double grade = grades.get(position).getGrade();
        if (grade < INSUFFICIENT) {
            viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.colorRed));
        }

        // Display the grade.
        String gradeString = String.valueOf(grade);
        viewHolder.grade.setText(gradeString);

        // Display the course.
        viewHolder.course.setText(grades.get(position).getName());

        return v;
    }

    /**
     * View holder to access the components.
     */
    private static class ViewHolder {
        private TextView grade;
        private TextView course;
    }
}
