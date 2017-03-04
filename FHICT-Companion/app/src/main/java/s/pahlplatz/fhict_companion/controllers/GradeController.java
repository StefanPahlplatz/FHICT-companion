package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import s.pahlplatz.fhict_companion.adapters.GradeAdapter;
import s.pahlplatz.fhict_companion.models.Grade;
import s.pahlplatz.fhict_companion.utils.FontysAPI;
import s.pahlplatz.fhict_companion.utils.PreferenceHelper;

/**
 * Created by Stefan on 25-2-2017.
 * <p>
 * Controller for grade fragment.
 */
public class GradeController {
    private static final String TAG = GradeController.class.getSimpleName();
    private static final int NONE = 0;
    private static final int ALPH = 1;
    private static final int ASC = 2;
    private static final int DESC = 3;

    private final GradeControllerListener listener;
    private final Context ctx;
    private final ArrayList<Grade> grades;

    public GradeController(final Context ctx, final GradeControllerListener listener) {
        this.ctx = ctx;
        this.grades = new ArrayList<>();
        this.listener = listener;
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
            public int compare(final Grade grade, final Grade t1) {
                return grade.sortByGradeAsc(t1);
            }
        });
        listener.setAdapter(new GradeAdapter(ctx, grades));
    }

    /**
     * Sort grades descending.
     */
    public void sortGradesDesc() {
        Collections.sort(grades, new Comparator<Grade>() {
            @Override
            public int compare(final Grade grade, final Grade t1) {
                return grade.sortByGradeDesc(t1);
            }
        });
        listener.setAdapter(new GradeAdapter(ctx, grades));
    }

    /**
     * Sort grades alphabetically.
     */
    public void sortGradesAlp() {
        Collections.sort(grades, new Comparator<Grade>() {
            @Override
            public int compare(final Grade grade, final Grade t1) {
                return grade.sortByNameDesc(t1);
            }
        });
        listener.setAdapter(new GradeAdapter(ctx, grades));
    }

    /**
     * Interface implemented by the host.
     */
    public interface GradeControllerListener {
        void setProgressbarVisibility(boolean visible);

        void setAdapter(GradeAdapter adapter);
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
        public Void doInBackground(final Void... params) {
            try {
                JSONArray jArray = new JSONArray(FontysAPI.getStream(
                        "https://api.fhict.nl/grades/me", PreferenceHelper.getString(ctx, PreferenceHelper.TOKEN)));

                for (int i = 0; i < jArray.length(); i++) {
                    Grade toAdd = new Grade(jArray.getJSONObject(i).getString("item"),
                            jArray.getJSONObject(i).getDouble("grade"));

                    // Prevent duplicates.
                    boolean duplicate = false;
                    for (Grade grade : grades) {
                        if (grade.getName().equals(toAdd.getName())) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        grades.add(toAdd);
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "doInBackground: A problem occurred while parsing the JSON file.", ex);
            }

            return null;
        }

        @Override
        public void onPostExecute(final Void params) {
            switch (Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ctx).getString("grade_sort", "0"))) {
                case ALPH:
                    sortGradesAlp();
                    break;

                case ASC:
                    sortGradesAsc();
                    break;

                case DESC:
                    sortGradesDesc();
                    break;

                case NONE:
                default:
                    GradeAdapter a = new GradeAdapter(ctx, grades);
                    listener.setAdapter(a);
                    break;
            }
            listener.setProgressbarVisibility(false);
        }
    }
}
