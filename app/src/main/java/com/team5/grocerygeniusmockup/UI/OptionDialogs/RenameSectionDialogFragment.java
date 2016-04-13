package com.team5.grocerygeniusmockup.UI.OptionDialogs;

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
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.FirebaseInternalExpandableAdapter;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class RenameSectionDialogFragment extends DialogFragment {

    EditText mEditTextSecRename;
    String shopName;
    Firebase thisSecRef;
    int order;

    public RenameSectionDialogFragment() {
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static RenameSectionDialogFragment newInstance(String thisSecRef, String shopName, int order) {
        RenameSectionDialogFragment addSectionDialogFragment = new RenameSectionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("refcode", thisSecRef);
        bundle.putString("shopName", shopName);
        bundle.putInt("order", order);
        addSectionDialogFragment.setArguments(bundle);
        return addSectionDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisSecRef = new Firebase(getArguments().getString("refcode"));
        shopName = getArguments().getString("shopName", "Default");
        order = getArguments().getInt("order", 3);
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
        View rootView = inflater.inflate(R.layout.dialog_rename_section, null);
        mEditTextSecRename = (EditText) rootView.findViewById(R.id.edit_text_section_rename);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEditTextSecRename.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    renameSection();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_rename, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        renameSection();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void renameSection() {

        // Get the string that the user entered into the EditText and make an object with it
        // We'll use "Anonymous Owner" for the owner because we don't have user accounts yet
        String userEnteredName = mEditTextSecRename.getText().toString();

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            Section newSec = new Section(userEnteredName, shopName, order);

            thisSecRef.setValue(newSec);
        }
    }
}
