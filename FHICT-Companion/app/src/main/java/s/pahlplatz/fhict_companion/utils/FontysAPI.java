package s.pahlplatz.fhict_companion.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Stefan on 30-11-2016.
 * <p>
 * Static class to connect to the fontys api and retrieve the JSON files.
 */
public final class FontysAPI {
    private static final String TAG = FontysAPI.class.getSimpleName();

    private FontysAPI() {
        // Not called.
    }

    /**
     * Get the JSON stream from the fontys API.
     *
     * @param link  to request the data.
     * @param token the users unique token.
     * @return JSON stream.
     */
    public static String getStream(final String link, final String token) {
        InputStream inputStream;
        try {
            URL url = new URL(link);

            // Create Http connection.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.connect();

            // Get the result.
            inputStream = connection.getInputStream();
        } catch (Exception ex) {
            Log.e(TAG, "getStream: Couldn't get data from fontys api." + ex.getMessage());
            return null;
        }
        return convertStreamToString(inputStream);
    }

    /**
     * Converts the stream to a string.
     *
     * @param is InputStream to convert.
     */
    private static String convertStreamToString(final InputStream is) {
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

    /**
     * Gets the picture from the fontys API.
     *
     * @param link  of the picture.
     * @param token the users unique token.
     * @return a bitmap of the image.
     */
    public static Bitmap getPicture(final String link, final String token) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "image/jpeg");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (IOException ex) {
            Log.e(TAG, "doInBackground: Couldn't get picture from url", ex);
            return null;
        }
    }
}
