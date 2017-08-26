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
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
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
import s.pahlplatz.fhict_companion.utils.PreferenceHelper;

/** 
 * ============= DO NOT USE IN CURRENT STATE! =============
 * 
 * DEPRECATED SINCE FONTYS CHANGED THEIR AUTH SYSTEM!
 * Request your own credentials here: https://api.fhict.nl/Documentation/RequestAccess
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String CLIENT_ID = // CLIENT_ID
    private static final String REDIRECT_URI = // REDIRECT_URI
    private static final String OAUTH_URL = // OAUTH_URL
    private static final String OAUTH_SCOPE = "fhict fhict_personal";
    private static final String FULL_URL = OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=token&client_id="
            + CLIENT_ID + "&scope=" + OAUTH_SCOPE;
    private static final int RC_SAVE = 1;
    private static final int RC_READ = 3;

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
        autoLogin = PreferenceHelper.getBoolean(this, PreferenceHelper.AUTO_LOGIN);

        // Adjust browser settings.
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setVerticalScrollBarEnabled(false);

        // Disable scrolling.
        web.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        BrowserHelper.clearCookies(this);

        // Handle javascript errors.
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
                Log.e("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                // Restart the activity if we missed a step in the js execution.
                if (consoleMessage.message().contains("Uncaught TypeError")) {
                    finish();
                    startActivity(getIntent());
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });

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
                view.setVisibility(View.INVISIBLE);

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
                        // Auto login script.
                        web.loadUrl(
                                "javascript: {var login = document.getElementById('username').value='" + credential.getId() + "';"
                                        + "var password = document.getElementById('password').value='" + credential.getPassword() + "';"
                                        + "var button = document.getElementById('SubmitCreds').click();};");
                    } else {
                        // Subscribe to the 'Log in' click event to capture the credentials.
                        // Also hides some elements.
                        web.loadUrl("javascript: var checkbox = document.getElementsByClassName('checkbox')[0].style.visibility = 'hidden';"
                                + "var body = document.getElementsByClassName('controls')[2].style.visibility = 'hidden';"
                                + "var sec = document.getElementsByClassName('control-label')[2].style.visibility = 'hidden';"
                                + "var pme = document.getElementsByTagName('iframe')[0].style.visibility = 'hidden';"
                                + "var buttonClick = document.getElementById('SubmitCreds').onclick = "
                                + "function(){window.INTERFACE.processJavascriptCallback(document.getElementById('username').value,"
                                + "document.getElementById('password').value)} ");
                        showBrowser();
                    }

                    // =================================================================
                    // AUTHORIZATION PAGE
                    // =================================================================
                } else if (url.contains("connect/authorize?redirect_uri")) {
                    if (autoLogin) {
                        web.loadUrl("javascript: {var button = document.getElementsByClassName('btn btn-success')[0].click();};");
                    } else {
                        web.loadUrl("javascript: var button = document.getElementsByClassName('btn btn-danger')[0].style.visibility = 'hidden';"
                                + "var btn2 = document.getElementsByClassName('pull-right btn btn-default')[0].style.visibility = 'hidden';"
                                + "var headerBar = document.getElementsByClassName('navbar navbar-default navbar-fixed-top')[0].style.visibility = 'hidden';"
                                + "var text = document.getElementsByClassName('col-md-8 col-xs-8')[0].style.visibility = 'hidden';");
                        showBrowser();
                    }

                    // =================================================================
                    // ACCESS DENIED!
                    // =================================================================
                } else if (url.contains("error=access_denied")) {
                    Log.e(TAG, "ACCESS_DENIED_HERE");
                    authComplete = true;
                    showBrowser();

                    // =================================================================
                    // WE'RE ON THE FINAL PAGE
                    // =================================================================
                } else if (url.contains("access_token=") && !authComplete) {
                    // Get the auth token.
                    authCode = getAuthCode(url);
                    authComplete = true;

                    if (!autoLogin) {
                        Log.v(TAG, "AUTO_LOGIN == FALSE");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                        builder.setTitle("Enable auto-login?");

                        builder.setMessage("Do you want to save your credentials so you can be logged in automatically?")
                                .setPositiveButton("Yes (Recommended)", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                                                .edit()
                                                .putBoolean("auto_login", true)
                                                .apply();
                                        saveCredential();       // Save credentials to google API.
                                        saveToken(authCode);    // Save the access token.
                                        startMainActivity();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveToken(authCode);    // Save the access token.
                                        startMainActivity();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Log.v(TAG, "AUTO_LOGIN == TRUE");
                        saveToken(authCode);    // Save the access token.
                        startMainActivity();
                    }
                }
            }

            private void showBrowser() {
                if (!web.getUrl().contains("tas.fhict.nl/oob.html")) {
                    web.setVisibility(View.VISIBLE);
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
        String strUri = uri.toString();
        String[] results = strUri.split("#access_token=", 2);
        strUri = results[1];
        results = strUri.split("&", 2);
        authCode = results[0];
        Log.i(TAG, "CODE: " + authCode);
        return authCode;
    }

    /**
     * Saves the credentials to the API.
     */
    private void saveCredential() {
        Auth.CredentialsApi.save(mGoogleApiClient, credential).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull final Status status) {
                if (status.isSuccess()) {
                    Log.v(TAG, "Credential saved");
                } else {
                    Log.e(TAG, "Attempt to save credential failed " + status.getStatusMessage() + " "
                            + status.getStatusCode());
                    resolveResult(status, RC_SAVE);
                }
            }
        });
    }

    /**
     * Processes the info from requestCredentials().
     */
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
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            } else {
                Log.e(TAG, "Credential Read: NOT OK");
            }
            // Load the url to let the user sign in.
            web.loadUrl(FULL_URL);
        } else if (requestCode == RC_SAVE) {
            Log.d(TAG, "Result code: " + resultCode);
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "Credential Save: OK");
            } else {
                Log.e(TAG, "Credential Save Failed");
            }
        }
        mIsResolving = false;
    }

    /**
     * Request Credentials once connected. If credentials are retrieved the user will either
     * be automatically signed in or will be required to log in himself.
     */
    @Override
    public void onConnected(final Bundle bundle) {
        Log.i(TAG, "onConnected: Requesting credentials.");
        requestCredentials();
    }

    @Override
    public void onConnectionSuspended(final int cause) {
        Log.e(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

    /**
     * Request the credentials from the google API.
     * If there are credentials, save them and load the web link to start the authentication process.
     * If the user doesn't want to auto login, just load the login page.
     */
    private void requestCredentials() {
        if (autoLogin) {
            CredentialRequest request = new CredentialRequest.Builder()
                    .setSupportsPasswordLogin(true)
                    .build();

            Auth.CredentialsApi.request(mGoogleApiClient, request).setResultCallback(
                    new ResultCallback<CredentialRequestResult>() {
                        @Override
                        public void onResult(@NonNull final CredentialRequestResult credentialRequestResult) {
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
                                // Load the url to let the user sign in.
                                web.loadUrl(FULL_URL);
                            } else if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                                // This is most likely the case where the user has multiple saved
                                // credentials and needs to pick one.
                                resolveResult(status, RC_READ);
                            } else if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                                // This is most likely the case where the user does not currently
                                // have any saved credentials and thus needs to provide a username
                                // and password to sign in.
                                Log.d(TAG, "Sign in required");
                                // Load the url to let the user sign in.
                                web.loadUrl(FULL_URL);
                            } else {
                                Log.w(TAG, "Unrecognized status code: " + status.getStatusCode());
                            }
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
        PreferenceHelper.save(getBaseContext(), PreferenceHelper.TOKEN, token);
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
        }
    }
}
