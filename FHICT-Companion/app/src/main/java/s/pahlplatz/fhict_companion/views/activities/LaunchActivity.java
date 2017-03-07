package s.pahlplatz.fhict_companion.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import s.pahlplatz.fhict_companion.utils.NetworkState;
import s.pahlplatz.fhict_companion.utils.PreferenceHelper;

/**
 * Activity to determine in which mode the app will be launched, online or offline.
 */
public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasConnection = NetworkState.isActive(getBaseContext());
        Log.i(TAG, "onCreate: ACTIVE INTERNET CONNECTION: " + hasConnection);

        Intent intent;
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("always_offline", false)) {
            intent = new Intent(getBaseContext(), MainActivity.class);
        } else {
            intent = new Intent(getBaseContext(), hasConnection ? LoginActivity.class : MainActivity.class);
        }
        PreferenceHelper.save(getBaseContext(), PreferenceHelper.STARTED_ONLINE, hasConnection);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        this.finish();
    }
}
