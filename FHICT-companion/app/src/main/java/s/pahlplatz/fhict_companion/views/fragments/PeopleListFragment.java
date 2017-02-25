package s.pahlplatz.fhict_companion.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.controllers.PeopleListController;
import s.pahlplatz.fhict_companion.models.Person;

public class PeopleListFragment extends Fragment {
    private PeopleListController controller;

    /**
     * Use this to create a new instance of the fragment.
     *
     * @param persons list of persons to display in the listview.
     * @return PeopleListFragment.
     */
    public static PeopleListFragment newInstance(ArrayList<Person> persons) {
        PeopleListFragment fragment = new PeopleListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("persons", persons);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ArrayList<Person> persons = getArguments().getParcelableArrayList("persons");
            controller = new PeopleListController(getContext(), persons);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView lv = new ListView(getActivity());
        lv.setAdapter(controller.getAdapter());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                controller.onItemSelected(i);
            }
        });
        return lv;
    }
}