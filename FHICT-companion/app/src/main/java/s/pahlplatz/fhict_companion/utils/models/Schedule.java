package s.pahlplatz.fhict_companion.utils.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class Schedule implements java.io.Serializable {
    private static final String TAG = Schedule.class.getSimpleName();

    private final ArrayList<Week> weeks;

    public Schedule() {
        weeks = new ArrayList<>();
    }

    /**
     * Returns the week corresponding to the passed index
     *
     * @param i index
     * @return Week
     */
    public Week getWeek(int i) {
        return weeks.get(i);
    }

    /**
     * Add a week to the schedule
     *
     * @param week to add
     */
    public void addWeek(Week week) {
        weeks.add(week);
    }

    /**
     * Add a block to the schedule, not used to block the adds
     *
     * @param block   to add
     * @param current Date of the block you want to add
     */
    public void addBlock(Block block, Date current) {
        for (int i = 0; i < weeks.size(); i++) {
            // If the date is within the week
            if (current.compareTo(weeks.get(i).getStart()) != -1 &&
                    current.compareTo(weeks.get(i).getEnd()) != 1) {
                // If the week already has the day
                if (weeks.get(i).hasDate(current) != -1) {
                    weeks.get(i).addBlock(block, weeks.get(i).hasDate(current));
                }
                // Else create the day and add the block
                else {
                    Day day = new Day(current);
                    day.addBlock(block);
                    weeks.get(i).addDay(day);
                }
                return;
            }
        }
        Log.e(TAG, "addBlock: NO WEEK FOUND TO ADD THE BLOCK TO! - " + block.toString() + " " + current);
    }

    /**
     * Returns the number of the week you are currently in
     *
     * @param date Date of the date you want to check
     * @return week number
     */
    public int getWeekFromDate(Date date) {
        for (int i = 0; i < weeks.size(); i++) {
            if (i + 1 < weeks.size()) {
                if (date.compareTo(weeks.get(i).getStart()) != -1 && date.compareTo(weeks.get(i + 1).getStart()) == -1) {
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
     * Calls Day.mergeDuplicates on every one of the blocks in the schedule
     */
    public void mergeBlocks() {
        for (Week week : weeks)
            for (Day day : week.getDays())
                day.mergeDuplicates();
    }

    /**
     * Calls Day.addBreaks on every one of the blocks in the schedule
     */
    public void insertBreaks() {
        for (Week week : weeks)
            for (Day day : week.getDays())
                day.addBreaks();
    }

    /**
     * Returns a string[] of all the weeks in the schedule
     *
     * @return string[]
     */
    public String[] getWeekNrs() {
        ArrayList<String> retList = new ArrayList<>();
        for (Week week : weeks)
            retList.add("Week " + String.valueOf(week.getWeekNr()));

        String retArray[] = new String[retList.size()];
        return retList.toArray(retArray);
    }

    /**
     * Clear the weeks from the list
     */
    public void clear() {
        for (int i = 0; i < weeks.size(); i++)
            weeks.get(i).dispose();
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
