package s.pahlplatz.fhict_companion.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

//TODO: Merge teachers too if the block is the same.

/**
 * Schedule model to host all information about the schedule.
 */
public class Schedule implements java.io.Serializable {
    private static final String TAG = Schedule.class.getSimpleName();

    private final ArrayList<Week> weeks;

    public Schedule() {
        weeks = new ArrayList<>();
    }

    /**
     * Returns the week corresponding to the passed index.
     * Returns null if the week array doesn't contain any items.
     *
     * @param i index.
     * @return Week.
     */
    public Week getWeek(final int i) {
        if (weeks.size() == 0) {
            return null;
        }
        return weeks.get(i);
    }

    /**
     * Add a week to the schedule.
     *
     * @param week to add.
     */
    public void addWeek(final Week week) {
        weeks.add(week);
    }

    /**
     * Add a block to the schedule.
     *
     * @param block   to add.
     * @param current date of the block you want to add.
     */
    public void addBlock(final Block block, final Date current) {
        for (int i = 0; i < weeks.size(); i++) {
            // If the date is within the week.
            if (current.compareTo(weeks.get(i).getStart()) != -1
                    && current.compareTo(weeks.get(i).getEnd()) != 1) {
                if (weeks.get(i).hasDate(current) != -1) {
                    // If the week already has the day.
                    weeks.get(i).addBlock(block, weeks.get(i).hasDate(current));
                } else {
                    // Else create the day and add the block.
                    Day day = new Day(current);
                    day.addBlock(block);
                    weeks.get(i).addDay(day);
                }
                return;
            }
        }
        Log.e(TAG, "addBlock: No week found to add the block to! - " + block.toString() + " " + current);
    }

    /**
     * Returns the number of the week we're currently in.
     *
     * @param date Date of the date you want to check.
     * @return week number. Returns -1 if the week is not found.
     */
    public int getWeekFromDate(final Date date) {
        for (int i = 0; i < weeks.size(); i++) {
            if (i + 1 < weeks.size()) {
                if (date.compareTo(weeks.get(i).getStart()) != -1
                        && date.compareTo(weeks.get(i + 1).getStart()) == -1) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getAmountOfWeeks() {
        return weeks.size();
    }

    /**
     * Calls Day.mergeDuplicates on every one of the blocks in the schedule.
     */
    public void mergeBlocks() {
        for (Week week : weeks) {
            for (Day day : week.getDays()) {
                day.mergeDuplicates();
            }
        }
    }

    /**
     * Calls Day.addBreaks on every one of the blocks in the schedule.
     */
    public void insertBreaks() {
        for (Week week : weeks) {
            for (Day day : week.getDays()) {
                day.addBreaks();
            }
        }
    }

    /**
     * Returns a string[] of all the weeks in the schedule (e.g. 'Week 3').
     */
    public String[] getWeekNrs() {
        ArrayList<String> retList = new ArrayList<>();
        for (Week week : weeks) {
            retList.add("Week " + String.valueOf(week.getWeekNr()));
        }

        String[] retArray = new String[retList.size()];
        return retList.toArray(retArray);
    }

    /**
     * Clear the weeks from the list.
     */
    public void clear() {
        for (int i = 0; i < weeks.size(); i++) {
            weeks.get(i).dispose();
        }
        weeks.clear();
    }

    @Override
    public String toString() {
        String retString = "";
        for (Week week : weeks) {
            retString += week.toString() + "\n";
        }
        return retString;
    }
}
