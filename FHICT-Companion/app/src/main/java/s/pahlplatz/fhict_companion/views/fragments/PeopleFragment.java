package s.pahlplatz.fhict_companion.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.controllers.PeopleController;
import s.pahlplatz.fhict_companion.models.Person;
import s.pahlplatz.fhict_companion.utils.KeyboardManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment implements PeopleController.PeopleListener {
    private static final String PERSISTENT_VARIABLE_ADAPTER = "persistentAdapter";

    private PeopleController controller;
    private ProgressBar progressBar;
    private ListView listView;

    public PeopleFragment() {
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        // Set toolbar title.
        getActivity().setTitle("People");

        // Assign UI elements.
        progressBar = (ProgressBar) view.findViewById(R.id.people_pbar);
        listView = (ListView) view.findViewById(R.id.people_listview);
        final Button btnSearch = (Button) view.findViewById(R.id.people_button_search);
        final EditText etQuery = (EditText) view.findViewById(R.id.people_edittext_search);

        // Configure search editText.
        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    search(etQuery.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Listview item clicks.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                controller.onItemSelected(i);
            }
        });

        // Configure search button.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                search(etQuery.getText().toString());
            }
        });

        // Create the controller.
        controller = new PeopleController(getContext(), this);

        // Restore the listview if we searched for something before.
        Bundle mySavedInstance = getArguments();
        if (mySavedInstance != null) {
            ArrayList<Person> persons = mySavedInstance.getParcelableArrayList(PERSISTENT_VARIABLE_ADAPTER);
            if (persons != null) {
                controller.createList(persons);
            }
            mySavedInstance.clear();
            getArguments().clear();
        }

        return view;
    }

    /**
     * Save the listview state so we can restore it when we go back to this fragment.
     */
    @Override
    public void onPause() {
        super.onPause();

        ArrayList<Person> persons = controller.getPersonList();
        if (persons != null) {
            getArguments().putParcelableArrayList(PERSISTENT_VARIABLE_ADAPTER, persons);
        }
    }

    @Override
    public void setAdapter(final ArrayAdapter adapter) {
        if (adapter != null) {
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void progressbarVisibility(final boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void search(final String query) {
        controller.search(query);
        KeyboardManager.hide(getActivity());
    }
}
