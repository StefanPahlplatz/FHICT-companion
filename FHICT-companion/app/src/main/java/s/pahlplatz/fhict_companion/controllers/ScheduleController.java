package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import s.pahlplatz.fhict_companion.adapters.ScheduleAdapter;
import s.pahlplatz.fhict_companion.models.Block;
import s.pahlplatz.fhict_companion.models.Day;
import s.pahlplatz.fhict_companion.models.Schedule;
import s.pahlplatz.fhict_companion.models.Week;
import s.pahlplatz.fhict_companion.utils.FontysAPI;
import s.pahlplatz.fhict_companion.utils.LocalPersistence;
import s.pahlplatz.fhict_companion.utils.NetworkState;

public class ScheduleController {
    private static final String TAG = ScheduleController.class.getSimpleName();
    private final static String[] days = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private Context ctx;
    private Schedule schedule;
    private String[] weeks;
    private int week = 0;
    private int day = 0;

    // Reference to the view hosting the schedule.
    private ScheduleListener scheduleListener;

    /**
     * @param ctx      context.
     * @param listener reference to the caller.
     */
    public ScheduleController(Context ctx, ScheduleListener listener) {
        this.ctx = ctx;
        this.scheduleListener = listener;

        if (NetworkState.ONLINE) {
            new LoadSchedule().execute();
        } else {
            // Try to load a schedule from storage.
            schedule = (Schedule) LocalPersistence.readObjectFromFile(ctx, "schedule");

            // If there is no schedule saved.
            if (schedule == null) {
                scheduleListener.onNoSchedule();
            } else {
                weeks = schedule.getWeekNrs();

                setToday();
            }
        }
    }

    /**
     * Views today's schedule.
     */
    public void setToday() {
        day = getCurrentDay();
        week = getCurrentWeek();

        scheduleListener.onDaySpinner(days[day]);
        scheduleListener.onWeekSpinner(weeks[week]);
    }

    public void prevWeek() {
        if (weeks != null) {
            week--;
            if (week < 0) {
                week = weeks.length - 1;
            }
            scheduleListener.onWeekSpinner(weeks[week]);
            scheduleListener.onShowSchedule();
        }
    }

    public void nextWeek() {
        if (weeks != null) {
            week++;
            if (week >= weeks.length) {
                week = 0;
            }
            scheduleListener.onWeekSpinner(weeks[week]);
            scheduleListener.onShowSchedule();
        }
    }

    public void prevDay() {
        if (schedule != null) {
            day--;
            if (day < 0) {
                day = days.length - 1;

                // If we're not in the first week
                if (week - 1 >= 0) {
                    scheduleListener.onWeekSpinner(weeks[--week]);
                } else {
                    Toast.makeText(ctx, "Can't view further back.", Toast.LENGTH_SHORT).show();
                }
            }
            scheduleListener.onDaySpinner(days[day]);
            scheduleListener.onShowSchedule();
        }
    }

    public void nextDay() {
        if (schedule != null) {
            day++;
            if (day > days.length - 1) {
                day = 0;
                if (week + 1 < schedule.getAmountOfWeeks()) {
                    scheduleListener.onWeekSpinner(weeks[++week]);
                } else {
                    Toast.makeText(ctx, "Can't view any further.", Toast.LENGTH_SHORT).show();
                }
            }
            scheduleListener.onDaySpinner(days[day]);
            scheduleListener.onShowSchedule();
        }
    }

