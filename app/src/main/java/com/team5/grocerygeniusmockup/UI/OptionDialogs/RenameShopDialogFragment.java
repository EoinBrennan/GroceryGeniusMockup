package com.team5.grocerygeniusmockup.UI.OptionDialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

// This fragment handles adding a new shop to the user's Firebase.

public class RenameShopDialogFragment extends DialogFragment {
    private static final String LOG_TAG = RenameShopDialogFragment.class.getSimpleName();

    // Declare UI element
    EditText mEditTextShopRename;

    // Declare global variables
    Firebase thisShopRef;
    int frequency;

    public RenameShopDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     *
     * @param thisShopRef A string containing the firebase address of the shop being renamed.
     * @param frequency The shop's frequency of visitation.
     */
    public static RenameShopDialogFragment newInstance(String thisShopRef, int frequency) {
        RenameShopDialogFragment addRenameShopDialogFragment = new RenameShopDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("refcode", thisShopRef);
        bundle.putInt("freq", frequency);
        addRenameShopDialogFragment.setArguments(bundle);
        return addRenameShopDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // Initialise variables.
        thisShopRef = new Firebase( getArguments().getString("refcode", "") );
        frequency = getArguments().getInt("freq", 3);
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
        // Get the layout inflater and initialise UI element
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_rename_shop, null);
        mEditTextShopRename = (EditText) rootView.findViewById(R.id.edit_text_shop_rename);

        /**
         * Call renameShop() when user taps "Done" keyboard action
         */
        mEditTextShopRename.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    renameShop();
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
                        renameShop();
                    }
                });

        return builder.create();
    }

    public void renameShop() {

        // Fetch user input
        String userEnteredName = mEditTextShopRename.getText().toString();

        /* If the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            // Replace current shop with new Shop object with only name being different.
            Shop newShop = new Shop(userEnteredName, frequency);
            thisShopRef.setValue(newShop);
        }
    }
}
