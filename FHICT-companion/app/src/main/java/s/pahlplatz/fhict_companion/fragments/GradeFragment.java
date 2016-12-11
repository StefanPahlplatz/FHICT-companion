package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.models.Grade;

/**
 * Created by Stefan on 30-11-2016.
 *
 * Fragment to show the user their grades.
 */

public class GradeFragment extends Fragment
{
    private static final String TAG = GradeFragment.class.getSimpleName();

    // Grade array
    private ArrayList<Grade> grades;
    private ArrayAdapter adapter;

    // Reference to the main listView
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        grades = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_grades, container, false);

        setHasOptionsMenu(true);

        listView = (ListView) view.findViewById(R.id.grades_lv);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.grades_swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new loadGrades().execute();
            }
        });

        // Load the grades in to the listView
        new loadGrades().execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        // Refresh grades
        if (id == R.id.action_grade_refresh)
        {
            new loadGrades().execute();
            return true;
        }

        // Sort by grade
        else if (id == R.id.action_grade_sort_grade)
        {
            Collections.sort(grades, new Comparator<Grade>()
            {
                @Override
                public int compare(Grade grade, Grade t1)
                {
                    return (int) (t1.getGrade() - grade.getGrade());
                }
            });
            adapter.notifyDataSetChanged();
        }

        // Sort by name
        else if (id == R.id.action_grade_sort_alphabetical)
        {
            Collections.sort(grades, new Comparator<Grade>()
            {
                @Override
                public int compare(Grade grade, Grade t1)
                {
                    return grade.getName().compareTo(t1.getName());
                }
            });
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    public class loadGrades extends AsyncTask<Void, Void, Void>
    {
        @Override
        public void onPreExecute()
        {
            // Clear grade list
            grades.clear();
        }

        @Override
        public Void doInBackground(Void... params)
        {
            try
            {
                JSONArray jArray = new JSONArray(FhictAPI.getStream(
                        "https://api.fhict.nl/grades/me",
                        getContext().getSharedPreferences(
                                "settings", Context.MODE_PRIVATE).getString("token", "")));

                for (int i = 0; i < jArray.length(); i++)
                {
                    grades.add(new Grade(jArray.getJSONObject(i).getString("item"), jArray.getJSONObject(i).getDouble("grade")));
                }
            }catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: A problem occurred while parsing the JSON file.", ex);
            }

            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onPostExecute(Void params)
        {
            try
            {
                // Create an ArrayAdapter for the listView

                adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, grades)
                {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent)
                    {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        double grade = grades.get(position).getGrade();
                        String detail = "Grade: " + Double.toString(grade);

                        text1.setText(grades.get(position).getName());
                        text2.setText(detail);

                        if (grade < 5.5)
                        {
                            text2.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        }
                        return view;
                    }
                };

                // Assign the new adapter
                listView.setAdapter(adapter);

                // Stop refreshing
                View view = getView();
                if (view != null)
                {
                    SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.grades_swiperefresh);
                    if (refreshLayout.isRefreshing())
                    {
                        refreshLayout.setRefreshing(false);
                    }
                }
            } catch (NullPointerException ex)
            {
                Log.e(TAG, "onPostExecute: Couldn't load grades, view changed before onPostExecute triggered?", ex);
            }
        }
    }
}
