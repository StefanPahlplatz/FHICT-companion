package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import s.pahlplatz.fhict_companion.utils.PreferenceHelper;

/**
 * Controller for the schedule fragment.
 */
public class ScheduleController {
    private static final String TAG = ScheduleController.class.getSimpleName();
    /**
     * Turns 'Monday' into 'Mon' for comparison.
     */
    private static final int SHORT_DAY = 3;
    private static final String[] DAYS = new String[]{"Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};

    private final Context ctx;
    /**
     * Reference to the view hosting the schedule.
     */
    private final ScheduleListener listener;
    private Schedule schedule;
    private String[] weeks;
    private int week = 0;
    private int day = 0;

    /**
     * @param ctx      context.
     * @param listener reference to the caller.
     */
    public ScheduleController(final Context ctx, final ScheduleListener listener) {
        this.ctx = ctx;
        this.listener = listener;

        if (NetworkState.isOnline()) {
            new LoadSchedule().execute();
        } else {
            try {
                // Try to load a schedule from storage.
                schedule = (Schedule) LocalPersistence.readObjectFromFile(ctx, "schedule");
            } catch (ClassCastException ex) {
                Log.e(TAG, "ScheduleController: Couldn't convert the saved schedule to a schedule object.");
            }

            // If there is no schedule saved.
            if (schedule == null) {
                listener.noSchedule();
            } else {
                weeks = schedule.getWeekNrs();
                setCurrentWeek();
                setCurrentDay();
                setToday();
            }
        }
    }

    /**
     * Views today's schedule.
     */
    public void setToday() {
        if (schedule == null) {
            return;
        }

        day = getCurrentDay();
        week = getCurrentWeek();

        listener.setDaySpinner(DAYS[day]);
        if (weeks != null) {
            listener.setWeekSpinner(weeks[week]);
        }
    }

    public void prevWeek() {
        if (weeks != null) {
            week--;
            if (week < 0) {
                week = weeks.length - 1;
            }
            listener.setWeekSpinner(weeks[week]);
            listener.showSchedule();
        }
    }

    public void nextWeek() {
        if (weeks != null) {
            week++;
            if (week >= weeks.length) {
                week = 0;
            }
            listener.setWeekSpinner(weeks[week]);
            listener.showSchedule();
        }
    }

    public void prevDay() {
        if (schedule != null) {
            day--;
            if (day < 0) {
                day = DAYS.length - 1;

                // If we're not in the first week
                if (week - 1 >= 0) {
                    listener.setWeekSpinner(weeks[--week]);
                } else {
                    Toast.makeText(ctx, "Can't view further back.", Toast.LENGTH_SHORT).show();
                }
            }
            listener.setDaySpinner(DAYS[day]);
            listener.showSchedule();
        }
    }

    public void nextDay() {
        if (schedule != null) {
            day++;
            if (day > DAYS.length - 1) {
                day = 0;
                if (week + 1 < schedule.getAmountOfWeeks()) {
                    listener.setWeekSpinner(weeks[++week]);
                } else {
                    Toast.makeText(ctx, "Can't view any further.", Toast.LENGTH_SHORT).show();
                }
            }
            listener.setDaySpinner(DAYS[day]);
            listener.showSchedule();
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
                    return new ScheduleAdapter(sDay, ctx);
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
    private int getDayAsInt(final String dayAsString) {
        for (int i = 0; i < DAYS.length; i++) {
            if (dayAsString.substring(0, SHORT_DAY).equals(DAYS[i].substring(0, SHORT_DAY))) {
                return i;
            }
        }
        throw new RuntimeException(TAG + " Couldn't find day: '" + dayAsString + "'.");
    }

    /**
     * Sets the current day as selected.
     * Also appends '(today)' to the current day.
     */
    private void setCurrentDay() {
        day = getCurrentDay();
        if (!DAYS[day].contains("(today)")) {
            DAYS[day] += " (today)";
        }
        listener.setDaySpinner(DAYS[day]);
    }

    /**
     * Set the current week as selected.
     * Also appends '(current)' to the current week.
     */
    private void setCurrentWeek() {
        week = getCurrentWeek();

        if (week == -1) {
            listener.setWeekSpinner("Week ?");
            Log.e(TAG, "setCurrentWeek: Couldn't find right week.");
        } else if (weeks == null) {
            Log.e(TAG, "setCurrentWeek: weeks array is null.");
        } else {
            weeks[week] += " (current)";
            listener.setWeekSpinner(weeks[week]);
        }
    }

    /**
     * An interface to be implemented by every class that is using the schedule.
     */
    public interface ScheduleListener {
        void showSchedule();

        void setDaySpinner(String selection);

        void setWeekSpinner(String selection);

        void noSchedule();
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
        protected Void doInBackground(final Void... voids) {
            try {
                // Get JSONObject from fontys API
                JSONObject jObject = new JSONObject(FontysAPI.getStream(
                        "https://api.fhict.nl/schedule/me?startLastMonday=true&expandWeeks=true",
                        PreferenceHelper.getString(ctx, PreferenceHelper.TOKEN)));

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
        protected void onPostExecute(final Void params) {
            if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("merge_same_blocks", true)) {
                schedule.mergeBlocks();
            }

            if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("show_breaks", true)) {
                schedule.insertBreaks();
            }

            setCurrentWeek();
            setCurrentDay();

            listener.showSchedule();

            // Save the schedule if option is selected.
            if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("always_download_schedule", false)) {
                if (LocalPersistence.readObjectFromFile(ctx, "schedule") != schedule) {
                    LocalPersistence.writeObjectToFile(ctx, schedule, "schedule");
                }
            }
        }

        /**
         * Adds all weeks of the given array to the schedule.
         *
         * @param jWeeks JSONArray that contains data about the weeks.
         */
        private void addWeeks(final JSONArray jWeeks) {
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
        private void addBlocks(final JSONArray jDays) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (int i = 0; i < jDays.length(); i++) {
                try {
                    // Get data from array.
                    String room = jDays.getJSONObject(i).getString("room").replace("_", " ");
                    String subject = jDays.getJSONObject(i).getString("subject");
                    String teacherAbbr = jDays.getJSONObject(i).getString("teacherAbbreviation");
                    String start = jDays.getJSONObject(i).getString("start");
                    String end = jDays.getJSONObject(i).getString("end");
                    Date date = format.parse(start);

                    // Wrap all info in a block object.
                    Block block = new Block(room, subject, teacherAbbr, start, end);

                    // Add block to the schedule.
                    schedule.addBlock(block, date);
                } catch (ParseException ex) {
                    Log.e(TAG, "Week: Exception occurred while converting the start date string to a date object", ex);
                } catch (JSONException ex) {
                    Log.e(TAG, "Week: Exception occurred while parsing JSON", ex);
                }
            }
        }
    }
}
