package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class that handles network checks.
 */
public final class NetworkState {
    /**
     * Global var of whether we had internet connection the last time we checked.
     */
    private static boolean online;

    private NetworkState() {
        // Not called.
    }

    public static void setOnline(final boolean o) {
        online = o;
    }

    public static boolean isOnline() {
        return online;
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * @param ctx context.
     * @return active connection.
     */
    public static boolean isActive(final Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
