package s.pahlplatz.fhict_companion.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Person class to store data about a person.
 */
public class Person implements Parcelable {
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        public Person createFromParcel(final Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(final int size) {

            return new Person[size];
        }
    };
    private static final String TAG = Person.class.getSimpleName();
    private String name;
    private String title;
    private String pictureUrl;
    private String id;
    private ArrayList<PersonInfo> info;
    private boolean hasExtra;

    /**
     * Special constuctor for students.
     * @param p json object with info.
     * @param className the name of the group the student is in.
     */
    public Person(final JSONObject p, final String className) {
        this(p);
        info.add(PersonInfo.createInfo("Group", className));
    }

    /**
     * Default constructor.
     */
    public Person(final JSONObject p) {
        info = new ArrayList<>();

        try {
            name = p.getString("displayName");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        // Because there are 2 ways the picture url can be stored, try them both.
        try {
            pictureUrl = p.getString("photo");
        } catch (JSONException e) {
            //// Do nothing.
        }
        try {
            String pic = p.getString("photoUri");
            if (!pic.equals("")) {
                pictureUrl = pic;
            }
        } catch (JSONException e) {
            // Do nothing.
        }
        try {
            title = p.getString("title");
        } catch (JSONException e) {
            title = "Student";
        }
        try {
            id = p.getString("id");
        } catch (JSONException e) {
            // Do nothing.
        }

        info.add(PersonInfo.createInfo("ID", getId()));
        try {
            info.add(PersonInfo.createInfo("Mail", p.getString("mail")));
        } catch (JSONException e) {
            // Do nothing.
        }
        try {
            info.add(PersonInfo.createInfo("Office", p.getString("office")));
        } catch (JSONException e) {
            // Do nothing.
        }
        try {
            info.add(PersonInfo.createInfo("Phone", p.getString("telephoneNumber")));
        } catch (JSONException e) {
            // Do nothing.
        }
        try {
            info.add(PersonInfo.createInfo("Department", p.getString("department")));
        } catch (JSONException e) {
            // Do nothing.
        }
        if (getTitle() != null) {
            info.add(PersonInfo.createInfo("Title", getTitle()));
        }
        try {
            info.add(PersonInfo.createInfo("Personal title", p.getString("personalTitle")));
        } catch (JSONException e) {
            // Do nothing.
        }
        try {
            info.add(PersonInfo.createInfo("Affiliations", p.getString("affiliations")));
        } catch (JSONException e) {
            // Do nothing.
        }
        try {
            info.add(PersonInfo.createInfo("Employee ID", p.getString("employeeId")));
        } catch (JSONException e) {
            // Do nothing.
        }
    }

    /**
     * Constructor used for parcels.
     *
     * @param in Parcel.
     */
    private Person(final Parcel in) {
        super();
        readFromParcel(in);
    }

    /**
     * Add extra information to the list.
     *
     * @param extra ArrayList containing personInfo.
     */
    public void addExtraInfo(final ArrayList<PersonInfo> extra) {
        info.addAll(extra);
        hasExtra = true;
    }

    /**
     * Checks all elements to see if any of them match the query.
     * @param query search.
     * @return true if person contains the information.
     */
    public boolean contains(String query) {
        query = query.toLowerCase();
        for (PersonInfo personInfo : info) {
            if (personInfo.getRight().toLowerCase().contains(query)) {
                return true;
            }
        }
        return name.toLowerCase().contains(query)
                || (title != null && title.toLowerCase().contains(query))
                || pictureUrl.toLowerCase().contains(query)
                || id.toLowerCase().contains(query);
    }

    public boolean hasExtra() {
        return hasExtra;
    }

    public ArrayList<PersonInfo> getInfo() {
        return info;
    }

    public PersonInfo getInfo(final int position) {
        return info.get(position);
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    private void readFromParcel(final Parcel in) {
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
    }
}
