package com.team5.grocerygeniusmockup.UI.MainActivityFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

// This fragment handles adding a new shop to the user's Firebase.

public class AddShopDialogFragment extends DialogFragment {
    private static final String LOG_TAG = AddShopDialogFragment.class.getSimpleName();

    // Declaring UI elements.
    EditText mEditTextShopName;
    Spinner mSpinnerFrequency;

    // Used to fetch the User's ID and set this user's Firebase root.
    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public AddShopDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created.
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
        View rootView = inflater.inflate(R.layout.dialog_add_shop, null);
        mEditTextShopName = (EditText) rootView.findViewById(R.id.dialog_add_shop_et_shop_name);
        mSpinnerFrequency = (Spinner) rootView.findViewById(R.id.dialog_add_shop_sp_frequency);

        // 3 here corresponds to the third option in the frequency shop visit array in res/strings.
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
        Log.d(LOG_TAG, "addShop");
        // Fetch the user entered shop name.
        String userEnteredName = mEditTextShopName.getText().toString();

        // Fetch the frequency option selected by the user.
        int frequencySelected = mSpinnerFrequency.getSelectedItemPosition();

        /* If and only if the user actually enters a list name. */
        if (!userEnteredName.equals("") && userEnteredName != null) {
            // Create a new shop object using user data.
            Shop newShop = new Shop(userEnteredName, frequencySelected);

            // Set up a ref in the shops section of the user's Firebase.
            String FIREBASE_MY_URL_SHOPS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHOPS;

            // Initialise a Firebase object at the shops node.
            Firebase shopRef = new Firebase(FIREBASE_MY_URL_SHOPS);

            // Create a unique key for a new child and fetch it's key.
            Firebase newShopRef = shopRef.push();
            String newShopKey = newShopRef.getKey();

            // Store the Shop object at the new child node.
            newShopRef.setValue(newShop);

            // Add a default section to the newly created shop by following the same process.
            Section defaultSection = new Section("Other", userEnteredName, 0);
            String FIREBASE_MY_URL_SHOPSECTION = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + newShopKey;
            Firebase sectionRef = new Firebase(FIREBASE_MY_URL_SHOPSECTION);
            Firebase defaultSecRef = sectionRef.push();
            defaultSecRef.setValue(defaultSection);
        }

    }
}
