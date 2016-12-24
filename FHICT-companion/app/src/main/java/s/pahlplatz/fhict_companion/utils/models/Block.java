package s.pahlplatz.fhict_companion.utils.models;

/**
 * Created by Stefan on 22-12-2016.
 * <p>
 * Class that represents a block in the schedule
 */

public class Block
{
    private String room;                // Room
    private String subject;             // Subject
    private String teacherAbbr;         // Teacher
    private String desc;                // Description
    private String start;               // Start - TIME!
    private String end;                 // End - TIME!

    /**
     * Default constructor
     */
    public Block(String room, String subject, String teacherAbbr, String desc, String start, String end)
    {
        this.room = room;
        this.subject = subject;
        this.teacherAbbr = teacherAbbr;
        this.desc = desc;
        this.start = start.substring(11, 16);
        this.end = end.substring(11, 16);
    }

    /**
     * Constructor for breaks
     *
     * @param start full datetime string
     * @param end   full datetime string
     */
    Block(String start, String end)
    {
        this.room = "";
        this.subject = "Break";
        this.teacherAbbr = "";
        this.start = start;
        this.end = end;
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

    @Override
    public String toString()
    {
        if (subject.equals("Break"))
            return "Break";
        else
            return String.format("Subject: %-10s\tRoom: %-10s\tDesc: %-10s\tTeacher: %-10s\tStart: %-10s\tEnd: %-10s",
                    subject, room, desc, teacherAbbr, start, end);
    }
}
