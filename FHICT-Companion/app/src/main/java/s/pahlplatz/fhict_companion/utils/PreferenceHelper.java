package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Stefan on 4-3-2017.
 * <p>
 * Wrapper for preferences.
 */

public final class PreferenceHelper {
    public static final String TOKEN = "token";
    public static final String DISPLAY_NAME = "displayName";
    public static final String CLASS_ID = "classId";
    public static final String CLASS_NAME = "className";
    public static final String AMOUNT_OF_NEWS_ITEMS = "amountOfNewsItems";
    public static final String USER_ID = "id";
    public static final String USER_TITLE = "title";
    public static final String PROFILE_PICTURE_URL = "title";

    private PreferenceHelper() {
        // Not called.
    }

    /**
     * Saves a string to preferences.
     *
     * @param ctx  context.
     * @param var  name of the variable.
     * @param data string to save.
     */
    public static void save(final Context ctx, final String var, final String data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        sharedPreferences.edit().putString(var, data).apply();
    }

    /**
     * Saves an integer to preferences.
     *
     * @param ctx  context.
     * @param var  name of the variable.
     * @param data int to save.
     */
    public static void save(final Context ctx, final String var, final int data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        sharedPreferences.edit().putInt(var, data).apply();
    }

    /**
     * Gets a string from preferences.
     *
     * @param ctx context.
     * @param var name of the variable to retrieve.
     */
    public static String getString(final Context ctx, final String var) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString(var, "");
    }

    /**
     * Gets an integer from preferences.
     *
     * @param ctx          context.
     * @param var          name of the variable to retrieve.
     * @param defaultValue value to return when no value in found.
     */
    public static int getInt(final Context ctx, final String var, final int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getInt(var, defaultValue);
    }
}
