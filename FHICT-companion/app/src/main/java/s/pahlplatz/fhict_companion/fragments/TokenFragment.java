package s.pahlplatz.fhict_companion.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import s.pahlplatz.fhict_companion.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TokenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TokenFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TokenFragment extends Fragment
{
    private static final String TAG = TokenFragment.class.getSimpleName();

    //Change the Scope as you need
    WebView web;

    private OnFragmentInteractionListener mListener;

	/**
     * @return fragment
     */
    public static TokenFragment newInstance() {
        TokenFragment fragment = new TokenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_token, container, false);

        web = (WebView) frameLayout.findViewById(R.id.token_webview);
        web.getSettings().setJavaScriptEnabled(true);
		
        web.setWebViewClient(new WebViewClient()
        {
            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
            }

            String authCode;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("access_token=") && !authComplete)
                {
                    // Store current url
                    Uri uri = Uri.parse(url);

                    // Searches the query string for the first value with the given key.
                    authCode = uri.getQueryParameter("access_token");
                    Log.i("", "CODE : " + authCode);
                    authCode = uri.getQueryParameter("#access_token");

                    String strUri = uri.toString();

                    String[] results = strUri.split("#access_token=", 2);
                    strUri = results[1];

                    results = strUri.split("&", 2);
                    authCode = results[0];

                    authComplete = true;

                    resultIntent.putExtra("code", authCode);

                    View v = getView();
                    if(v != null)
                    {
                        v.setVisibility(View.GONE);
                    }

                    if(mListener != null)
                    {
                        mListener.onFragmentInteraction(authCode);
                    }
                } else if (url.contains("error=access_denied"))
                {
                    Log.e(TAG, "ACCESS_DENIED_HERE");
                    mListener.onFragmentInteraction("LOL ERROR OFCOURSE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                }
            }
        });

        String CLIENT_ID = "i874073-studentapp";
        String REDIRECT_URI="https://tas.fhict.nl/oob.html";
        String OAUTH_URL ="https://identity.fhict.nl/connect/authorize";
        String OAUTH_SCOPE="fhict fhict_personal";

        web.loadUrl(OAUTH_URL+"?redirect_uri="+REDIRECT_URI+"&response_type=token&client_id="+CLIENT_ID+"&scope="+OAUTH_SCOPE);

        // Inflate the layout for this fragment
        return frameLayout;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String token);
    }
}
