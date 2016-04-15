package com.team5.grocerygeniusmockup.UI.MainActivityFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

// This fragment handles adding a new shop to the user's Firebase.

public class AddSectionDialogFragment extends DialogFragment {
    private static final String LOG_TAG = AddSectionDialogFragment.class.getSimpleName();

    // Declare UI elements.
    EditText mEditTextSecName;
    Spinner mOrderSpinner;

    // Declare global variables
    String shopName;
    String shopKey;

    // Used to fetch UID and set user's firebase root.
    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddSectionDialogFragment() {
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     *
     * @param shopName The name of the shop this section belongs to.
     * @param shopKey The Firebase key of the shop this section belongs to.
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
        Log.d(LOG_TAG, "onCreate");

        // Fetch data from the constructor args and initialise variables.
        shopName = getArguments().getString("shopName", "Default");
        shopKey = getArguments().getString("shopKey", "Default");
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

        // Fetch UID and set user's root node address.
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FIREBASE_MY_NODE_URL += "/" + mPrefs.getString("UserID", null);

        // Get the layout inflater and initialise UI elements.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_section, null);
        mEditTextSecName = (EditText) rootView.findViewById(R.id.dialog_add_sec_et_sec_name);
        mOrderSpinner = (Spinner) rootView.findViewById(R.id.dialog_add_sec_sp_order);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numerical_order, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mOrderSpinner.setAdapter(adapter);

        /**
         * Call addSection() when user taps "Done" keyboard action
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
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addSection();
                    }
                });

        return builder.create();
    }

    /**
     * Add new section to a shop.
     */
    public void addSection() {
        Log.d(LOG_TAG, "addSection");
        // Fetch user entered/selected data.
        String userEnteredName = mEditTextSecName.getText().toString();
        int OrderInShop = mOrderSpinner.getSelectedItemPosition() + 1;

        // If the user actually enters a list name.
        if (!userEnteredName.equals("") && userEnteredName != null) {
            // Create a new Section object using user data and parent shop name.
            Section newSec = new Section(userEnteredName, shopName, OrderInShop);

            // Set up Firebase for this shop's sections.
            String FIREBASE_MY_URL_SECTIONS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + shopKey;
            Firebase secRef = new Firebase(FIREBASE_MY_URL_SECTIONS);

            // Create a unique key for a new node and put the newly created section there.
            Firebase newSecRef = secRef.push();
            newSecRef.setValue(newSec);
        }
    }
}
