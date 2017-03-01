package s.pahlplatz.fhict_companion.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Stefan on 30-11-2016.
 * <p>
 * Static class to manually hide the keyboard.
 */
public final class KeyboardManager {
    private KeyboardManager() {
        // Not called.
    }

    /**
     * Hides the keyboard.
     *
     * @param activity current activity
     */
    public static void hide(final Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it.
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
