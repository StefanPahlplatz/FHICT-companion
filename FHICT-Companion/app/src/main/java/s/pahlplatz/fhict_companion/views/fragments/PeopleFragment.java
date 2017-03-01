package s.pahlplatz.fhict_companion.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.controllers.PeopleController;
import s.pahlplatz.fhict_companion.utils.KeyboardManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment implements PeopleController.ProgressbarListener {

    private PeopleController controller;
    private ProgressBar progressBar;
    private EditText etQuery;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        // Set toolbar title.
        getActivity().setTitle("People");

        controller = new PeopleController(getContext(), this);

        // Assign UI elements.
        progressBar = (ProgressBar) view.findViewById(R.id.people_pbar);
        final Button btnSearch = (Button) view.findViewById(R.id.people_button_search);
        etQuery = (EditText) view.findViewById(R.id.people_edittext_search);

        // Configure search editText.
        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        // Configure search button.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                search();
            }
        });

        return view;
    }

    @Override
    public void progressbarVisibility(final boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void search() {
        controller.search(etQuery.getText().toString());
        KeyboardManager.hide(getActivity());
    }
}
