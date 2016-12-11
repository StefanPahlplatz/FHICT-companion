package s.pahlplatz.fhict_companion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.fragments.TokenFragment;

/**
 * A login screen that offers login via pcn/password.
 */
public class LoginActivity extends AppCompatActivity implements TokenFragment.OnFragmentInteractionListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null)
        {
            Fragment fragment = null;
            Class fragmentClass;
            fragmentClass = TokenFragment.class;

            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // Load default fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.login_frame, fragment).commit();
        }
    }

    /**
     * Triggered when the user logged in
     *
     * @param token is the string that is returned from the auth
     */
    public void onFragmentInteraction(String token)
    {
        // Store the user token
        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", token).apply();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}