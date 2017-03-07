package s.pahlplatz.fhict_companion.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.controllers.PeopleController;
import s.pahlplatz.fhict_companion.models.NewsItem;
import s.pahlplatz.fhict_companion.models.Person;
import s.pahlplatz.fhict_companion.utils.FontysAPI;
import s.pahlplatz.fhict_companion.utils.NetworkState;
import s.pahlplatz.fhict_companion.utils.PreferenceHelper;
import s.pahlplatz.fhict_companion.views.fragments.GradeFragment;
import s.pahlplatz.fhict_companion.views.fragments.NewsDetailsFragment;
import s.pahlplatz.fhict_companion.views.fragments.NewsFragment;
import s.pahlplatz.fhict_companion.views.fragments.PeopleDetailsFragment;
import s.pahlplatz.fhict_companion.views.fragments.PeopleFragment;
import s.pahlplatz.fhict_companion.views.fragments.ScheduleFragment;

/**
 * MainActivity that hosts the normal app fragments.
 */
public final class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PeopleController.OnFragmentInteractionListener,
        NewsAdapter.OnAdapterInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        boolean started_online = PreferenceHelper.getBoolean(getBaseContext(), PreferenceHelper.STARTED_ONLINE);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("always_offline", false)) {
            NetworkState.setOnline(false);
        } else {
            NetworkState.setOnline(started_online);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
        setSupportActionBar(toolbar);

        configureToolbar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_schedule).setChecked(true);

        if (NetworkState.isOnline()) {
            // Load user data.
            new LoadUserData().execute();
            new LoadProfilePicture().execute();

        } else {
            // Hide all menu except the schedule.
            Menu navMenu = navigationView.getMenu();
            navMenu.findItem(R.id.nav_coworkers).setVisible(false);
            navMenu.findItem(R.id.nav_news).setVisible(false);
            navMenu.findItem(R.id.nav_grades).setVisible(false);
        }

        // Show the default fragment.
        replaceFragment(ScheduleFragment.class);
    }

    @Override
    public void onBackPressed() {
        // Change the icon to the drawer icon
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }
        configureToolbar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Hide 'go online' or 'go offline' depending on the device.
        menu.findItem(R.id.action_go_online).setVisible(!NetworkState.isOnline());
        menu.findItem(R.id.action_go_offline).setVisible(NetworkState.isOnline());

        // Hide 'today' option if fragment is not the schedule.
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.app_bar_main_content_frame);
        menu.findItem(R.id.action_schedule_today).setVisible(f instanceof ScheduleFragment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_go_online:
                if (NetworkState.isActive(getBaseContext())) {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putBoolean("always_offline", false)
                            .apply();
                    PreferenceHelper.save(getBaseContext(), PreferenceHelper.STARTED_ONLINE, true);
                    restart();
                } else {
                    Toast.makeText(this, "Can't connect to the internet.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_go_offline:
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("always_offline", true)
                        .apply();
                restart();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Triggered when the user clicks an item in the people listview.
     *
     * @param p the selected person.
     */
    @Override
    public void onFragmentInteraction(final Person p) {
        // Try to create the detail fragment.
        Fragment fragment = null;
        try {
            fragment = PeopleDetailsFragment.newInstance(p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction()
                .addToBackStack("people_overview")
                .replace(R.id.app_bar_main_content_frame, fragment, "people_details")
                .commit();
        showUpButton();
    }

    /**
     * Triggered when a news item is clicked in the news fragment.
     *
     * @param newsItem selected item.
     */
    @Override
    public void onAdapterInteractionListener(final NewsItem newsItem) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.app_bar_main_content_frame, NewsDetailsFragment.newInstance(newsItem))
                .addToBackStack("parent")
                .commit();
        showUpButton();
    }

    /**
     * Shows the up button in the toolbar.
     */
    private void showUpButton() {
        // Change the icon to the up arrow
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // On UP click
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Go to previous fragment
                MainActivity.super.onBackPressed();

                // Change the icon to the drawer icon
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                // Get the default configuration
                configureToolbar();
            }
        });
    }

    /**
     * Sets up the drawer layout, actionbar and toolbar.
     */
    private void configureToolbar() {
        // Assign drawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);

        // Configure actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_main_toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Restarts the current activity.
     */
    private void restart() {
        Intent intent = new Intent(this, LaunchActivity.class);
        this.finish();
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_coworkers) {
            fragmentClass = PeopleFragment.class;
        } else if (id == R.id.nav_schedule) {
            fragmentClass = ScheduleFragment.class;
        } else if (id == R.id.nav_news) {
            fragmentClass = NewsFragment.class;
        } else if (id == R.id.nav_grades) {
            fragmentClass = GradeFragment.class;
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        replaceFragment(fragmentClass);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Replaces the current fragment.
     *
     * @param fragmentClass fragment class to replace the current.
     */
    private void replaceFragment(final Class fragmentClass) {
        try {
            if (fragmentClass != null) {
                Fragment fragment = (Fragment) fragmentClass.newInstance();

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.app_bar_main_content_frame, fragment).commit();
            }
        } catch (Exception ex) {
            Log.e(TAG, "onNavigationItemSelected: Couldn't switch fragments", ex);
        }
    }

    /**
     * Class to load the user's data. E.g. student id, name, title, etc.
     */
    private class LoadUserData extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(final Void... params) {
            Map<String, String> retMap = new HashMap<>();
            String token = PreferenceHelper.getString(getBaseContext(), PreferenceHelper.TOKEN);
            if (token.equals("")) {
                return null;
            }

            try {
                // Get the user's information.
                JSONObject jObject = new JSONObject(FontysAPI.getStream("https://api.fhict.nl/people/me", token));

                // Get the user's class.
                JSONArray jsonArray = new JSONArray(FontysAPI.getStream("https://api.fhict.nl/groups", token));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject group = jsonArray.getJSONObject(i);
                    try {
                        // If the user has a class store it in sp.
                        String groupType = group.getString("groupType");
                        if (groupType.contains("Klas")) {
                            PreferenceHelper.save(getBaseContext(), PreferenceHelper.CLASS_ID, group.getString("id"));
                            PreferenceHelper.save(getBaseContext(), PreferenceHelper.CLASS_NAME, group.getString("groupName"));
                            Log.i(TAG, "doInBackground: User is part of class " + group.getString("id"));
                        }
                    } catch (Exception ex) {
                        // Do nothing.
                    }
                }

                // Store info
                PreferenceHelper.save(getBaseContext(), PreferenceHelper.USER_ID, jObject.getString("id"));
                PreferenceHelper.save(getBaseContext(), PreferenceHelper.DISPLAY_NAME, jObject.getString("displayName"));
                PreferenceHelper.save(getBaseContext(), PreferenceHelper.USER_TITLE, jObject.getString("title"));
                PreferenceHelper.save(getBaseContext(), PreferenceHelper.PROFILE_PICTURE_URL, jObject.getString("photo"));

                // Return name and title
                retMap.put("displayName", jObject.getString("displayName"));
                retMap.put("title", jObject.getString("title"));
            } catch (Exception ex) {
                Log.e(TAG, "doInBackground: A problem occurred while parsing the JSON file.", ex);
            }
            return retMap;
        }

        @Override
        protected void onPostExecute(final Map<String, String> map) {
            if (map == null) {
                return;
            }

            TextView name = (TextView) findViewById(R.id.nav_header_name);
            name.setText(map.get("displayName"));

            TextView title = (TextView) findViewById(R.id.nav_header_title);
            title.setText(map.get("title"));
        }
    }

    /**
     * Class to load the user's picture for in the nav drawer.
     */
    private class LoadProfilePicture extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(final Void... params) {
            String token = PreferenceHelper.getString(getBaseContext(), PreferenceHelper.TOKEN);
            if (token.equals("")) {
                return null;
            }

            return FontysAPI.getPicture(
                    "https://api.fhict.nl/pictures/I" + PreferenceHelper.getString(getBaseContext(),
                            PreferenceHelper.USER_ID).substring(1) + ".jpg", token);
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            if (result == null) {
                return;
            }

            CircleImageView image = (CircleImageView) findViewById(R.id.nav_header_profile_image);
            image.setImageBitmap(result);
        }
    }
}
