package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.FhictAPI;
import s.pahlplatz.fhict_companion.utils.models.Person;

public class PeopleFragment extends Fragment
{
    private static final String TAG = NewsFragment.class.getSimpleName();

    private OnPeopleSearchListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        final EditText et_query = (EditText) view.findViewById(R.id.people_edittext_search);

        Button btnSearch = (Button) view.findViewById(R.id.people_button_search);
        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new LoadResults().execute(et_query.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnPeopleSearchListener)
        {
            mListener = (OnPeopleSearchListener) context;
        } else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnPeopleSearchListener
    {
        void onPeopleSearchListener(ArrayList<Person> persons);
    }

    private class LoadResults extends AsyncTask<String, Void, JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params)
        {
            JSONArray jArray = null;
            String query = params[0];
            SharedPreferences sp = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
            if (sp != null)
            {
                try
                {
                    jArray = new JSONArray(FhictAPI.getStream(
                            "https://api.fhict.nl/people/search/" + query,
                            sp.getString("token", "")
                    ));
                } catch (Exception ex)
                {
                    Log.e(TAG, "doInBackground: Couldn't get data", ex);
                }
            }

            return jArray;
        }

        @Override
        protected void onPostExecute(JSONArray jArray)
        {
            if (jArray != null)
            {
                if (jArray.length() == 0)
                {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Person> persons = new ArrayList<>();
                try
                {
                    for (int i = 0; i < jArray.length(); i++)
                    {
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

                    if (jArray.length() == 1)
                    {
                        mListener.onPeopleSearchListener(persons);
                    } else
                    {
                        mListener.onPeopleSearchListener(persons);
                    }
                } catch (Exception ex)
                {
                    Log.e(TAG, "onPostExecute: Error parsing JSON");
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
