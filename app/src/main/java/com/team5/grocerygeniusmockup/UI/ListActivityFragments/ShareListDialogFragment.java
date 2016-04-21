package com.team5.grocerygeniusmockup.UI.ListActivityFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.team5.grocerygeniusmockup.Model.ListModel.List;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This fragment handles adding a new shop to the user's Firebase.

public class ShareListDialogFragment extends DialogFragment {
    private static final String LOG_TAG = ShareListDialogFragment.class.getSimpleName();

    // Declaring UI elements.
    EditText mEmail;

    // Used to fetch the User's ID and set this user's Firebase root.
    SharedPreferences mPrefs;
    String listKey;
    String userEmail;
    final String[] deets = new String[3];
    final ArrayList<String> currentSharedGuys = new ArrayList<String>();
    String userEnteredName;

    public ShareListDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created.
     */
    public static ShareListDialogFragment newInstance() {
        ShareListDialogFragment addShopDialogFragment = new ShareListDialogFragment();
        Bundle bundle = new Bundle();
        addShopDialogFragment.setArguments(bundle);
        return addShopDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateDialog");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

       // Fetch User ID and set up Firebase address.

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listKey = mPrefs.getString(Constants.KEY_LIST_ID, "");
        userEmail = mPrefs.getString(Constants.USER_EMAIL, "");

        // Get the layout inflater inflate the appropriate layout and initialise the UI.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_share_list, null);
        mEmail = (EditText) rootView.findViewById(R.id.dialog_share_list_et_email);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    shareList();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        shareList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void shareList() {
        Log.d(LOG_TAG, "addShop");
        // Fetch the user entered shop name.
        userEnteredName = mEmail.getText().toString();

        /* If and only if the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            // Create a new shop object using user data.

            Firebase listRef = new Firebase(Constants.FIREBASE_URL_LISTS + "/" + userEmail + "/" + listKey);

            listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List thisList = snapshot.getValue(List.class);
                    deets[0] = thisList.getName();
                    deets[1] = thisList.getOwner();
                    deets[2] = thisList.getOwnerUID();
                    loadList();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }

    }

    public void loadList() {
        List newList = new List(deets[0], deets[1], userEmail, deets[2]);

        userEnteredName = userEnteredName.replace(".","()");

        // Set up a ref in the shops section of the user's Firebase.
        String FIREBASE_MY_LISTS_URL = Constants.FIREBASE_URL_LISTS + "/" + userEnteredName;

        // Initialise a Firebase object at the shops node.
        Firebase myListsRef = new Firebase(FIREBASE_MY_LISTS_URL);

        // Create a unique key for a new child and fetch it's key.
        Firebase newListRef = myListsRef.child(listKey);

        // Store the Shop object at the new child node.
        newListRef.setValue(newList);

        Firebase mySharedRef = new Firebase(Constants.FIREBASE_URL_SHARED).child(listKey);

        Map<String, Object> sharing = new HashMap<String, Object>();
        sharing.put(userEnteredName, true);
        mySharedRef.updateChildren(sharing);
    }
}
