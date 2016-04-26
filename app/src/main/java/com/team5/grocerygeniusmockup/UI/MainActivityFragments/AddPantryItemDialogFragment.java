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
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.PantryModel.PantryItem;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPantryItemDialogFragment extends DialogFragment {

    EditText mEditTextItemName;
    NumberPicker mNumberPickerQuantity;
    CheckBox mCheckbox;
    DatePicker mCalendar;
    String shelfName;
    String shelfKey;

    SharedPreferences mPrefs;
    String listKey;

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
        listKey = mPrefs.getString(Constants.KEY_LIST_ID, "");
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
        mNumberPickerQuantity = (NumberPicker) rootView.findViewById(R.id.pItemQuantityNumberPicker);
        mNumberPickerQuantity.setMinValue(1);
        mNumberPickerQuantity.setMaxValue(100);
        mNumberPickerQuantity.setValue(1);

        mCheckbox = (CheckBox) rootView.findViewById(R.id.add_p_item_exp_checkbox);
        mCalendar = (DatePicker) rootView.findViewById(R.id.datePicker2);
        mCalendar.setVisibility(View.GONE);
        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckbox.isChecked()) {
                    mCalendar.setVisibility(View.VISIBLE);
                } else {
                    mCalendar.setVisibility(View.GONE);
                }
            }
        });

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

        int quantitySelected = mNumberPickerQuantity.getValue();

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        String month;
        int monthnum = mCalendar.getMonth() + 1;
        if (monthnum < 10) {
            month = "0" + monthnum;
        } else {
            month = "" + monthnum;
        }

        String day;
        int daynum = mCalendar.getDayOfMonth();
        if (daynum < 10) {
            day = "0" + daynum;
        } else {
            day = "" + daynum;
        }

        String strDate = mCalendar.getYear() + "-" + month + "-" + day;
        Toast.makeText(getActivity(), strDate, Toast.LENGTH_LONG).show();
        Date date;
        try {
            date = ft.parse(strDate);
        } catch (ParseException e) {
            date = new Date();
        }

        long dateSet = date.getTime();

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Date today = new Date();
            long now = today.getTime();
            if (!(mCheckbox.isChecked())) {
                dateSet = now + (1000*365*24*60*60*1000);
            }

            if (dateSet < now) {
                dateSet = now;
            }

            PantryItem newItem = new PantryItem(userEnteredName, shelfName, "na", "na", "na", "na", quantitySelected, dateSet);

            /* Fetch User ID and set up Firebase address. */

            String FIREBASE_MY_PANTRY_ITEMS = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + listKey +"/" + shelfKey;

            // Get the reference to the root node in Firebase
            Firebase itemRef = new Firebase(FIREBASE_MY_PANTRY_ITEMS);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newItemRef = itemRef.push();

            final String itemID = newItemRef.getKey();

            newItemRef.setValue(newItem);
        }
    }
}
