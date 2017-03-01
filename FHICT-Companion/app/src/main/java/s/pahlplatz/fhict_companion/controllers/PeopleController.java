package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.models.Person;
import s.pahlplatz.fhict_companion.utils.FontysAPI;

/**
 * Created by Stefan on 25-2-2017.
 * <p>
 * Controller for the people fragment.
 */

public class PeopleController {
    private static final String TAG = PeopleController.class.getSimpleName();

    private Context ctx;

    // Reference to the view hosting the search.
    private ProgressbarListener progressbarListener;

    // Reference to the activity hosting the search.
    private OnPeopleSearchListener searchListener;

    /**
     * @param ctx                 context that implements OnPeopleSearchListener.
     * @param progressbarListener calling view that implements ProgressbarListener.
     */
    public PeopleController(final Context ctx, final ProgressbarListener progressbarListener) {
        this.ctx = ctx;
        this.progressbarListener = progressbarListener;

        // Create searchListener
        if (ctx instanceof OnPeopleSearchListener) {
            searchListener = (OnPeopleSearchListener) ctx;
        } else {
            throw new RuntimeException(ctx.toString() + " must implement OnPeopleSearchListener");
        }
    }

    /**
     * Starts a search for the specified query.
     *
     * @param query search parameter.
     */
    public void search(final String query) {
        if (!query.equals("")) {
            new LoadResults().execute(query);

        } else {
            Toast.makeText(ctx, "Search can't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Interface implemented by the host to control the visibility of the progressbar.
     */
    public interface ProgressbarListener {
        void progressbarVisibility(boolean visible);
    }

    /**
     * Interface implemented by the hosting activity to switch fragments when a search gets results.
     */
    public interface OnPeopleSearchListener {
        void onPeopleSearchListener(ArrayList<Person> persons);
    }

    /**
     * Load the results from the fontys API.
     */
    private class LoadResults extends AsyncTask<String, Void, JSONArray> {
        private SharedPreferences sp;

        @Override
        protected void onPreExecute() {
            sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
            progressbarListener.progressbarVisibility(true);
        }

        @Override
        protected JSONArray doInBackground(final String... params) {
            JSONArray jArray = null;
            String query = params[0];

            if (sp != null) {
                try {
                    jArray = new JSONArray(FontysAPI.getStream(
                            "https://api.fhict.nl/people/search/" + query,
                            sp.getString("token", "")
                    ));
                } catch (Exception ex) {
                    Log.e(TAG, "doInBackground: Couldn't get data", ex);
                }
            }
            return jArray;
        }

        @Override
        protected void onPostExecute(final JSONArray jArray) {
            if (jArray != null) {
                if (jArray.length() == 0) {
                    Toast.makeText(ctx, "No results found", Toast.LENGTH_SHORT).show();
                    progressbarListener.progressbarVisibility(false);
                    return;
                }

                ArrayList<Person> persons = new ArrayList<>();
                try {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject p = jArray.getJSONObject(i);
                        persons.add(new Person(
                                p.getString("displayName"),
                                p.getString("mail"),
                                p.getString("office"),
                                p.getString("telephoneNumber"),
                                p.getString("department"),
                                p.getString("title"),
                                p.getString("id")));
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "onPostExecute: Error parsing JSON");
                    Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                }
                searchListener.onPeopleSearchListener(persons);
            }
            progressbarListener.progressbarVisibility(false);
        }
    }
}
