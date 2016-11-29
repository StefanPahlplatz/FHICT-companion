package s.pahlplatz.fhict_companion.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.fragments.CoworkersFragment;
import s.pahlplatz.fhict_companion.fragments.JsonFragment;
import s.pahlplatz.fhict_companion.fragments.NewsFragment;
import s.pahlplatz.fhict_companion.fragments.NotificationsFragment;
import s.pahlplatz.fhict_companion.fragments.ParticipationFragment;
import s.pahlplatz.fhict_companion.fragments.ResultsFragment;
import s.pahlplatz.fhict_companion.fragments.ScheduleFragment;
import s.pahlplatz.fhict_companion.fragments.TokenFragment;

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
            fragmentClass = CoworkersFragment.class;

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
        Log.i(TAG, "onFragmentInteraction: token=" + token);
        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", token).apply();
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
            fragmentClass = NotificationsFragment.class;
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
            fragmentClass = TokenFragment.class;
        } else if (id == R.id.nav_jsontest)
        {
            fragmentClass = JsonFragment.class;
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
}
