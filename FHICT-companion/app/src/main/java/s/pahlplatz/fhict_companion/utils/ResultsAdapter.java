package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.Course;
import s.pahlplatz.fhict_companion.utils.models.ItemDetail;

/**
 * Created by Stefan on 30-11-2016.
 */

public class ResultsAdapter extends BaseAdapter
{
    private final Context ctx;
    private HashMap<String, Integer> map;

    public ResultsAdapter(Context ctx, HashMap map)
    {
        this.ctx = ctx;
        this.map = map;
    }

    @Override
    public int getCount()
    {
        return map.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return map.
    }
}
