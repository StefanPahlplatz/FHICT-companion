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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

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

        @Override
        public void onPostExecute(Void params)
        {
            // Create an ArrayAdapter for the listView
            @SuppressWarnings("unchecked")
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, grades)
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
            SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.grades_swiperefresh);
            if (refreshLayout.isRefreshing())
            {
                refreshLayout.setRefreshing(false);
            }
        }
    }
}
