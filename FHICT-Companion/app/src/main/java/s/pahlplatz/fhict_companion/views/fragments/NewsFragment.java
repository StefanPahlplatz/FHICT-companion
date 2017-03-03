package s.pahlplatz.fhict_companion.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.NewsAdapter;
import s.pahlplatz.fhict_companion.controllers.NewsController;
import s.pahlplatz.fhict_companion.utils.WrapContentLinearLayoutManager;

/**
 * Fragment to show the Fontys news.
 */
public class NewsFragment extends Fragment implements NewsController.NewsControllerListener {
    private NewsController controller;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        getActivity().setTitle("News");
        setHasOptionsMenu(true);

        progressBar = (ProgressBar) view.findViewById(R.id.news_pbar);

        // Configure recyclerView.
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        controller = new NewsController(getContext(), this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_news_amount) {
            controller.newsAmountDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapter(final NewsAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void notifyDataSetChanged() {
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void setProgressbarVisibility(final boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
