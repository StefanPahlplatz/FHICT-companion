package s.pahlplatz.fhict_companion.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import s.pahlplatz.fhict_companion.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JsonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JsonFragment extends Fragment
{
    private static final String TAG = JsonFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JsonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JsonFragment newInstance(String param1, String param2)
    {
        JsonFragment fragment = new JsonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_json, container, false);

        Button btn = (Button) view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                new loadJSONFromWeb().execute();

                /*
                try
                {
                    JSONObject reader = new JSONObject(loadJSONFromAsset());
                    Log.i(TAG, "onClick: " + reader.getString("latestUpdate"));
                } catch (Exception ex)
                {
                    Log.e(TAG, "onClick: ", ex);
                }*/
            }
        });

        return view;
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("json.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public class loadJSONFromWeb extends AsyncTask<Void, Void, String>
    {
        @Override
        public String doInBackground(Void... params)
        {
            try
            {
                URL url = new URL("https://api.fhict.nl/grades/me");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + getContext()
                        .getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .getString("token", ""));
                Log.i(TAG, "doInBackground: " + getContext()
                        .getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .getString("token", ""));
                connection.connect();

                InputStream is = connection.getInputStream();
                Log.i(TAG, "doInBackground: " + convertStreamToString(is));
                InputStreamReader isr = new InputStreamReader(is);

                JsonReader reader = new JsonReader(isr);

                reader.beginArray();
                reader.beginObject();

                //TODO: JSON hype

                /*
                while(jsonReader.hasNext())
                {

                    {
                        Log.i(TAG, "doInBackground: " + jsonReader.nextName());
                    }
                }*/

            } catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: ", ex);
            }

            return "done";
        }

        @Override
        public void onPostExecute(String result)
        {
            Log.i(TAG, "onPostExecute: " + result);
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

}
