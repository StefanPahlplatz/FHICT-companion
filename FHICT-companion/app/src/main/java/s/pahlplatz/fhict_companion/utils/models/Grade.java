package s.pahlplatz.fhict_companion.utils.models;

/**
 * Created by Stefan on 30-11-2016.
 *
 * Class to store grades
 */

public class Grade
{
    private String name;
    private double grade;

    public Grade(String name, double grade)
    {
        this.name = name;
        this.grade = grade;
    }

    public String getName()
    {
        return name;
    }

    public double getGrade()
    {
        return grade;
    }
}
