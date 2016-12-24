package s.pahlplatz.fhict_companion.fragments;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.ScheduleAdapter;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.WrapContentLinearLayoutManager;
import s.pahlplatz.fhict_companion.utils.models.Block;
import s.pahlplatz.fhict_companion.utils.models.Day;
import s.pahlplatz.fhict_companion.utils.models.Schedule;
import s.pahlplatz.fhict_companion.utils.models.Week;

/**
 * Created by Stefan on 22-12-2016.
 *
 * Fragment to show the schedule
 */

public class ScheduleFragment extends Fragment
{
    private static final String TAG = ScheduleFragment.class.getSimpleName();

    private Schedule schedule;          // List to store all information from api
    private String[] days;
    private String[] weeks;
    private int week;
    private int day;

    // UI references
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Spinner dropdownWeeks;
    private Spinner dropdownDays;
    private TextView noData;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        schedule = new Schedule();
        days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Set toolbar title
        getActivity().setTitle("Schedule");

        // Assign UI elements
        recyclerView = (RecyclerView) view.findViewById(R.id.schedule_recycler);
        progressBar = (ProgressBar) view.findViewById(R.id.schedule_pbar);
        dropdownWeeks = (Spinner) view.findViewById(R.id.schedule_spinner_week);
        dropdownDays = (Spinner) view.findViewById(R.id.schedule_spinner_day);
        noData = (TextView) view.findViewById(R.id.schedule_no_data);
        Button prevWeek = (Button) view.findViewById(R.id.schedule_week_prev);
        Button nextWeek = (Button) view.findViewById(R.id.schedule_week_next);
        Button prevDay = (Button) view.findViewById(R.id.schedule_day_prev);
        Button nextDay = (Button) view.findViewById(R.id.schedule_day_next);

        // Configure day spinner
        ArrayAdapter<String> adapter_days = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, days);
        dropdownDays.setAdapter(adapter_days);
        setCurrentDay();                // Show current day in spinner
        dropdownDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                day = i;
                showSchedule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        // Configure week spinner
        dropdownWeeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                week = i;
                showSchedule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        // Configure buttons
        prevWeek.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                week--;
                if (week < 0)
                    week = weeks.length - 1;
                dropdownWeeks.setSelection(week);
                showSchedule();
            }
        });
        nextWeek.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                week++;
                if (week >= weeks.length)
                    week = 0;
                dropdownWeeks.setSelection(week);
                showSchedule();
            }
        });
        prevDay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                day--;
                if (day < 0)
                    day = days.length - 1;
                dropdownDays.setSelection(day);
                showSchedule();
            }
        });
        nextDay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                day++;
                if (day > days.length - 1)
                    day = 0;
                dropdownDays.setSelection(day);
                showSchedule();
            }
        });

        // Configure recyclerView
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Configure progressbar
        progressBar.setVisibility(View.VISIBLE);

        // Load the schedule
        new LoadSchedule().execute();

        return view;
    }

    /**
     * Set the current day as the selected day in the day spinner
     */
    private void setCurrentDay()
    {
        Date date = new Date();
        day = getDayAsInt(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()));
        dropdownDays.setSelection(day);
    }

    /**
     * Set the current week as the selected week in the week spinner
     */
    private void setCurrentWeek()
    {
        Date date = new Date();
        week = schedule.getWeekFromDate(date);
        dropdownWeeks.setSelection(week);
    }

    /**
     * Show the schedule. If the current day has no info, show the 'no data' label instead
     */
    private void showSchedule()
    {
        Day scheduleDay = null;
        try
        {
            scheduleDay = schedule.getWeek(week).getDay(day);
        } catch (Exception ex)
        {
            Log.e(TAG, "showSchedule: Tried to call showSchedule before the schedule was loaded");
        }
        if (scheduleDay == null)
        {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else
        {
            ScheduleAdapter adapter = new ScheduleAdapter(scheduleDay);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Convert the day string into an int
     *
     * @param day string
     * @return day represented as integer
     */
    private int getDayAsInt(String day)
    {
        for (int i = 0; i < days.length; i++)
        {
            if (day.equals(days[i]))
                return i;
        }
        return -1;
    }

    /**
     * Async class to load the schedule from the api
     */
    private class LoadSchedule extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            if (schedule != null)
            {
                schedule.clear();
                noData.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                // Get JSONObject from fontys API
                JSONObject jObject = new JSONObject(FhictAPI.getStream(
                        "https://api.fhict.nl/schedule/me?startLastMonday=true&expandWeeks=true",
                        getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getString("token", "")));

                // Add weeks to schedule
                addWeeks(jObject.getJSONArray("weeks"));

                // Loop through all days
                addBlocks(jObject.getJSONArray("data"));
            } catch (JSONException ex)
            {
                Log.e(TAG, "doInBackground: Exception occurred", ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params)
        {
            schedule.mergeBlocks();
            schedule.insertBreaks();

            // Assign adapter for dropdownWeeks
            weeks = schedule.getWeekNrs();
            ArrayAdapter<String> adapter_weeks = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, weeks);
            dropdownWeeks.setAdapter(adapter_weeks);

            setCurrentWeek();

            showSchedule();

            // Hide progressbar
            progressBar.setVisibility(View.GONE);

            // Log the schedule
            //Log.i(TAG, "onPostExecute: \n" + schedule.toString());
        }

        /**
         * Adds all weeks of the given array to the schedule
         *
         * @param jWeeks JSONArray that contains data about the weeks
         */
        private void addWeeks(JSONArray jWeeks)
        {
            try
            {
                for (int i = 0; i < jWeeks.length(); i++)
                {
                    String weekNr = jWeeks.getJSONObject(i).getString("title");
                    String start = jWeeks.getJSONObject(i).getString("start");
                    String end = jWeeks.getJSONObject(i).getString("end");
                    schedule.addWeek(new Week(weekNr, start, end));
                }
            } catch (JSONException ex)
            {
                Log.e(TAG, "addWeeks: Exception while adding weeks", ex);
            }
        }

        /**
         * Adds all blocks of the given array to the schedule
         *
         * @param jDays JSONArray that contains data about the blocks
         */
        private void addBlocks(JSONArray jDays)
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (int i = 0; i < jDays.length(); i++)
            {
                try
                {
                    // Get data from array
                    String room = jDays.getJSONObject(i).getString("room").replace("_", " ");
                    String subject = jDays.getJSONObject(i).getString("subject");
                    String desc = jDays.getJSONObject(i).getString("description");
                    String teacherAbbr = jDays.getJSONObject(i).getString("teacherAbbreviation");
                    String start = jDays.getJSONObject(i).getString("start");
                    String end = jDays.getJSONObject(i).getString("end");
                    Date date = format.parse(start);

                    // Wrap all info in a block object
                    Block block = new Block(room, subject, teacherAbbr, desc, start, end);

                    // Add block to the schedule
                    schedule.addBlock(block, date);
                } catch (ParseException ex)
                {
                    Log.e(TAG, "Week: Exception occurred while converting the start date string to a date object", ex);
                } catch (JSONException ex)
                {
                    Log.e(TAG, "Week: Exception occurred while parsing JSON", ex);
                }
            }
        }
    }
}
