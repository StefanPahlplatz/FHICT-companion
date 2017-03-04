package s.pahlplatz.fhict_companion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.models.PersonInfo;

/**
 * Created by Stefan on 3-3-2017.
 * <p>
 * Adapter to display the person details.
 */

public class PeopleDetailAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;
    private final ArrayList<PersonInfo> info;

    public PeopleDetailAdapter(final ArrayList<PersonInfo> info, final Context ctx) {
        this.info = info;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(final int position) {
        return info.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        View v = convertView;

        if (v == null) {
            viewHolder = new ViewHolder();
            v = layoutInflater.inflate(R.layout.row_people_detail, parent, false);
            viewHolder.constant = (TextView) v.findViewById(R.id.row_people_detail_constant);
            viewHolder.variable = (TextView) v.findViewById(R.id.row_people_detail_var);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.constant.setText(info.get(position).getLeft());
        viewHolder.variable.setText(info.get(position).getRight());

        return v;
    }

    /**
     * View holder to access the components.
     */
    private static class ViewHolder {
        private TextView constant;
        private TextView variable;
    }
}
