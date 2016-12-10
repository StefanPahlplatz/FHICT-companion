package s.pahlplatz.fhict_companion.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.fragments.GradeFragment;
import s.pahlplatz.fhict_companion.fragments.NewsDetailsFragment;
import s.pahlplatz.fhict_companion.fragments.NewsFragment;
import s.pahlplatz.fhict_companion.fragments.ParticipationFragment;
import s.pahlplatz.fhict_companion.fragments.PeopleDetailFragment;
import s.pahlplatz.fhict_companion.fragments.PeopleFragment;
import s.pahlplatz.fhict_companion.fragments.PeopleListFragment;
import s.pahlplatz.fhict_companion.fragments.ScheduleFragment;
import s.pahlplatz.fhict_companion.fragments.TokenFragment;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.LoadProfilePicture;
import s.pahlplatz.fhict_companion.utils.models.NewsItem;
import s.pahlplatz.fhict_companion.utils.models.Person;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TokenFragment.OnFragmentInteractionListener,
        NewsAdapter.OnAdapterInteractionListener,
        PeopleFragment.OnPeopleSearchListener,
        PeopleListFragment.OnFragmentInteractionListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure toolbar
        toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        configureToolbar();

        // Configure navigationView
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

            // Load default fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_main_content_frame, fragment).commit();
        }
    }

    /**
     * Check if the navigation drawer is open, if so close it.
     * Otherwise do the normal action.
     */
    @Override
    public void onBackPressed()
    {
        // Change the icon to the drawer icon
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Get the default configuration
        configureToolbar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            {
                getSupportFragmentManager().popBackStack();
            } else if (doubleBackToExitPressedOnce)
            {
                super.onBackPressed();
            } else
            {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press back again to leave", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
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

        // Set the profile picture
        new LoadProfilePicture().execute(getBaseContext(), findViewById(R.id.header_profile_image));

        // Get id and name of the user
        new LoadUserData().execute();
    }

    /**
     * Triggered when the search button is pressed in the people fragment
     *
     * @param persons list of people that match the query
     */
    public void onPeopleSearchListener(final ArrayList<Person> persons)
    {
        if (persons.size() == 1)
        {
            Fragment fragment = null;
            try
            {
                fragment = PeopleDetailFragment.newInstance(persons.get(0));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

            PeopleListFragment myFragment = (PeopleListFragment) getSupportFragmentManager().findFragmentByTag("people_overview");
            if (myFragment != null && myFragment.isVisible())
            {
                fragmentManager.beginTransaction().addToBackStack("people_overview").replace(R.id.people_content, fragment, "people_details").commit();
            } else
            {
                fragmentManager.beginTransaction().replace(R.id.people_content, fragment, "people_details").commit();
            }


        } else
        {
            Fragment fragment = null;
            try
            {
                fragment = PeopleListFragment.newInstance(persons);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.people_content, fragment, "people_overview").commit();
        }
    }

    /**
     * Triggered when the user clicks an item in the people listview
     *
     * @param p the selected person
     */
    @Override
    public void onFragmentInteraction(Person p)
    {
        ArrayList<Person> person = new ArrayList<>();
        person.add(p);
        onPeopleSearchListener(person);
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu which menu to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item the item that is selected
     * @return true if selection is handled, otherwise call super method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0)
                {
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the user selects an item from the navigation drawer
     *
     * @param item the selected item
     * @return true if action is handled
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Fragment fragment;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_coworkers)
        {
            fragmentClass = PeopleFragment.class;
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

    /**
     * Triggered when the user selects an item in the news fragment.
     * Create an intent with detailed information about the selected item.
     *
     * @param item NewsItem instance that contains information about the selected item.
     */
    public void onAdapterInteractionListener(NewsItem item)
    {
        // Switch fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_content_frame, NewsDetailsFragment.newInstance(item))
                .addToBackStack("parent")
                .commit();

        // Change the icon to the up arrow
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // On UP click
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go to previous fragment
                MainActivity.super.onBackPressed();

                // Change the icon to the drawer icon
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                // Get the default configuration
                configureToolbar();
            }
        });
    }

    private void configureToolbar()
    {
        // Assign drawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);

        // Configure actionBar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Get user info from fontys api
     */
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
            } catch (Exception ex)
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
