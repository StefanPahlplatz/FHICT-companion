package s.pahlplatz.fhict_companion.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.ScheduleAdapter;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.WrapContentLinearLayoutManager;
import s.pahlplatz.fhict_companion.utils.models.Day;

/**
 * Fragment to show your schedule
 */
public class ScheduleFragment extends Fragment
{
    private static final String TAG = ScheduleFragment.class.getSimpleName();

    private ArrayList<Day> days;
    private ScheduleAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Spinner dropdownWeeks;
    private TextView no_data;

    private String todayDay;
    private String todayDate;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Configure the spinner
        dropdownWeeks = (Spinner) view.findViewById(R.id.schedule_spinner);
        String[] items = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdownWeeks.setAdapter(adapter);
        dropdownWeeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                showDaySchedule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        // Configure recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.schedule_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Set the current day as selected day
        Date date = new Date();
        todayDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        dropdownWeeks.setSelection(getDayAsInt(todayDay));

        // Make progressbar visible
        progressBar = (ProgressBar) view.findViewById(R.id.schedule_pbar);
        progressBar.setVisibility(View.VISIBLE);

        no_data = (TextView) view.findViewById(R.id.schedule_no_data);

        new loadSchedule().execute();

        return view;
    }

    /**
     * Convert the day string into an int
     * If the day is sunday or saturday return 0 anyway.
     * @param day string
     * @return day representation as integer
     */
    private int getDayAsInt(String day)
    {
        switch (day)
        {
            case "Monday":
                return 0;
            case "Tuesday":
                return 1;
            case "Wednesday":
                return 2;
            case "Thursday":
                return 3;
            case "Friday":
                return 4;
            case "Saturday":
                return 0;
            case "Sunday":
                return 0;
        }
        return 0;
    }

    private void showDaySchedule()
    {
        String day = dropdownWeeks.getSelectedItem().toString();

        int dif = getDayAsInt(day) - getDayAsInt(todayDay);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, dif);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        String formatted = format1.format(cal.getTime());
        Log.i(TAG, "showDaySchedule: " + formatted);

        ArrayList<Day> customDays = new ArrayList<>();

        for (int i = 0; i < days.size(); i++)
        {
            if (days.get(i).getDate().equals(formatted))
            {
                customDays.add(days.get(i));
            }
        }

        adapter = new ScheduleAdapter(customDays, getContext());
        recyclerView.setAdapter(adapter);

        no_data.setVisibility(customDays.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Async class to load the schedule
     */
    public class loadSchedule extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            if (days != null)
            {
                days.clear();
            }
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            days = new ArrayList<>();

            try
            {
                JSONArray jArray = new JSONObject(FhictAPI.getStream(
                        "https://api.fhict.nl/schedule/me?days=9",
                        getContext().getSharedPreferences(
                                "settings", Context.MODE_PRIVATE).getString("token", "")
                )).getJSONArray("data");

                for (int i = 0; i < jArray.length(); i++)
                {
                    days.add(new Day(
                            jArray.getJSONObject(i).getString("room"),
                            jArray.getJSONObject(i).getString("subject"),
                            jArray.getJSONObject(i).getString("teacherAbbreviation"),
                            jArray.getJSONObject(i).getString("start"),
                            jArray.getJSONObject(i).getString("end")
                    ));
                }
            } catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: Couldn't fetch schedule");
            }

            return null;
        }

        protected void onPostExecute(Void params)
        {
            try
            {
                boolean isNextValid = true;

                // Merge duplicates
                for (int i = 0; i < days.size(); i++)
                {
                    if (isNextValid)
                    {
                        if (days.get(i).getSubject().equals(days.get(i + 1).getSubject()) &&
                                days.get(i).getDate().equals(days.get(i + 1).getDate()))
                        {
                            days.get(i).setEnd(days.get(i + 1).getEnd());
                            days.remove(i + 1);
                            isNextValid = false;
                        }
                    } else
                    {
                        isNextValid = true;
                    }
                }

                adapter = new ScheduleAdapter(days, getContext());
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

                showDaySchedule();
            } catch (Exception ex)
            {
                Log.e(TAG, "onPostExecute: Exception", ex);
            }
        }
    }
}
