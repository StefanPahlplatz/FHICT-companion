package s.pahlplatz.fhict_companion.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.models.Person;

/**
 * Created by Stefan on 25-2-2017.
 * <p>
 * Controller for the people list fragment.
 */

public class PeopleListController {
    private Context ctx;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Person> persons;

    /**
     * @param ctx     context that implements OnFragmentInteractionListener.
     * @param persons list of persons to display in the listview.
     */
    public PeopleListController(Context ctx, ArrayList<Person> persons) {
        this.ctx = ctx;
        this.persons = persons;

        if (ctx instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) ctx;
        } else {
            throw new RuntimeException(ctx.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Returns the adapter needed for the listview
     *
     * @return array adapter.
     */
    public ArrayAdapter getAdapter() {
        @SuppressWarnings("unchecked") ArrayAdapter adapter =
                new ArrayAdapter(ctx, android.R.layout.simple_list_item_2, android.R.id.text1, persons) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        text1.setText(persons.get(position).getName());
                        text2.setText(persons.get(position).getTitle());
                        return view;
                    }
                };
        return adapter;
    }

    /**
     * Switch fragments to display the information of the selected person.
     *
     * @param i index in the listview/list.
     */
    public void onItemSelected(int i) {
        mListener.onFragmentInteraction(persons.get(i));
    }

    /**
     * Interface implemented by the hosting activity to switch to the details fragment.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Person person);
    }
}
