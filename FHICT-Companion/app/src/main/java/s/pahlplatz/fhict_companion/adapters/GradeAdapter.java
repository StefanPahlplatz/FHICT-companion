package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
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

        Double grade = grades.get(position).getGrade();
        viewHolder.grade.setTypeface(null, Typeface.BOLD);

        if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("colour_grades", true)) {
            if (grade < 5.5) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeRed));
            } else if (grade < 6) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeOrange));
            } else if (grade < 7) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeYellow));
            } else if (grade < 8) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeLime));
            } else if (grade < 9) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeLightGreen500));
            } else if (grade < 10) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeLightGreen600));
            } else if (grade == 10) {
                viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.gradeGreen));
            }
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
