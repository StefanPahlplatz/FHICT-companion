package s.pahlplatz.fhict_companion.utils.models;

/**
 * Created by Stefan on 4-12-2016.
 * <p>
 * Class that represents a week in the schedule
 */

public class Day
{

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
        this.start = start.substring(11, 15);
        this.end = end.substring(11, 15);
        this.date = start.substring(0, 9);
    }
}
