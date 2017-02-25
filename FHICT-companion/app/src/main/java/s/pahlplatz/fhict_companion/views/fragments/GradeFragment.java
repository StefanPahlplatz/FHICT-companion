package s.pahlplatz.fhict_companion.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.GradeAdapter;
import s.pahlplatz.fhict_companion.controllers.GradeController;

/**
 * Created by Stefan on 30-11-2016.
 * <p>
 * Fragment to show the user their grades.
 */
public class GradeFragment extends Fragment implements GradeController.GradeControllerListener {
    private GradeController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);

        getActivity().setTitle("Grades");
        setHasOptionsMenu(true);

        controller = new GradeController(getContext(), this);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.grades_swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controller.refresh();
            }
        });

        controller.refresh();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_grade_refresh:
                controller.refresh();
                break;

            case R.id.action_grade_sort_grade_asc:
                controller.sortGradesAsc();
                break;

            case R.id.action_grade_sort_grade_desc:
                controller.sortGradesDesc();
                break;

            case R.id.action_grade_sort_alphabetical:
                controller.sortGradesAlp();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Changes the visibility of the progressbar.
     * @param visible bool.
     */
    @Override
    public void onProgressbarVisibility(boolean visible) {
        View view = getView();

        if (view != null) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.grades_pbar);
            progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

            SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.grades_swiperefresh);
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * Assigns a new adapter to the listview.
     * @param adapter to be assigned.
     */
    @Override
    public void onAdapterChanged(GradeAdapter adapter) {
        View view = getView();

        if (view != null) {
            ListView listView = (ListView) view.findViewById(R.id.grades_listview);
            listView.setAdapter(adapter);
        }
    }
}
