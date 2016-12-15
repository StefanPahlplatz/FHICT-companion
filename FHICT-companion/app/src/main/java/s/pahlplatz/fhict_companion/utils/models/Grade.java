package s.pahlplatz.fhict_companion.utils.models;

import android.support.annotation.NonNull;

/**
 * Created by Stefan on 30-11-2016.
 *
 * Class to store grades
 */

public class Grade implements Comparable<Grade>
{
    private final String name;
    private final double grade;

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

    public int compareTo(@NonNull Grade grade)
    {
        double compareGrade = grade.getGrade();

        return (int) (this.getGrade() - compareGrade);
    }
}
