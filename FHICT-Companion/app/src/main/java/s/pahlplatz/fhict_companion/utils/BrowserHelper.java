package s.pahlplatz.fhict_companion.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Created by Stefan on 2-3-2017.
 * <p>
 * Helper class for browser actions.
 */
public final class BrowserHelper {
    private static final String TAG = BrowserHelper.class.getSimpleName();

    private BrowserHelper() {
        // Not called.
    }

    /**
     * Clears the browsers cookies.
     *
     * @param context context.
     */
    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    public static void clearCookies(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
