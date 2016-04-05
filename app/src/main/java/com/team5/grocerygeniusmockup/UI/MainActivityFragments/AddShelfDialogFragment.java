package com.team5.grocerygeniusmockup.UI.MainActivityFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AddShelfDialogFragment extends DialogFragment {

    EditText mEditTextShelfName;
    Spinner mOrderSpinner;
    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddShelfDialogFragment() {
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

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FIREBASE_MY_NODE_URL += "/" + mPrefs.getString("UserID", null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_shelf, null);
        mEditTextShelfName = (EditText) rootView.findViewById(R.id.edit_text_shelf_name);

        mOrderSpinner = (Spinner) rootView.findViewById(R.id.shelf_order_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.section_order, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mOrderSpinner.setAdapter(adapter);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
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

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_add_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addShelf();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addShelf() {

        // Get the string that the user entered into the EditText and make an object with it
        // We'll use "Anonymous Owner" for the owner because we don't have user accounts yet
        String userEnteredName = mEditTextShelfName.getText().toString();

        int OrderInShelf = mOrderSpinner.getSelectedItemPosition() + 1;

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Shelf newShelf = new Shelf(userEnteredName, OrderInShelf);

            /* Fetch User ID and set up Firebase address. */

            String FIREBASE_MY_URL_SHELVES = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHELVES;

            // Get the reference to the root node in Firebase
            Firebase shelfRef = new Firebase(FIREBASE_MY_URL_SHELVES);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newShelfRef = shelfRef.push();

            final String secID = newShelfRef.getKey();

            newShelfRef.setValue(newShelf);
        }
    }
}
