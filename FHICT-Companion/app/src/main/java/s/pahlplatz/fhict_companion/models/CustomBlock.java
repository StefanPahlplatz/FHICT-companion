package s.pahlplatz.fhict_companion.models;

/**
 * Custom block for manually added blocks.
 */

public class CustomBlock extends Block {
    private String day;

    public CustomBlock(String room, String subject, String teacherAbbr, String start, String end, String day) {
        super(room, subject, teacherAbbr, start, end);
        this.setStart(start);
        this.setEnd(end);
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(CustomBlock other) {
        return super.equals(other) && this.day.equals(other.day);
    }
}
