package s.pahlplatz.fhict_companion.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.models.NewsItem;

public class NewsDetailsFragment extends Fragment {
    private String title, content, author, pubDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newsItem News Item that contains the details about the selected news.
     * @return A new instance of fragment NewsDetailsFragment.
     */
    public static NewsDetailsFragment newInstance(NewsItem newsItem) {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            content = getArguments().getString("content");
            author = getArguments().getString("author");
            pubDate = getArguments().getString("pubDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_details, container, false);

        // Title
        TextView tv_Title = (TextView) view.findViewById(R.id.news_details_title);
        tv_Title.setText(title);

        // Author
        TextView tv_Author = (TextView) view.findViewById(R.id.news_details_author);
        String authorString = "By " + author;
        tv_Author.setText(authorString);

        // Publish date
        TextView tv_Publish = (TextView) view.findViewById(R.id.news_details_pubdate);
        tv_Publish.setText(pubDate.substring(0, 10));

        // Content
        TextView tv_Content = (TextView) view.findViewById(R.id.news_details_desc);
        tv_Content.setText(content);

        return view;
    }
}
