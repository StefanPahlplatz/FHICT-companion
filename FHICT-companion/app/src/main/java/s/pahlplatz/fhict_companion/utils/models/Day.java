package s.pahlplatz.fhict_companion.utils.models;

/**
 * Created by Stefan on 4-12-2016.
 * <p>
 * Class that represents a week in the schedule
 */

public class Day
{
    private final String room;
    private final String subject;
    private final String teacherAbbr;
    private final String date;
    private final String start;
    private String end;

    public Day(String room, String subject, String teacherAbbr, String start, String end)
    {
        this.room = room;
        this.subject = subject;
        this.teacherAbbr = teacherAbbr;
        this.start = start.substring(11, 16);
        this.end = end.substring(11, 16);
        this.date = start.substring(0, 10);
    }

    public Day(String start, String end, String date)
    {
        this.room = "";
        this.subject = "Break";
        this.teacherAbbr = "";
        this.start = start;
        this.end = end;
        this.date = date;
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
