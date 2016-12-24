package s.pahlplatz.fhict_companion.utils.models;

/**
 * Created by Stefan on 22-12-2016.
 * <p>
 * Class that represents a block in the schedule
 */

public class Block
{
    private final String room;          // Room
    private final String subject;       // Subject
    private final String teacherAbbr;   // Teacher
    private final String start;         // Start - TIME!
    private String desc;                // Description
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

    /**
     * Returns the end time of the block
     *
     * @return String in hh:mm format
     */
    public String getEnd()
    {
        return end;
    }

    /**
     * Sets the end time of the block
     * @param end String in hh:mm format
     */
    public void setEnd(String end)
    {
        this.end = end;
    }

    /**
     * Returns the start time of the block
     * @return String in hh:mm format
     */
    public String getStart()
    {
        return start;
    }

    /**
     * Returns the abbreviation of the teacher
     * @return String
     */
    public String getTeacherAbbr()
    {
        return teacherAbbr;
    }

    /**
     * Returns the subject of the block
     * @return String
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * Returns the room of the block
     * @return String
     */
    public String getRoom()
    {
        return room;
    }

    /**
     * Returns the description of the block
     *
     * @return String
     */
    public String getDesc()
    {
        return desc;
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
