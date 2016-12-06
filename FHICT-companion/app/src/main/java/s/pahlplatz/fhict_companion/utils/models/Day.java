package s.pahlplatz.fhict_companion.utils.models;

import android.util.Log;

/**
 * Created by Stefan on 4-12-2016.
 * <p>
 * Class that represents a week in the schedule
 */

public class Day
{
    private static final String TAG = Day.class.getSimpleName();

    private String room;
    private String subject;
    private String teacherAbbr;
    private String date;
    private String start;
    private String end;

    public Day(String room, String subject, String teacherAbbr, String start, String end)
    {
        this.room = room;
        this.subject = subject;
        this.teacherAbbr = teacherAbbr;
        this.start = start.substring(11, 16);
        this.end = end.substring(11, 16);
        this.date = start.substring(0, 10);
        Log.i(TAG, "Day: date  = " + this.date);
    }

    public String getEnd()
    {
        return end;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public String getStart()
    {
        return start;
    }

    public String getDate()
    {
        return date;
    }

    public String getTeacherAbbr()
    {
        return teacherAbbr;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getRoom()
    {
        return room;
    }
}
