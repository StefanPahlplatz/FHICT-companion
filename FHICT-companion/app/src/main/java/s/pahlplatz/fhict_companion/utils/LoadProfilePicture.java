package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;
import s.pahlplatz.fhict_companion.R;

/**
 * Created by Stefan on 2-12-2016.
 * <p>
 * Class to load the profile picture in the navigation header
 * <p>
 * Params:
 * 0 = Context
 * 1 = View
 */

public class LoadProfilePicture extends AsyncTask<Object, Void, Bitmap>
{
    private View view;

    @Override
    protected Bitmap doInBackground(Object... params)
    {
        Context ctx = (Context) params[0];
        view = (View) params[1];
        SharedPreferences sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return FhictAPI.getPicture("https://api.fhict.nl/pictures/I" + sp.getString("id", "").substring(1) + ".jpg", sp.getString("token", ""));
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        CircleImageView image = (CircleImageView) view.findViewById(R.id.header_profile_image);
        image.setImageBitmap(result);
    }
}
