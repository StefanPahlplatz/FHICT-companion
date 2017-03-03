package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.models.Person;
import s.pahlplatz.fhict_companion.models.PersonInfo;
import s.pahlplatz.fhict_companion.utils.FontysAPI;

/**
 * Created by Stefan on 25-2-2017.
 * <p>
 * Controller for the people fragment.
 */

public class PeopleController {
    private static final String TAG = PeopleController.class.getSimpleName();

    private Context ctx;
    private ArrayList<Person> personList;

    private PeopleListener listener;                            // Reference to the view hosting the search.
    private OnFragmentInteractionListener activityListener;     // Reference to the activity hosting the search.

    /**
     * Constructor.
     *
     * @param ctx    context that implements OnPeopleSearchListener.
     * @param caller calling view that implements ProgressbarListener.
     */
    public PeopleController(final Context ctx, final PeopleListener caller) {
        this.ctx = ctx;
        this.listener = caller;

        // Assign the listener.
        if (ctx instanceof OnFragmentInteractionListener) {
            activityListener = (OnFragmentInteractionListener) ctx;
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

    private ArrayAdapter getAdapter(final ArrayList<Person> persons) {
        @SuppressWarnings("unchecked")
        ArrayAdapter adapter =
                new ArrayAdapter(ctx, android.R.layout.simple_list_item_2, android.R.id.text1, persons) {
                    @NonNull
                    @Override
                    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        text1.setText(persons.get(position).getName());
                        text2.setText(persons.get(position).getTitle());
                        return view;
                    }
                };
        return adapter;
    }

    /**
     * Creates and sets the adapter to show the persons from the search results.
     *
     * @param persons list of retrieved persons.
     */
    public void createList(final ArrayList<Person> persons) {
        personList = persons;
        listener.setAdapter(getAdapter(persons));
    }

    /**
     * Getter for personList.
     */
    public ArrayList<Person> getPersonList() {
        return personList;
    }

    /**
     * Switch fragments to display the information of the selected person.
     *
     * @param i index in the listview/list.
     */
    public void onItemSelected(final int i) {
        activityListener.onFragmentInteraction(personList.get(i));
    }

    /**
     * Interface implemented by the host to control the visibility of the progressbar.
     */
    public interface PeopleListener {
        /**
         * Hides and un-hides the progressbar.
         *
         * @param visible or not.
         */
        void progressbarVisibility(boolean visible);

        /**
         * Assign the adapter to the listview.
         *
         * @param adapter to be assigned.
         */
        void setAdapter(ArrayAdapter adapter);
    }

    /**
     * Interface implemented by the hosting activity to switch to the details fragment.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Person person);
    }

    /**
     * Load the results from the fontys API.
     */
    private class LoadResults extends AsyncTask<String, Void, JSONArray> {
        private SharedPreferences sp;

        @Override
        protected void onPreExecute() {
            sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
            listener.progressbarVisibility(true);
        }

        @Override
        protected JSONArray doInBackground(final String... params) {
            JSONArray jArrayPeople = null;
            String query = params[0];

            if (sp != null) {
                try {
                    jArrayPeople = new JSONArray(FontysAPI.getStream(
                            "https://api.fhict.nl/people/search/" + query, sp.getString("token", "")));
                } catch (Exception ex) {
                    Log.e(TAG, "doInBackground: Couldn't get data", ex);
                }
            }
            return jArrayPeople;
        }

        @Override
        protected void onPostExecute(final JSONArray jArray) {
            if (jArray != null) {
                // No results.
                if (jArray.length() == 0) {
                    Toast.makeText(ctx, "No results found", Toast.LENGTH_SHORT).show();

                // One result.
                } else if (jArray.length() == 1) {
                    try {
                        activityListener.onFragmentInteraction(new Person(jArray.getJSONObject(0)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                // More results.
                } else {
                    personList = new ArrayList<>();
                    try {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject p = jArray.getJSONObject(i);
                            personList.add(new Person(p));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    createList(personList);
                }

                // Hide the pbar.
                listener.progressbarVisibility(false);
            }
        }
    }
}
