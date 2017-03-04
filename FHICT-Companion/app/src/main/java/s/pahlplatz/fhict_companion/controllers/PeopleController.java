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
    private class LoadResults extends AsyncTask<String, Void, Void> {
        private JSONArray jArrayPeopleSearch = null;
        private JSONArray jArrayClassMembers = null;
        private String token;
        private String classId;
        private String className;
        private String query;

        @Override
        protected void onPreExecute() {
            SharedPreferences sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
            token = sp.getString("token", "");
            classId = sp.getString("classId", "");
            className = sp.getString("className", "");
            listener.progressbarVisibility(true);
        }

        @Override
        protected Void doInBackground(final String... params) {
            query = params[0];

            // // Get data from the people search.
            try {
                jArrayPeopleSearch = new JSONArray(FontysAPI.getStream(
                        "https://api.fhict.nl/people/search/" + query, token));
            } catch (Exception ex) {
                Log.e(TAG, "doInBackground: Couldn't get data", ex);
            }

            // Get data from the class search.
            if (!classId.equals("")) {
                try {
                    JSONObject jObj = new JSONObject(FontysAPI.getStream(
                            "https://api.fhict.nl/groups/" + classId + "?includeMembers=true", token));
                    jArrayClassMembers = jObj.getJSONArray("members");
                } catch (Exception ex) {
                    Log.e(TAG, "doInBackground: Couldn't get data from class search.", ex);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            personList = new ArrayList<>();

            if (jArrayPeopleSearch != null) {
                // One result.
                if (jArrayPeopleSearch.length() == 1) {
                    try {
                        personList.add(new Person(jArrayPeopleSearch.getJSONObject(0)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                // More results.
                } else {
                    try {
                        for (int i = 0; i < jArrayPeopleSearch.length(); i++) {
                            JSONObject p = jArrayPeopleSearch.getJSONObject(i);
                            personList.add(new Person(p));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e(TAG, "onPostExecute: People search is null");
            }

            if (jArrayClassMembers != null) {
                for (int i = 0; i < jArrayClassMembers.length(); i++) {
                    JSONObject p = null;
                    try {
                        p = jArrayClassMembers.getJSONObject(i);
                    } catch (JSONException e) {
                        // Do nothing.
                    }
                    Person person = new Person(p, className);
                    if (person.contains(query)) {
                        personList.add(person);
                    }
                }
            } else {
                Log.e(TAG, "onPostExecute: jArrayClassMembers = null. "
                        + "This means that the search for people in your class was unsuccessful");
            }

            if (personList.size() == 0) {
                Toast.makeText(ctx, "No results found", Toast.LENGTH_SHORT).show();
            } else if (personList.size() == 1) {
                activityListener.onFragmentInteraction(personList.get(0));
            } else {
                createList(personList);
            }

            // Hide the pbar.
            listener.progressbarVisibility(false);
        }
    }
}
