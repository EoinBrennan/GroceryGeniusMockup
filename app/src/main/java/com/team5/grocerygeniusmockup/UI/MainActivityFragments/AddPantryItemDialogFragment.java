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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.PantryModel.PantryItem;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPantryItemDialogFragment extends DialogFragment {

    EditText mEditTextItemName;
    NumberPicker mNumberPickerQuantity;
    String shelfName;
    String shelfKey;

    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddPantryItemDialogFragment() {
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddPantryItemDialogFragment newInstance(String shelfName, String shelfKey) {
        AddPantryItemDialogFragment addPantryItemDialogFragment = new AddPantryItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("shelfName", shelfName);
        bundle.putString("shelfKey", shelfKey);
        addPantryItemDialogFragment.setArguments(bundle);
        return addPantryItemDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shelfName = getArguments().getString("shelfName", "Default");
        shelfKey = getArguments().getString("shelfKey", "Default");

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
        //shop = savedInstanceState.getString("shopName");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_pantry_item, null);
        mEditTextItemName = (EditText) rootView.findViewById(R.id.edit_text_p_item_name);
        /*mNumberPickerQuantity = (NumberPicker) rootView.findViewById(R.id.itemQuantityNumberPicker);
        mNumberPickerQuantity.setMinValue(1);
        mNumberPickerQuantity.setMaxValue(100);
        mNumberPickerQuantity.setValue(1);*/

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

        //int frequencySelected = mNumberPickerQuantity.getValue();

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Date forever = new Date();
            long foreverLong = forever.getTime() + (1000*365*24*60*60*1000);

            PantryItem newItem = new PantryItem(userEnteredName, shelfName, "na", "na", "na", "na", 1, foreverLong);

            /* Fetch User ID and set up Firebase address. */

            String FIREBASE_MY_PANTRY_ITEMS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + shelfKey;

            // Get the reference to the root node in Firebase
            Firebase itemRef = new Firebase(FIREBASE_MY_PANTRY_ITEMS);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newItemRef = itemRef.push();

            final String itemID = newItemRef.getKey();

            newItemRef.setValue(newItem);
        }
    }
}
