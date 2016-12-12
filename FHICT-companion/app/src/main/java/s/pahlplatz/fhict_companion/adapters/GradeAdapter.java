package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.Grade;

/**
 * Created by Stefan on 11-12-2016.
 * <p>
 * Adapter to display the grades in the grade fragment.
 */

public class GradeAdapter extends BaseAdapter
{
    private LayoutInflater layoutinflater;
    private ArrayList<Grade> grades;
    private Context ctx;

    public GradeAdapter(Context ctx, ArrayList<Grade> grades)
    {
        this.ctx = ctx;
        this.grades = grades;
        layoutinflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return grades.size();
    }

    @Override
    public Object getItem(int i)
    {
        return grades.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        ViewHolder viewHolder;

        if (view == null)
        {
            viewHolder = new ViewHolder();
            view = layoutinflater.inflate(R.layout.grade_card_view, viewGroup, false);
            viewHolder.grade = (TextView) view.findViewById(R.id.grade_card_grade);
            viewHolder.course = (TextView) view.findViewById(R.id.grade_card_course);
            view.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        String grade = String.valueOf(grades.get(position).getGrade());
        viewHolder.grade.setText(grade);
        viewHolder.course.setText(grades.get(position).getName());

        return view;
    }

    private static class ViewHolder
    {
        TextView grade;
        TextView course;
    }
}
