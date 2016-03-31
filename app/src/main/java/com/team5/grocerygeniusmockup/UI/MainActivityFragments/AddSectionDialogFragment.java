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
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.Item;
import com.team5.grocerygeniusmockup.Model.Section;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSectionDialogFragment extends DialogFragment {

    EditText mEditTextSecName;
    Spinner mOrderSpinner;
    String shopName;
    String shopKey;
    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddSectionDialogFragment() {
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddSectionDialogFragment newInstance(String shopName, String shopKey) {
        AddSectionDialogFragment addSectionDialogFragment = new AddSectionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("shopName", shopName);
        bundle.putString("shopKey", shopKey);
        addSectionDialogFragment.setArguments(bundle);
        return addSectionDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopName = getArguments().getString("shopName", "Default");
        shopKey = getArguments().getString("shopKey", "Default");

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
        View rootView = inflater.inflate(R.layout.dialog_add_section, null);
        mEditTextSecName = (EditText) rootView.findViewById(R.id.edit_text_section_name);

        mOrderSpinner = (Spinner) rootView.findViewById(R.id.section_order_spinner);

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
        mEditTextSecName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addSection();
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
                        addSection();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addSection() {

        // Get the string that the user entered into the EditText and make an object with it
        // We'll use "Anonymous Owner" for the owner because we don't have user accounts yet
        String userEnteredName = mEditTextSecName.getText().toString();

        int OrderInShop = mOrderSpinner.getSelectedItemPosition() + 1;

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Section newSec = new Section(userEnteredName, shopName, OrderInShop);

            /* Fetch User ID and set up Firebase address. */

            String FIREBASE_MY_URL_SECTIONS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + shopKey;

            // Get the reference to the root node in Firebase
            Firebase secRef = new Firebase(FIREBASE_MY_URL_SECTIONS);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newSecRef = secRef.push();

            final String secID = newSecRef.getKey();

            newSecRef.setValue(newSec);
        }
    }
}
