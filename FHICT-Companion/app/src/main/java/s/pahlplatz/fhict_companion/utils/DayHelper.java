package s.pahlplatz.fhict_companion.utils;

/**
 * Helper class to get the days of the week and converting them.
 */

public final class DayHelper {
    public static final String[] DAYS = new String[]{"Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};

    /**
     * Turns 'Monday' into 'Mon' for comparison.
     */
    private static final int SHORT_DAY = 3;

    /**
     * Convert the day string into an int.
     *
     * @param dayAsString string, for example 'Monday'.
     * @return day represented as integer.
     * @throws RuntimeException when it can't find the given day.
     */
    public static int getDayAsInt(final String dayAsString) {
        for (int i = 0; i < DAYS.length; i++) {
            if (dayAsString.substring(0, SHORT_DAY).equals(DAYS[i].substring(0, SHORT_DAY))) {
                return i;
            }
        }
        throw new RuntimeException(DayHelper.class.getSimpleName() + " Couldn't find day: '" +
                dayAsString + "'.");
    }
}
