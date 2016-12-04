package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.models.Day;

/**
 * Fragment to show your schedule
 */
public class ScheduleFragment extends Fragment
{
    private static final String TAG = ScheduleFragment.class.getSimpleName();

    private ArrayList<Day> days;

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
        Spinner dropdownWeeks = (Spinner) view.findViewById(R.id.schedule_spinner);
        String[] items = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdownWeeks.setAdapter(adapter);

        // Set the current day as selected day
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        dropdownWeeks.setSelection(getCurrentDayAsInt(day));


        return view;
    }

    /**
     * Convert the day string into an int
     * If the day is sunday or saturday return 0 anyway.
     * @param day string
     * @return day representation as integer
     */
    private int getCurrentDayAsInt(String day)
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

    /**
     * Async class to load the schedule
     */
    public class loadSchedule extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {

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
                //
            } catch (NullPointerException ex)
            {
                Log.e(TAG, "onPostExecute: Couldn't load schedule, view changed before onPostExecute triggered?");
            }
        }
    }
}