    /**
     * Returns the current day as int.
     */
    private int getCurrentDay() {
        return getDayAsInt(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date().getTime()));
    }

    /**
     * Returns the current week as int.
     */
    private int getCurrentWeek() {
        return schedule.getWeekFromDate(new Date());
    }

    /**
     * Saved the schedule to the device.
     */
    public void save() {
        LocalPersistence.writeObjectToFile(ctx, schedule, "schedule");
        Toast.makeText(ctx, "Download completed!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns the proper adapter for the selected day.
     *
     * @return scheduleAdapter.
     */
    public ScheduleAdapter getAdapter() {
        if (schedule != null) {
            Week sWeek = schedule.getWeek(week);
            if (sWeek != null) {
                Day sDay = sWeek.getDay(day);
                if (sDay != null) {
                    return new ScheduleAdapter(sDay);
                }
            }
        }
        return null;
    }

    /**
     * Convert the day string into an int.
     *
     * @param dayAsString string, for example 'Monday'.
     * @return day represented as integer.
     * @throws RuntimeException when it can't find the given day.
     */
    private int getDayAsInt(String dayAsString) {
        for (int i = 0; i < days.length; i++) {
                return i;
            }
        }
        throw new RuntimeException(TAG + " Couldn't find day: '" + dayAsString + "'.");
    }

    /**
     * An interface to be implemented by every class that is using the schedule.
     */
    public interface ScheduleListener {
        void onShowSchedule();

        void onDaySpinner(String selection);

        void onWeekSpinner(String selection);

        void onNoSchedule();
    }

    /**
     * Async class to load the schedule from the API.
     */
    private class LoadSchedule extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            if (schedule == null) {
                schedule = new Schedule();
            } else {
                schedule.clear();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Get JSONObject from fontys API
                JSONObject jObject = new JSONObject(FontysAPI.getStream(
                        "https://api.fhict.nl/schedule/me?startLastMonday=true&expandWeeks=true",
                        ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("token", "")));

                // Add weeks to schedule
                addWeeks(jObject.getJSONArray("weeks"));

                // Loop through all days
                addBlocks(jObject.getJSONArray("data"));

                weeks = schedule.getWeekNrs();
            } catch (JSONException ex) {
                Log.e(TAG, "doInBackground: Exception occurred", ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            schedule.mergeBlocks();
            schedule.insertBreaks();

            setCurrentWeek();
            setCurrentDay();

            scheduleListener.onShowSchedule();
        }

        /**
         * Adds all weeks of the given array to the schedule.
         *
         * @param jWeeks JSONArray that contains data about the weeks.
         */
        private void addWeeks(JSONArray jWeeks) {
            try {
                for (int i = 0; i < jWeeks.length(); i++) {
                    String weekNr = jWeeks.getJSONObject(i).getString("title");
                    String start = jWeeks.getJSONObject(i).getString("start");
                    String end = jWeeks.getJSONObject(i).getString("end");
                    schedule.addWeek(new Week(weekNr, start, end));
                }
            } catch (JSONException ex) {
                Log.e(TAG, "addWeeks: Exception while adding weeks", ex);
            }
        }

        /**
         * Adds all blocks of the given array to the schedule.
         *
         * @param jDays JSONArray that contains data about the blocks.
         */
        private void addBlocks(JSONArray jDays) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (int i = 0; i < jDays.length(); i++) {
                try {
                    // Get data from array
                    String room = jDays.getJSONObject(i).getString("room").replace("_", " ");
                    String subject = jDays.getJSONObject(i).getString("subject");
                    //String desc = jDays.getJSONObject(i).getString("description");
                    String teacherAbbr = jDays.getJSONObject(i).getString("teacherAbbreviation");
                    String start = jDays.getJSONObject(i).getString("start");
                    String end = jDays.getJSONObject(i).getString("end");
                    Date date = format.parse(start);

                    // Wrap all info in a block object
                    Block block = new Block(room, subject, teacherAbbr, start, end);

                    // Add block to the schedule
                    schedule.addBlock(block, date);
                } catch (ParseException ex) {
                    Log.e(TAG, "Week: Exception occurred while converting the start date string to a date object", ex);
                } catch (JSONException ex) {
                    Log.e(TAG, "Week: Exception occurred while parsing JSON", ex);
                }
            }
        }

        /**
         * Sets the current day as selected.
         * Also appends '(today)' to the current day.
         */
        private void setCurrentDay() {
            day = getCurrentDay();
            days[day] += " (today)";
            scheduleListener.onDaySpinner(days[day]);
        }

        /**
         * Set the current week as selected.
         * Also appends '(current)' to the current week.
         */
        private void setCurrentWeek() {
            week = getCurrentWeek();

            if (week == -1) {
                scheduleListener.onWeekSpinner("Week ?");
                Log.e(TAG, "setCurrentWeek: Couldn't find right week.");
            } else if (weeks == null) {
                Log.e(TAG, "setCurrentWeek: weeks array is null.");
            } else {
                weeks[week] += " (current)";
                scheduleListener.onWeekSpinner(weeks[week]);
            }
        }
    }
}
