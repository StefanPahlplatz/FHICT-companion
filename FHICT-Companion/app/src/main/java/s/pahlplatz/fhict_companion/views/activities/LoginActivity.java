package s.pahlplatz.fhict_companion.views.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.utils.BrowserHelper;

/**
 * Activity that hosts the login fragment.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String CLIENT_ID = "i874073-studentapp";
    private static final String REDIRECT_URI = "https://tas.fhict.nl/oob.html";
    private static final String OAUTH_URL = "https://identity.fhict.nl/connect/authorize";
    private static final String OAUTH_SCOPE = "fhict fhict_personal";
    private static final String FULL_URL = OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=token&client_id="
            + CLIENT_ID + "&scope=" + OAUTH_SCOPE;
    private static final int RC_SAVE = 1;

    private boolean mIsRequesting;
    private boolean mIsResolving;
    private boolean autoLogin;
    private Credential credential;
    private GoogleApiClient mGoogleApiClient;
    private WebView web;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_token);

        // Initialize the google api.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        // Assign elements.
        web = (WebView) findViewById(R.id.token_webview);
        progressBar = (ProgressBar) findViewById(R.id.token_pbar);
        autoLogin = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("auto_login", false);

        // Adjust browser settings.
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        BrowserHelper.clearCookies(this);

        // Configure the web client.
        web.setWebViewClient(new WebViewClient() {
            private boolean authComplete = false;       // Whether the process is done or not.
            private String authCode;                    // This will hold the token.

            /**
             * Triggered when a page starts loading.
             * If the page is the login screen, add the javascript listener so we can extract the credentials.
             */
            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("CookieAuth.dll?GetLogon")) {
                    web.addJavascriptInterface(new MyJavaScriptInterface(), "INTERFACE");
                }
            }

            /**
             * Oh boy..
             * Handlers for all loaded pages.
             *
             * @param view the webview.
             * @param url  of the finished page.
             */
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: LOADED: '" + url + "'");

                // Hide the progressbar.
                if (credential == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                // =================================================================
                // LOGIN FORM
                // =================================================================
                if (url.contains("CookieAuth.dll?GetLogon")) {
                    if (autoLogin && credential != null) {
                        web.loadUrl(
                                "javascript: {var login = document.getElementById('username').value='" + credential.getId() + "';"
                                        + "var password = document.getElementById('password').value='" + credential.getPassword() + "';"
                                        + "var button = document.getElementById('SubmitCreds').click();};");
                    }

                    // Subscribe to the 'Log in' click event to capture the credentials.
                    web.loadUrl("javascript: var buttonClick = document.getElementById('SubmitCreds').onclick = "
                            + "function(){window.INTERFACE.processJavascriptCallback(document.getElementById('username').value,"
                            + "document.getElementById('password').value);};");


                } else if (url.contains("connect/authorize?redirect_uri")) {
                    if (autoLogin) {
                        web.loadUrl("javascript: {var button = document.getElementsByClassName('btn btn-success')[0].click();};");
                    }

                } else if (url.contains("error=access_denied")) {
                    Log.e(TAG, "ACCESS_DENIED_HERE");
                    authComplete = true;
                    //TODO: hide the deny access button with js injection.


                } else if (url.contains("access_token=") && !authComplete) {
                    // Get the auth token.
                    authCode = getAuthCode(url);
                    authComplete = true;

                    if (credential == null) {
                        // Ask the user if they want to auto login.
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                                                .edit()
                                                .putBoolean("auto_login", true)
                                                .apply();
                                        saveCredential();       // Save credentials to google API.
                                        saveToken(authCode);    // Save the access token.
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        saveToken(authCode);    // Save the access token.
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Do you want to save your credentials so you can be logged in automatically?")
                                .setPositiveButton("Yes (Recommended)", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    } else {
                        saveToken(authCode);    // Save the access token.
                    }
                }
            }
        });
    }


    /**
     * Extracts the auth token from the url.
     *
     * @param url with the token.
     * @return token.
     */
    private String getAuthCode(final String url) {
        Uri uri = Uri.parse(url);

        // Searches the query string for the first value with the given key.
        String authCode = uri.getQueryParameter("access_token");
        Log.i(TAG, "CODE : " + authCode);
        String strUri = uri.toString();
        String[] results = strUri.split("#access_token=", 2);
        strUri = results[1];
        results = strUri.split("&", 2);
        authCode = results[0];
        return authCode;
    }

    /**
     * Saves the credentials to the API.
     */
    void saveCredential() {
        Auth.CredentialsApi.save(mGoogleApiClient, credential).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull final Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "Credential saved");
                } else {
                    Log.d(TAG, "Attempt to save credential failed " + status.getStatusMessage() + " "
                            + status.getStatusCode());
                    resolveResult(status, RC_SAVE);
                }
            }
        });
    }

    private void resolveResult(final Status status, final int requestCode) {
        // We don't want to fire multiple resolutions at once since that
        // can result in stacked dialogs after rotation or another
        // similar event.
        if (mIsResolving) {
            Log.w(TAG, "resolveResult: already resolving.");
            return;
        }

        Log.d(TAG, "Resolving: " + status);
        if (status.hasResolution()) {
            Log.d(TAG, "STATUS: RESOLVING");
            try {
                status.startResolutionForResult(this, requestCode);
                mIsResolving = true;
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "STATUS: Failed to send resolution.", e);
            }
        } else {
            Log.e(TAG, "STATUS: FAIL");
            if (requestCode == RC_SAVE) {
                //goToContent();
                Log.i(TAG, "resolveResult: would call goToContent");
            }
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_SAVE) {
            Log.d(TAG, "Result code: " + resultCode);
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Credential Save: OK");
            } else {
                Log.e(TAG, "Credential Save Failed");
            }
            //goToContent();
            Log.i(TAG, "resolveResult: would call goToContent");
        }
        mIsResolving = false;
    }

    /**
     * Request Credentials once connected. If credentials are retrieved the user will either
     * be automatically signed in or will be required to log in himself.
     */
    @Override
    public void onConnected(final Bundle bundle) {
        Log.d(TAG, "onConnected: Requesting credentials.");
        requestCredentials();
    }

    @Override
    public void onConnectionSuspended(final int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    /**
     * Request the credentials from the google API.
     * If there are credentials, save them and load the web link to start the authentication process.
     * If the user doesn't want to auto login, just load the login page.
     */
    private void requestCredentials() {
        mIsRequesting = true;
        if (autoLogin) {
            CredentialRequest request = new CredentialRequest.Builder()
                    .setSupportsPasswordLogin(true)
                    .build();

            Auth.CredentialsApi.request(mGoogleApiClient, request).setResultCallback(
                    new ResultCallback<CredentialRequestResult>() {
                        @Override
                        public void onResult(@NonNull final CredentialRequestResult credentialRequestResult) {
                            mIsRequesting = false;
                            Status status = credentialRequestResult.getStatus();
                            if (credentialRequestResult.getStatus().isSuccess()) {
                                // Successfully read the credential without any user interaction, this
                                // means there was only a single credential and the user has auto
                                // sign-in enabled.
                                credential = credentialRequestResult.getCredential();
                                web.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                                TextView loading = (TextView) findViewById(R.id.token_loading);
                                loading.setVisibility(View.VISIBLE);


                            } else if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                                // This is most likely the case where the user does not currently
                                // have any saved credentials and thus needs to provide a username
                                // and password to sign in.
                                Log.d(TAG, "Sign in required");
                            } else {
                                Log.w(TAG, "Unrecognized status code: " + status.getStatusCode());
                            }

                            // Load the url to let the user sign in.
                            web.loadUrl(FULL_URL);
                        }
                    }
            );
        } else {
            // Load the url to let the user sign in.
            web.loadUrl(FULL_URL);
        }
    }

    /**
     * Saves the token to sp and starts the main activity.
     *
     * @param token to save.
     */
    private void saveToken(final String token) {
        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", token).apply();
        startMainActivity();
    }

    /**
     * Starts the main activity.
     */
    private void startMainActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("online", true);
        startActivity(intent);
        finish();
    }

    /**
     * An instance of this class will be registered as a JavaScript interface.
     */
    private class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processJavascriptCallback(final String user, final String pass) {
            credential = new Credential.Builder(user)
                    .setPassword(pass)
                    .build();
            Log.i(TAG, "processContent:\n\tusername: " + user + "\n\tpassword: " + pass);
        }
    }
}
