package s.pahlplatz.fhict_companion.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Stefan on 30-11-2016.
 *
 * Static class to connect to the fontys api and retrieve the JSON files.
 */

public class FhictAPI
{
    private static final String TAG = FhictAPI.class.getSimpleName();

    public static InputStream getStream(String link, String token)
    {
        InputStream inputStream;
        try
        {
            URL url = new URL(link);

            // Create Http connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.connect();

            // Get the result
            inputStream = connection.getInputStream();
        } catch (Exception ex)
        {
            Log.e(TAG, "getStream: Couldn't get data from fontys api.", ex);
            return null;
        }
        return inputStream;
    }

    public static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
