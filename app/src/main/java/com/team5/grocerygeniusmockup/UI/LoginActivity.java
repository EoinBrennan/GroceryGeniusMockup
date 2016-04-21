package com.team5.grocerygeniusmockup.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.QuizActivities.Quiz1Activity;
import com.team5.grocerygeniusmockup.Utilities.Constants;
import java.util.ArrayList;
import java.util.List;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password, or allows new users to sign up to the service.
 */

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // A boolean which when true allows user Eoin to skip the login page.
    private boolean EoinTestMode = false;

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView mErrorMessage;
    private View mProgressView;
    private View mLoginFormView;

    // Preference manager
    SharedPreferences mPrefs;

    //User's email and password if they have just created a new account
    //send email and password
    String email;
    String password;
    Boolean newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(LOG_TAG, "onCreate");

        //Bypass sign in for user if they have just created a new account. Use their new credentials
        //get email and passwrod sent from Quiz3Activity if exists
        newUser = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            password = extras.getString("password");
            newUser = true;
        }

        // Bypasses sign in for user Eoin when EoinTestMode is true. Beta for "stay logged in".
        if (EoinTestMode||newUser) {
            final String[] myEmail = {"black.leonhart@gmail.com"};
            String myPassword = "password";

            if(newUser){
                myEmail[0] = email;
                myPassword = password;
            }

            final Firebase myFirebaseRef = new Firebase(Constants.FIREBASE_URL);

            myFirebaseRef.authWithPassword(myEmail[0], myPassword, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    mPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    Intent logInIntent = new Intent(LoginActivity.this, ListActivity.class);
                    String alteredEmail = myEmail[0].replace(".","()");
                    editor.putString(Constants.USER_EMAIL, alteredEmail);
                    editor.putString(Constants.USER_ID, authData.getUid());
                    editor.commit();
                    logInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    logInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logInIntent);

                    showProgress(false);
                }

                //tests for connectivity
                private boolean isNetworkAvailable() {
                    ConnectivityManager connectivityManager
                            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.e(LOG_TAG, "Error authenticating user.");

                    //if device is not connected to the network, print statement
                    if (!isNetworkAvailable()) {
                        Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
                    }

                /* If login fails, and the user isn't sent to the main activity, the  */
                    showProgress(false);

                    String errMess = "Error!";

                    if (!firebaseError.getDetails().equals("")) {
                        mErrorMessage.setText(firebaseError.getDetails());
                    } else {
                        mErrorMessage.setText("An error has occured, ensure that you ahve the right email and password");
                    }
                }
            });
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_input_login);

        // This method fetches emails to suggest to the user from the device.
        populateAutoComplete();

        /* If the user completes an action with the editor, or clicks Sign In, they call the LogIn
         * method.
         */

        mPasswordView = (EditText) findViewById(R.id.password_input_login);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    LogIn();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LogIn();
            }
        });

        /* If the user doesn't have an account with GroceryGenius they can click new user to be
         * directed to a series of activities that gathers their information and creates an
         * account for them.
         */

        Button mCreateNewAccount = (Button) findViewById(R.id.create_new_account);
        mCreateNewAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(LoginActivity.this, Quiz1Activity.class);
                startActivity(logInIntent);
            }
        });

        // Initialising remaining UI elements.

        mErrorMessage = (TextView) findViewById(R.id.error_text_view_login);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }


    /* Calling permissions and setting up Loader for auto-complete list. */

    private void populateAutoComplete() {

        /* If the user cannot request contacts return and end the loading process. Otherwise call
         * the loader.
         */
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    // Determines whether the user can request contacts from the devices by determining...

    private boolean mayRequestContacts() {

        // ... If the version is appropriate.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // ... If the app has permission to read contacts.
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        /* ... if the user doesn't have access this prompts the user and allows them to manually
         * allow the search for contacts.
         */
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    // This method manages Firebase authentication.

    public void LogIn() {
        // Reset error bar to empty on new log in attempt
        mErrorMessage.setText("");

        // Overlays the layout with a loading spinner.
        showProgress(true);

        // Fetches input and sets up a Firebase Ref.
        final String mEmail = mEmailView.getText().toString();
        final String mPassword = mPasswordView.getText().toString();

        final Firebase myFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        // Attempt to authenticate user's email/password combination.
        myFirebaseRef.authWithPassword(mEmail, mPassword, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d(LOG_TAG, "LogIn: onAuthenticated");

                // On authentication, save the user's Firebase ID to the preferences for later use.
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = mPrefs.edit();

                // Once saved start the MainActivity.
                Intent logInIntent = new Intent(LoginActivity.this, ListActivity.class);
                String alteredEmail = mEmail.replace(".", "()");
                editor.putString(Constants.USER_EMAIL, alteredEmail);
                editor.putString(Constants.USER_ID, authData.getUid());
                editor.commit();
                logInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logInIntent);

                // Remove the loading animation for back-button navigation friendliness.
                showProgress(false);
            }

            //tests for connectivity
            private boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "Error authenticating user.");

                //if device is not connected to the network, print statement
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Please connect to internet and try again", Toast.LENGTH_LONG).show();
                }

                /* If login fails, and the user isn't sent to the Main Activity, then remove the
                 * loading animation.
                 */

                showProgress(false);

                // Display the error message to the Login Activity error TextView.
                String errMess = "Error!";

                if (!firebaseError.getDetails().equals("")) {
                    mErrorMessage.setText(firebaseError.getDetails());
                } else {
                    mErrorMessage.setText("An error has occured, ensure that you ahve the right email and password");
                }
            }
        });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        /* On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
         * for very easy animations. If available, use these APIs to fade-in
         * the progress spinner.
         */

        Log.d(LOG_TAG, "showProgress");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            /* The ViewPropertyAnimator APIs are not available, so simply show
             * and hide the relevant UI components.
             */
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /* Uses a Cursor and Loader to find the URI's on contact info, and adapt it into the
     * drop down list.
     */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLodaer");
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset");

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        Log.d(LOG_TAG, "addEmailsToAutoComplete");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

