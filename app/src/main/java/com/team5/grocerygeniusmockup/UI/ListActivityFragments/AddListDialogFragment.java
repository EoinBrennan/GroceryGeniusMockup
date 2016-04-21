package com.team5.grocerygeniusmockup.UI.ListActivityFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.ListModel.List;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.HashMap;
import java.util.Map;

// This fragment handles adding a new shop to the user's Firebase.

public class AddListDialogFragment extends DialogFragment {
    private static final String LOG_TAG = AddListDialogFragment.class.getSimpleName();

    // Declaring UI elements.
    EditText mEditTextListName;

    String userID;
    String userEmail;

    public AddListDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created.
     */
    public static AddListDialogFragment newInstance(String userEmail, String userID) {
        AddListDialogFragment addListDialogFragment = new AddListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID, userID);
        bundle.putString(Constants.USER_EMAIL, userEmail);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getString(Constants.USER_ID);
        userEmail = getArguments().getString(Constants.USER_EMAIL);
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

        // Get the layout inflater inflate the appropriate layout and initialise the UI.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_list, null);
        mEditTextListName = (EditText) rootView.findViewById(R.id.dialog_add_list_et_list_name);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addList();
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
                        addList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addList() {
        Log.d(LOG_TAG, "addList");
        // Fetch the user entered shop name.
        String userEnteredName = mEditTextListName.getText().toString();

        /* If and only if the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            // Create a new shop object using user data.

            List newList = new List(userEnteredName, "user", userEmail, userID);

            // Set up a ref in the shops section of the user's Firebase.
            String FIREBASE_MY_LISTS_URL = Constants.FIREBASE_URL_LISTS + "/" + userEmail;

            // Initialise a Firebase object at the shops node.
            Firebase myListsRef = new Firebase(FIREBASE_MY_LISTS_URL);

            // Create a unique key for a new child and fetch it's key.
            Firebase newListRef = myListsRef.push();
            String newListKey = newListRef.getKey();

            // Store the Shop object at the new child node.
            newListRef.setValue(newList);

            Firebase mySharedRef = new Firebase(Constants.FIREBASE_URL_SHARED).child(newListKey);
            Map<String, Object> sharing = new HashMap<String, Object>();
            sharing.put(userEmail, true);
            mySharedRef.updateChildren(sharing);
        }
    }
}
