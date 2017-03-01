package s.pahlplatz.fhict_companion.models;

import android.os.Parcel;
import android.os.Parcelable;

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

    private String name;
    private String mail;
    private String office;
    private String phone;
    private String dep;
    private String title;
    private String id;

    /**
     * Default constructor.
     */
    public Person(final String name, final String mail, final String office, final String phone,
                  final String dep, final String title, final String id) {
        this.name = name;
        this.mail = mail;
        this.office = office;
        this.phone = phone;
        this.dep = dep;
        this.title = title;
        this.id = id;
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
     * Read and store the data.
     *
     * @param in Parcel.
     */
    private void readFromParcel(final Parcel in) {
        name = in.readString();
        mail = in.readString();
        office = in.readString();
        phone = in.readString();
        dep = in.readString();
        title = in.readString();
        id = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeString(office);
        dest.writeString(phone);
        dest.writeString(dep);
        dest.writeString(title);
        dest.writeString(id);
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getOffice() {
        return office;
    }

    public String getPhone() {
        return phone;
    }

    public String getDep() {
        return dep;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}