package s.pahlplatz.fhict_companion.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.credentials.Credential;

import s.pahlplatz.fhict_companion.R;

/**
 * Fragment to show the user the fontys login form.
 */
public class TokenFragment extends Fragment {
    private static final String TAG = TokenFragment.class.getSimpleName();
    private static final String CLIENT_ID = "i874073-studentapp";
    private static final String REDIRECT_URI = "https://tas.fhict.nl/oob.html";
    private static final String OAUTH_URL = "https://identity.fhict.nl/connect/authorize";
    private static final String OAUTH_SCOPE = "fhict fhict_personal";

    private String username;
    private String password;

    private OnFragmentInteractionListener mListener;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_token, container, false);
        final WebView web = (WebView) frameLayout.findViewById(R.id.token_webview);
        final ProgressBar progressBar = (ProgressBar) frameLayout.findViewById(R.id.token_pbar);
        final boolean autoLogin = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("auto_login", false);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            private boolean authComplete = false;
            private String authCode;

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                web.addJavascriptInterface(new MyJavaScriptInterface(), "INTERFACE");

            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: LOADED: '" + url + "'");
                progressBar.setVisibility(View.INVISIBLE);

                if (url.contains("access_token=") && !authComplete) {
                    // Store current url.
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
                    View v = getView();
                    if (v != null) {
                        v.setVisibility(View.GONE);
                    }
                    if (mListener != null) {
                        mListener.onFragmentInteraction(authCode);

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Credential cred = new Credential.Builder(username).setPassword(password).build();

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                    default:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to save your credentials so you can be logged in automatically?").setPositiveButton("Yes (Recommended)", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                } else if (url.contains("error=access_denied")) {
                    Log.e(TAG, "ACCESS_DENIED_HERE");
                    getActivity().recreate();
                    authComplete = true;
                } else if (url.contains("CookieAuth.dll?GetLogon")) {
                    if (autoLogin) {
                        String username1 = "";   //TODO RETRIEVE USERNAME
                        String password1 = "";   //TODO RETRIEVE PASSWORD
                        web.loadUrl(
                                "javascript: {var login = document.getElementById('username').value='" + username1 + "';"
                                        + "var password = document.getElementById('password').value='" + password1 + "';"
                                        + "var button = document.getElementById('SubmitCreds').click();};");
                    }

                    web.loadUrl("javascript: var buttonClick = document.getElementById('SubmitCreds').onclick = "
                            + "function(){window.INTERFACE.processContent(document.getElementById('username').value,"
                            + "document.getElementById('password').value);};");
                } else if (url.contains("connect/authorize?redirect_uri")) {
                    if (autoLogin) {
                        web.loadUrl("javascript: {var button = document.getElementsByClassName('btn btn-success')[0].click();};");
                    }
                }
            }
        });

        web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=token&client_id="
                + CLIENT_ID + "&scope=" + OAUTH_SCOPE);

        // Inflate the layout for this fragment.
        return frameLayout;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface to be implemented by the caller to know when we got the token.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String token);
    }

    /**
     * An instance of this class will be registered as a JavaScript interface.
     */
    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processContent(final String user, final String pass) {
            username = user;
            password = pass;
            Log.i(TAG, "processContent: \n\tusername: " + user + "\n\tpassword: " + pass);
        }
    }
}
