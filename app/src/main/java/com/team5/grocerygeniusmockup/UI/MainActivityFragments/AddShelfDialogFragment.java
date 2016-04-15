package com.team5.grocerygeniusmockup.UI.MainActivityFragments;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.PantryModel.Shelf;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

// This fragment handles adding a new shelf to the user's Firebase.

public class AddShelfDialogFragment extends DialogFragment {

    private static final String LOG_TAG = AddShopDialogFragment.class.getSimpleName();

    // Declaring UI elements.
    EditText mEditTextShelfName;
    Spinner mOrderSpinner;

    // Used to fetch the User's ID and set this user's Firebase root.
    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddShelfDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddShelfDialogFragment newInstance() {
        AddShelfDialogFragment addShelfDialogFragment = new AddShelfDialogFragment();
        Bundle bundle = new Bundle();
        addShelfDialogFragment.setArguments(bundle);
        return addShelfDialogFragment;
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
        FIREBASE_MY_NODE_URL += "/" + mPrefs.getString("UserID", null);


        // Get the layout inflater inflate the appropriate layout and initialise the UI.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_shelf, null);
        mEditTextShelfName = (EditText) rootView.findViewById(R.id.dialog_add_shelf_et_shelf_name);
        mOrderSpinner = (Spinner) rootView.findViewById(R.id.dialog_add_shelf_sp_order);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numerical_order, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mOrderSpinner.setAdapter(adapter);

        /**
         * Call addShelf() when user taps "Done" keyboard action
         */
        mEditTextShelfName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addShelf();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog
         * Pass null as the parent view because its going in the dialog layout
         */
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addShelf();
                    }
                });

        return builder.create();
    }

    /**
     * Add new shelf
     */
    public void addShelf() {
        // Fetch the user's chosen name and order.
        String userEnteredName = mEditTextShelfName.getText().toString();
        int OrderInShelf = mOrderSpinner.getSelectedItemPosition() + 1;

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            // Create a new Shelf object with the user's data.
            Shelf newShelf = new Shelf(userEnteredName, OrderInShelf);

            // Set up a Firebase address and node reference for the user's shelves.
            String FIREBASE_MY_URL_SHELVES = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHELVES;
            Firebase shelfRef = new Firebase(FIREBASE_MY_URL_SHELVES);

            // Create a unique key for a new node and place the shelf item at that node.
            Firebase newShelfRef = shelfRef.push();
            newShelfRef.setValue(newShelf);
        }
    }
}
