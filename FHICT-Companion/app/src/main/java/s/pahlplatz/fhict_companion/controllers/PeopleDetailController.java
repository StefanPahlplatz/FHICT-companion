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
    private class LoadExtraResults extends AsyncTask<Void, Void, Void> {
        private JSONObject jObjectInfo = null;
        private String token;

        @Override
        protected void onPreExecute() {
            SharedPreferences sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
            token = sp.getString("token", "");
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Get data from the sharepoint search.
            try {
                jObjectInfo = new JSONArray(FontysAPI.getStream(
                        "https://api.fhict.nl/sharepoint/people?query=" + p.getId(), token)).getJSONObject(0);
            } catch (Exception ex) {
                Log.e(TAG, "doInBackground: Couldn't get data from sharepoint search. " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            ArrayList<PersonInfo> extraInfo = new ArrayList<>();

            // Add data from sharepoint search to the list.
            if (jObjectInfo != null) {
                addSharePointInfo(extraInfo);
            } else {
                Log.e(TAG, "onPostExecute: No extra information found on sharepoint.");
            }

            p.addExtraInfo(extraInfo);
            listener.setListViewAdapter(getAdapter());
            listener.setInfoProgressbarVisibility(false);
        }

        /**
         * Wrapper to extract all data from the json object safely.
         * @param extraInfo list to add to.
         */
        private void addSharePointInfo(final ArrayList<PersonInfo> extraInfo) {
            try {
                extraInfo.add(PersonInfo.createInfo("Facebook", jObjectInfo.getString("facebook")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("LinkedIn", jObjectInfo.getString("linkedin")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Website", jObjectInfo.getString("personalWebsite")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Twitter", jObjectInfo.getString("twitter")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Past employers", jObjectInfo.getString("pastEmployers")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Current projects", jObjectInfo.getString("currentProjects")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Ambitions", jObjectInfo.getString("ambitions")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Contributions", jObjectInfo.getString("contributions")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Goals", jObjectInfo.getString("goals")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Interests", jObjectInfo.getString("interests")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Past projects", jObjectInfo.getString("pastProjects")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Responsibilities", jObjectInfo.getString("responsibilities")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("Skills", jObjectInfo.getString("skills")));
            } catch (JSONException e) {
                // Do nothing.
            }
            try {
                extraInfo.add(PersonInfo.createInfo("About", jObjectInfo.getString("aboutMe")));
            } catch (JSONException e) {
                // Do nothing.
            }
        }
    }
}
