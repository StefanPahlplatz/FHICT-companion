package s.pahlplatz.fhict_companion.views.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import s.pahlplatz.fhict_companion.utils.NetworkState;

/**
 * Activity to determine in which mode the app will be launched, online or offline.
 */
public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasConnection = NetworkState.isActive(getBaseContext());
        Log.i(TAG, "onCreate: ACTIVE INTERNET CONNECTION: " + hasConnection);

        Intent intent = new Intent(getBaseContext(), hasConnection ? LoginActivity.class : MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("online", hasConnection);

        startActivity(intent);
        this.finish();
    }
}
