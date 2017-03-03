package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.adapters.PeopleDetailAdapter;
import s.pahlplatz.fhict_companion.models.Person;
import s.pahlplatz.fhict_companion.models.PersonInfo;
import s.pahlplatz.fhict_companion.utils.FontysAPI;

/**
 * Created by Stefan on 3-3-2017.
 * <p>
 * Controller for person details fragment.
 */

public class PeopleDetailController {
    private static final String TAG = PeopleDetailController.class.getSimpleName();

    private PeopleDetailListener listener;
    private Person p;
    private Context ctx;

    public PeopleDetailController(final Person p, final PeopleDetailListener caller) {
        this.p = p;
        this.listener = caller;
        ctx = caller.getContext();
        listener.setName(p.getName());
        listener.setListViewAdapter(getAdapter());
        new LoadProfilePicture().execute();

        if (!p.hasExtra()) {
            new LoadExtraResults().execute();
        } else {
            listener.setInfoProgressbarVisibility(false);
        }
    }

    private PeopleDetailAdapter getAdapter() {
        return new PeopleDetailAdapter(p.getInfo(), ctx);
    }

    public PersonInfo getPersonInfo(final int position) {
        return p.getInfo(position);
    }

    /**
     * Callbacks to the caller.
     */
    public interface PeopleDetailListener {
        void setProfileImage(Bitmap image);

        void setName(String name);

        void setListViewAdapter(PeopleDetailAdapter adapter);

        void setInfoProgressbarVisibility(boolean visible);

        Context getContext();
    }

    /**
     * Class to load the person's picture.
     */
    private class LoadProfilePicture extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(final Void... params) {
            SharedPreferences sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
            return FontysAPI.getPicture(p.getPictureUrl(), sp.getString("token", ""));
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            listener.setProfileImage(result);
        }
    }

    /**
     * Load the extra results from the fontys API.
     */
    private class LoadExtraResults extends AsyncTask<Void, Void, JSONArray> {
        private SharedPreferences sp;

        @Override
        protected void onPreExecute() {
            sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }

        @Override
        protected JSONArray doInBackground(final Void... params) {
            JSONArray jArrayInfo = null;

            if (sp != null) {
                try {
                    jArrayInfo = new JSONArray(FontysAPI.getStream(
                            "https://api.fhict.nl/sharepoint/people?query=" + p.getId(), sp.getString("token", "")));
                } catch (Exception ex) {
                    Log.e(TAG, "doInBackground: Couldn't get data", ex);
                }
            } else {
                Log.e(TAG, "doInBackground: SharedPreferences = null");
            }
            return jArrayInfo;
        }

        @Override
        protected void onPostExecute(final JSONArray jArray) {
            if (jArray != null) {
                JSONObject j = null;
                try {
                    j = jArray.getJSONObject(0);
                } catch (JSONException e) {
                    // Do nothing.
                }

                ArrayList<PersonInfo> extraInfo = new ArrayList<>();
                assert j != null;
                try {
                    extraInfo.add(PersonInfo.createInfo("Facebook", j.getString("facebook")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("LinkedIn", j.getString("linkedin")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Website", j.getString("personalWebsite")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Twitter", j.getString("twitter")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Past employers", j.getString("pastEmployers")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Current projects", j.getString("currentProjects")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Ambitions", j.getString("ambitions")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Contributions", j.getString("contributions")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Goals", j.getString("goals")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Interests", j.getString("interests")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Past projects", j.getString("pastProjects")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Responsibilities", j.getString("responsibilities")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("Skills", j.getString("skills")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                try {
                    extraInfo.add(PersonInfo.createInfo("About", j.getString("aboutMe")));
                } catch (JSONException e) {
                    // Do nothing.
                }
                p.addExtraInfo(extraInfo);
                listener.setListViewAdapter(getAdapter());
                listener.setInfoProgressbarVisibility(false);
            } else {
                Log.e(TAG, "onPostExecute: jARRAY = NULL");
            }
        }
    }
}
