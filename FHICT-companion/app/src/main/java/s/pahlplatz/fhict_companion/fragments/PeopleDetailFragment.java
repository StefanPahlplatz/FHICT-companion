package s.pahlplatz.fhict_companion.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.Person;

/**
 * We don't talk about this one mkay?
 */

public class PeopleDetailFragment extends Fragment
{
    private Person person;

    public static PeopleDetailFragment newInstance(String name, String mail, String office, String phone, String dep, String title, String id)
    {
        PeopleDetailFragment fragment = new PeopleDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("mail", mail);
        args.putString("office", office);
        args.putString("phone", phone);
        args.putString("dep", dep);
        args.putString("title", title);
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            person = new Person(getArguments().getString("name"),
                    getArguments().getString("mail"),
                    getArguments().getString("office"),
                    getArguments().getString("phone"),
                    getArguments().getString("dep"),
                    getArguments().getString("title"),
                    getArguments().getString("id"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_people_info, container, false);

        TextView name, mail, office, phone, dep, title, id;
        name = (TextView) view.findViewById(R.id.people_info_name);
        mail = (TextView) view.findViewById(R.id.people_info_mail);
        office = (TextView) view.findViewById(R.id.people_info_office);
        phone = (TextView) view.findViewById(R.id.people_info_phone);
        dep = (TextView) view.findViewById(R.id.people_info_dep);
        title = (TextView) view.findViewById(R.id.people_info_title);
        id = (TextView) view.findViewById(R.id.people_info_id);

        name.setText(person.getName());
        mail.setText(person.getMail());
        office.setText(person.getOffice());
        phone.setText(person.getPhone());
        dep.setText(person.getDep());
        title.setText(person.getTitle());
        id.setText(person.getId());

        return view;
    }

}
