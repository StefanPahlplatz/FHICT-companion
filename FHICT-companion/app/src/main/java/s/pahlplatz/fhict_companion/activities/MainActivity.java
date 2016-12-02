package s.pahlplatz.fhict_companion.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.fragments.CoworkersFragment;
import s.pahlplatz.fhict_companion.fragments.GradeFragment;
import s.pahlplatz.fhict_companion.fragments.NewsFragment;
import s.pahlplatz.fhict_companion.fragments.ParticipationFragment;
import s.pahlplatz.fhict_companion.fragments.ScheduleFragment;
import s.pahlplatz.fhict_companion.fragments.TokenFragment;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.LoadProfilePicture;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TokenFragment.OnFragmentInteractionListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

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

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_main_content_frame, fragment).commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    public void onFragmentInteraction(String token)
    {
        // Store the user token
        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", token).apply();

        // Set the profile picture
        new LoadProfilePicture().execute(getBaseContext(), findViewById(R.id.header_profile_image));

        // Get id and name of the user
        new LoadUserData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Fragment fragment;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_coworkers)
        {
            fragmentClass = CoworkersFragment.class;
        } else if (id == R.id.nav_notifications)
        {
            fragmentClass = TokenFragment.class; // TODO: replace
        } else if (id == R.id.nav_schedule)
        {
            fragmentClass = ScheduleFragment.class;
        } else if (id == R.id.nav_news)
        {
            fragmentClass = NewsFragment.class;
        } else if (id == R.id.nav_participation)
        {
            fragmentClass = ParticipationFragment.class;
        } else if (id == R.id.nav_results)
        {
            fragmentClass = GradeFragment.class;
        }

        try
        {
            if (fragmentClass != null)
            {
                fragment = (Fragment) fragmentClass.newInstance();

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.activity_main_content_frame, fragment).commit();
            }
        } catch (Exception ex)
        {
            Log.e(TAG, "onNavigationItemSelected: Couldn't switch fragments", ex);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Get user info from api
    private class LoadUserData extends AsyncTask<Void, Void, ArrayList<String>>
    {
        @Override
        protected ArrayList<String> doInBackground(Void... params)
        {
            ArrayList<String> retList = new ArrayList<>();

            try
            {
                // Convert the InputStream to a JSONArray
                JSONObject jObject = new JSONObject(FhictAPI.getStream("https://api.fhict.nl/people/me", getSharedPreferences("settings", MODE_PRIVATE).getString("token", "")));

                // Store info
                SharedPreferences.Editor edit = getSharedPreferences("settings", MODE_PRIVATE).edit();
                edit.putString("id", jObject.getString("id"));
                edit.putString("displayName", jObject.getString("displayName"));
                edit.putString("title", jObject.getString("title"));
                edit.putString("photo", jObject.getString("photo"));
                edit.apply();

                // Return name and title
                retList.add(jObject.getString("displayName"));
                retList.add(jObject.getString("title"));
            }catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: A problem occurred while parsing the JSON file.", ex);
            }
            return retList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result)
        {
            try
            {
                TextView name = (TextView) findViewById(R.id.header_tv_name);
                name.setText(result.get(0));

                TextView title = (TextView) findViewById(R.id.header_tv_title);
                title.setText(result.get(1));
            } catch (Exception ex)
            {
                Log.e(TAG, "onPostExecute: Couldn't set the header textviews.", ex);
            }
        }
    }
}
