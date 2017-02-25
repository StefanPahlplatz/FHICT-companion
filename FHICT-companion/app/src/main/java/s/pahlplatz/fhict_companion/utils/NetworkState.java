package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkState {
    /**
     * Global var of whether we had internet connection the last time we checked.
     */
    public static boolean ONLINE;

    /**
     * Checks if the device has an active internet connection.
     *
     * @param ctx context
     * @return active connection
     */
    public static boolean isActive(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
