package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import s.pahlplatz.fhict_companion.adapters.GradeAdapter;
import s.pahlplatz.fhict_companion.models.Grade;
import s.pahlplatz.fhict_companion.utils.FontysAPI;

/**
 * Created by Stefan on 25-2-2017.
 *
 * Controller for grade fragment.
 */
public class GradeController {
    private static final String TAG = GradeController.class.getSimpleName();

    private GradeControllerListener controllerListener;
    private Context ctx;
    private ArrayList<Grade> grades;

    public GradeController(Context ctx, GradeControllerListener listener) {
        this.ctx = ctx;
        this.grades = new ArrayList<>();
        this.controllerListener = listener;
    }

    /**
     * Refresh grades.
     */
    public void refresh() {
        new LoadGrades().execute();
    }

    /**
     * Sort grades ascending.
     */
    public void sortGradesAsc() {
        Collections.sort(grades, new Comparator<Grade>() {
            @Override
            public int compare(Grade grade, Grade t1) {
                return grade.sortByGradeAsc(t1);
            }
        });
        controllerListener.onAdapterChanged(new GradeAdapter(ctx, grades));
    }

    /**
     * Sort grades descending.
     */
    public void sortGradesDesc() {
        Collections.sort(grades, new Comparator<Grade>() {
            @Override
            public int compare(Grade grade, Grade t1) {
                return grade.sortByGradeDesc(t1);
            }
        });
        controllerListener.onAdapterChanged(new GradeAdapter(ctx, grades));
    }

    /**
     * Sort grades alphabetically.
     */
    public void sortGradesAlp() {
        Collections.sort(grades, new Comparator<Grade>() {
            @Override
            public int compare(Grade grade, Grade t1) {
                return grade.sortByNameDesc(t1);
            }
        });
        controllerListener.onAdapterChanged(new GradeAdapter(ctx, grades));
    }

    /**
     * Interface implemented by the host.
     */
    public interface GradeControllerListener {
        void onProgressbarVisibility(boolean visible);

        void onAdapterChanged(GradeAdapter adapter);
    }

    /**
     * Class to load grades from the fontys API.
     */
    private class LoadGrades extends AsyncTask<Void, Void, Void> {
        @Override
        public void onPreExecute() {
            // Clear grade list
            grades.clear();
        }

        @Override
        public Void doInBackground(Void... params) {
            try {
                JSONArray jArray = new JSONArray(FontysAPI.getStream(
                        "https://api.fhict.nl/grades/me",
                        ctx.getSharedPreferences(
                                "settings", Context.MODE_PRIVATE).getString("token", "")));

                for (int i = 0; i < jArray.length(); i++) {
                    grades.add(new Grade(jArray.getJSONObject(i).getString("item"), jArray.getJSONObject(i).getDouble("grade")));
                }
            } catch (Exception ex) {
                Log.e(TAG, "doInBackground: A problem occurred while parsing the JSON file.", ex);
            }

            return null;
        }

        @Override
        public void onPostExecute(Void params) {
            GradeAdapter adapter = new GradeAdapter(ctx, grades);
            controllerListener.onAdapterChanged(adapter);
            controllerListener.onProgressbarVisibility(false);
        }
    }
}