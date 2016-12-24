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

    /**
     * Default constructor
     *
     * @param name  of the class
     * @param grade >= 0 and =< 10
     */
    public Grade(String name, double grade)
    {
        this.name = name;
        this.grade = grade;
    }

    /**
     * Returns the name of the class
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the grade
     * @return double
     */
    public double getGrade()
    {
        return grade;
    }

    /**
     * Custom compareTo method that compares grades by grade
     * @param grade grade to compare to
     * @return -1, 0 or 1
     */
    public int compareTo(@NonNull Grade grade)
    {
        double compareGrade = grade.getGrade();

        return (int) (this.getGrade() - compareGrade);
    }
}
