package s.pahlplatz.fhict_companion.views.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.PeopleDetailAdapter;
import s.pahlplatz.fhict_companion.controllers.PeopleDetailController;
import s.pahlplatz.fhict_companion.models.Person;

/**
 * Fragment that displays the details about a person.
 */
public class PeopleDetailsFragment extends Fragment implements PeopleDetailController.PeopleDetailListener {
    private static final String TAG = PeopleDetailsFragment.class.getSimpleName();

    private Person person;
    private ListView lv;
    private TextView name;
    private CircleImageView image;
    private ProgressBar extraInfoPbar;

    public static PeopleDetailsFragment newInstance(final Person person) {
        PeopleDetailsFragment fragment = new PeopleDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("person", person);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            person = getArguments().getParcelable("person");
        } else {
            Log.e(TAG, "onCreate: No arguments.");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_details, container, false);
        image = (CircleImageView) view.findViewById(R.id.people_details_profile_image);
        lv = (ListView) view.findViewById(R.id.people_details_listview);
        name = (TextView) view.findViewById(R.id.people_details_name);
        extraInfoPbar = (ProgressBar) view.findViewById(R.id.people_details_extra_info_pbar);

        // Create a controller.
        new PeopleDetailController(person, this);

        return view;
    }

    @Override
    public void setName(final String nameParam) {
        name.setText(nameParam);
    }

    @Override
    public void setProfileImage(final Bitmap b) {
        View tempView = getView();
        if (tempView != null) {
            tempView.findViewById(R.id.people_details_profile_image_pbar).setVisibility(View.GONE);
            image.setImageBitmap(b);
        }
    }

    @Override
    public void setInfoProgressbarVisibility(final boolean visible) {
        extraInfoPbar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setListViewAdapter(final PeopleDetailAdapter adapter) {
        lv.setAdapter(adapter);
    }
}
