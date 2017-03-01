package s.pahlplatz.fhict_companion.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.models.NewsItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailsFragment extends Fragment {
    /** The amount of characters to extract the date in 'yyyy-mm-dd' from the pubDate. **/
    private static final int DATE_PART = 10;

    private String title, content, author, pubDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newsItem News Item that contains the details about the selected news.
     * @return A new instance of fragment NewsDetailsFragment.
     */
    public static NewsDetailsFragment newInstance(final NewsItem newsItem) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", newsItem.getTitle());
        args.putString("content", newsItem.getContent());
        args.putString("author", newsItem.getAuthor());
        args.putString("pubDate", newsItem.getPubDate());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            content = getArguments().getString("content");
            author = getArguments().getString("author");
            pubDate = getArguments().getString("pubDate");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_details, container, false);

        // Title
        TextView tvTitle = (TextView) view.findViewById(R.id.news_details_title);
        tvTitle.setText(title);

        // Author
        TextView tvAuthor = (TextView) view.findViewById(R.id.news_details_author);
        String authorString = "By " + author;
        tvAuthor.setText(authorString);

        // Publish date
        TextView tvPublish = (TextView) view.findViewById(R.id.news_details_pubdate);
        tvPublish.setText(pubDate.substring(0, DATE_PART));

        // Content
        TextView tvContent = (TextView) view.findViewById(R.id.news_details_desc);
        tvContent.setText(content);

        return view;
    }
}
