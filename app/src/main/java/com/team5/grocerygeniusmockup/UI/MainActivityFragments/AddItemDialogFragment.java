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
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.Item;
import com.team5.grocerygeniusmockup.Model.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemDialogFragment extends DialogFragment {

    EditText mEditTextItemName;
    NumberPicker mNumberPickerQuantity;
    String shop;

    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddItemDialogFragment() {
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddItemDialogFragment newInstance(String shopName) {
        AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("shopName", shopName);
        addItemDialogFragment.setArguments(bundle);
        return addItemDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shop = getArguments().getString("shopName", "Default");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //shop = savedInstanceState.getString("shopName");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_item, null);
        mEditTextItemName = (EditText) rootView.findViewById(R.id.edit_text_item_name);
        mNumberPickerQuantity = (NumberPicker) rootView.findViewById(R.id.itemQuantityNumberPicker);
        mNumberPickerQuantity.setMinValue(1);
        mNumberPickerQuantity.setMaxValue(100);
        mNumberPickerQuantity.setValue(1);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEditTextItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addItem();
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
                        addItem();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addItem() {

        // Get the string that the user entered into the EditText and make an object with it
        // We'll use "Anonymous Owner" for the owner because we don't have user accounts yet
        String userEnteredName = mEditTextItemName.getText().toString();

        int frequencySelected = mNumberPickerQuantity.getValue();

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Item newItem = new Item(userEnteredName, shop, frequencySelected);

            /* Fetch User ID and set up Firebase address. */

            mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            FIREBASE_MY_NODE_URL += "/" + mPrefs.getString("UserID", null);
            String FIREBASE_MY_URL_ITEMS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_ITEMS;

            // Get the reference to the root node in Firebase
            Firebase itemRef = new Firebase(FIREBASE_MY_URL_ITEMS);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newItemRef = itemRef.push();

            final String itemID = newItemRef.getKey();

            newItemRef.setValue(newItem);
        }

    }

}
