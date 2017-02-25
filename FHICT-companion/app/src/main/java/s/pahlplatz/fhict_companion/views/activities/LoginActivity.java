package s.pahlplatz.fhict_companion.views.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.views.fragments.TokenFragment;

/**
 * Activity that hosts the login fragment.
 */
public class LoginActivity extends AppCompatActivity implements TokenFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass;
            fragmentClass = TokenFragment.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_login, fragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(String token) {
        // Store the user token
        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", token).apply();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("online", true);
        startActivity(intent);
        finish();
    }
}
