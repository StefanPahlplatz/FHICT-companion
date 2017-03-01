package s.pahlplatz.fhict_companion.models;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stefan on 22-12-2016.
 * <p>
 * Wrapper for weeks in the schedule
 */

public class Week implements java.io.Serializable {
    private static final String TAG = Week.class.getSimpleName();

    private int weekNr;
    private Date start;
    private Date end;
    private ArrayList<Day> days;

    /**
     * @param weekNr     the week number.
     * @param startParam start date as string.
     * @param endParam   end date as string.
     */
    public Week(final String weekNr, final String startParam, final String endParam) {
        // Assign the week nr
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(weekNr);
        while (m.find()) {
            this.weekNr = Integer.parseInt(m.group());
        }

        DateFormat format = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

        // Assign the start date.
        try {
            this.start = format.parse(startParam);
        } catch (ParseException ex) {
            Log.e(TAG, "Week: Exception occurred while converting the start date of the week to a date object", ex);
        }

        // Assign the end date.
        try {
            this.end = format.parse(endParam);
        } catch (ParseException ex) {
            Log.e(TAG, "Week: Exception occurred while converting the end date of the week to a date object", ex);
        }

        // Initialize the ArrayList.
        days = new ArrayList<>();
    }

    /**
     * Checks if the week has a specific date.
     *
     * @param date that you want to check as Date.
     * @return index of the day, otherwise -1.
     */
    int hasDate(final Date date) {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getDate().equals(date)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns all the days in the week.
     *
     * @return ArrayList of all days.
     */
    ArrayList<Day> getDays() {
        return days;
    }

    /**
     * Returns the day corresponding to the passed index.
     *
     * @param i index.
     * @return Day.
     */
    public Day getDay(final int i) {
        if (i > days.size() - 1) {
            return null;
        }
        return days.get(i);
    }

    /**
     * Adds a block to the specified day.
     *
     * @param block to add.
     * @param i     index of the day you want to add the block to.
     */
    void addBlock(final Block block, final int i) {
        days.get(i).addBlock(block);
    }

    /**
     * Adds a day to the list.
     *
     * @param day to add.
     */
    void addDay(final Day day) {
        days.add(day);
    }

    /**
     * @return the week number.
     */
    int getWeekNr() {
        return weekNr;
    }

    /**
     * @return the start date as Date.
     */
    public Date getStart() {
        return start;
    }

    /**
     * @return the end date as Date.
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Disposes of the week.
     */
    void dispose() {
        for (int i = 0; i < days.size(); i++) {
            days.get(i).dispose();
        }
        days.clear();
        days = null;
    }

    @Override
    public String toString() {
        String retString = String.format(Locale.getDefault(), "week: %1d,\tstart: %2s,\tend: %3s\n",
                getWeekNr(), getStart().toString(), getEnd().toString());

        for (Day item : days) {
            retString += "\t" + item.toString() + "\n";
        }

        return retString;
    }
}
