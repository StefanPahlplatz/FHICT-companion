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
    private final LayoutInflater layoutinflater;
    private final ArrayList<Grade> grades;
    private final Context ctx;

    public GradeAdapter(Context ctx, ArrayList<Grade> grades) {
        this.ctx = ctx;
        this.grades = grades;
        layoutinflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return grades.size();
    }

    @Override
    public Object getItem(int i) {
        return grades.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        // Initialize the viewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutinflater.inflate(R.layout.row_grade, viewGroup, false);
            viewHolder.grade = (TextView) view.findViewById(R.id.grade_row_grade);
            viewHolder.course = (TextView) view.findViewById(R.id.grade_row_course);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Prevent the application from crashing because the list is empty
        if (grades.size() == 0)
            return view;

        // Make the grade colour red if it's insufficient
        Double grade = grades.get(position).getGrade();
        if (grade < 5.5) {
            viewHolder.grade.setTextColor(ContextCompat.getColor(ctx, R.color.colorRed));
        }

        // Display the grade
        String gradeString = String.valueOf(grade);
        viewHolder.grade.setText(gradeString);

        // Display the course
        viewHolder.course.setText(grades.get(position).getName());

        return view;
    }

    private static class ViewHolder {
        TextView grade;
        TextView course;
    }
}