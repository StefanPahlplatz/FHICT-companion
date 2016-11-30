package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.Course;
import s.pahlplatz.fhict_companion.utils.models.ItemDetail;

/**
 * Created by Stefan on 30-11-2016.
 */

public class ResultsAdapter extends BaseExpandableListAdapter
{
    private List<Course> courseList;
    private int itemLayoutId;
    private int groupLayoutId;
    private Context ctx;

    public ResultsAdapter(Context ctx, List<Course> courseList)
    {
        this.itemLayoutId = R.layout.adapter_results_item;
        this.groupLayoutId = R.layout.adapter_results_group;
        this.courseList = courseList;
        this.ctx = ctx;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return courseList.get(groupPosition).getItemList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return courseList.get(groupPosition).getItemList().get(childPosition).hashCode();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_results_item, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.results_textview_child);

        ItemDetail det = courseList.get(groupPosition).getItemList().get(childPosition);

        itemName.setText(det.getName());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return courseList.get(groupPosition).getItemList().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return courseList.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return courseList.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return courseList.get(groupPosition).hashCode();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_results_group, parent, false);
        }

        TextView groupName = (TextView) convertView.findViewById(R.id.results_textview_header);

        Course course = courseList.get(groupPosition);

        groupName.setText(course.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
