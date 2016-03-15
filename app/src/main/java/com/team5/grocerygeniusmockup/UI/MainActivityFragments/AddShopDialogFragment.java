package com.team5.grocerygeniusmockup.UI.MainActivityFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.Section;
import com.team5.grocerygeniusmockup.Model.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

public class AddShopDialogFragment extends DialogFragment {

    EditText mEditTextShopName;
    Spinner mSpinnerFrequency;

    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddShopDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddShopDialogFragment newInstance() {
        AddShopDialogFragment addShopDialogFragment = new AddShopDialogFragment();
        Bundle bundle = new Bundle();
        addShopDialogFragment.setArguments(bundle);
        return addShopDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View rootView = inflater.inflate(R.layout.dialog_add_shop, null);
        mEditTextShopName = (EditText) rootView.findViewById(R.id.edit_text_shop_name);
        mSpinnerFrequency = (Spinner) rootView.findViewById(R.id.frequency_spinner);
        mSpinnerFrequency.setSelection(3);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEditTextShopName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addShop();
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
                        addShop();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addShop() {

        // Get the string that the user entered into the EditText and make an object with it
        // We'll use "Anonymous Owner" for the owner because we don't have user accounts yet
        String userEnteredName = mEditTextShopName.getText().toString();

        int frequencySelected = mSpinnerFrequency.getSelectedItemPosition();

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Shop newShop = new Shop(userEnteredName, frequencySelected);

            /* Fetch User ID and set up Firebase address. */

            mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            FIREBASE_MY_NODE_URL += "/" + mPrefs.getString("UserID", null);
            String FIREBASE_MY_URL_SHOPS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHOPS;

            // Get the reference to the root node in Firebase
            Firebase shopRef = new Firebase(FIREBASE_MY_URL_SHOPS);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newShopRef = shopRef.push();

            final String shopKey = newShopRef.getKey();

            newShopRef.setValue(newShop);

            /* Add a default section */
            Section defSection = new Section("Other", userEnteredName, 0);

            String FIREBASE_MY_URL_SHOPSECTION = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + shopKey;
            Firebase sectionRef = new Firebase(FIREBASE_MY_URL_SHOPSECTION);

            Firebase defaultSecRef = sectionRef.push();
            defaultSecRef.setValue(defSection);
        }

    }
}
