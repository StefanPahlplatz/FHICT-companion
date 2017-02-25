package s.pahlplatz.fhict_companion.models;

import android.support.annotation.NonNull;

/**
 * Created by Stefan on 30-11-2016.
 * <p>
 * Class to store grades
 */

public class Grade {
    private final String name;
    private final double grade;

    /**
     * Default constructor
     *
     * @param name  of the class
     * @param grade >= 0 and =< 10
     */
    public Grade(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }

    /**
     * Returns the name of the class
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the grade
     *
     * @return double
     */
    public double getGrade() {
        return grade;
    }

    /**
     * Custom compareTo method that compares grades by grade
     * Sorts descending, highest grade first.
     *
     * @param other grade to compare to
     * @return -1, 0 or 1
     */
    public int sortByGradeDesc(@NonNull Grade other) {
        return Double.compare(other.getGrade(), this.getGrade());
    }

    /**
     * Custom compareTo method that compares grades by grade
     * Sorts descending, highest grade first.
     *
     * @param other grade to compare to
     * @return -1, 0 or 1
     */
    public int sortByGradeAsc(@NonNull Grade other) {
        return Double.compare(this.getGrade(), other.getGrade());
    }

    /**
     * Custom compareTo method that compares grades by name
     * Sorts descending, names with a first.
     *
     * @param other grade to compare to
     * @return -1, 0 or 1
     */
    public int sortByNameDesc(@NonNull Grade other) {
        return this.getName().compareTo(other.getName());
    }
}
