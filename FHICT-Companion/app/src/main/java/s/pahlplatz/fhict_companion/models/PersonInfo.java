package s.pahlplatz.fhict_companion.models;

/**
 * Created by Stefan on 3-3-2017.
 * <p>
 * Class to hold the details about a person.
 * Left is for the constant part, like 'Title' or 'Name'.
 * Right is for the variable.
 */

public final class PersonInfo {
    private final String left;
    private final String right;

    private PersonInfo(final String left, final String right) {
        if (left.equals("") || right.equals("")) {
            throw new RuntimeException("PersonInfo has an empty string. Are you sure you initialized the object with the createInfo method?");
        }

        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    /**
     * Validate the info.
     * @param left constant.
     * @param right variable.
     * @return personInfo object.
     */
    public static PersonInfo createInfo(final String left, final String right) {
        if (left != null && right != null) {
            if (!right.equals("")) {
                String rightNew = right.replace("[", "").replace("]", "").replace("\"", "").replace(",", ", ");
                return new PersonInfo(left, rightNew);
            }
        }
        return null;
    }
}
