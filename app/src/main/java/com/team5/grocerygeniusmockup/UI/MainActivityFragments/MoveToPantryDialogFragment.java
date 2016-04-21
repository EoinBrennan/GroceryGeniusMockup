package com.team5.grocerygeniusmockup.UI.MainActivityFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.PantryModel.PantryItem;
import com.team5.grocerygeniusmockup.Model.PantryModel.Shelf;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Item;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Shop;
import com.team5.grocerygeniusmockup.Model.SortedFirebaseArray;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoveToPantryDialogFragment extends DialogFragment {

    TextView mItemName;
    Spinner mSpinnerQuantity;
    Spinner mSpinnerShelf;

    SharedPreferences mPrefs;
    String listKey;
    String FIREBASE_MY_SHELVES_URL;

    Firebase itemRef;
    Firebase shelfRef;
    SortedFirebaseArray shelfSnapShots;

    String itemName;
    String shopName;
    String secName;
    String shopKey;
    String secKey;
    int itemQuantity;

    public MoveToPantryDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static MoveToPantryDialogFragment newInstance(String itemName, String shopName, String secName, int itemQuantity, String itemRef) {
        MoveToPantryDialogFragment moveToPantryDialogFragment = new MoveToPantryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("itemName", itemName);
        bundle.putString("shopName", shopName);
        bundle.putString("secName", secName);
        bundle.putInt("itemQuantity", itemQuantity);
        bundle.putString("itemRef", itemRef);
        moveToPantryDialogFragment.setArguments(bundle);
        return moveToPantryDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("CreatingMoveFrag", "Start");
        itemName = getArguments().getString("itemName", "item");
        shopName = getArguments().getString("shopName", "shop");
        secName = getArguments().getString("secName", "section");
        itemQuantity = getArguments().getInt("itemQuantity", 1);

        String itemKeyRef = getArguments().get("itemRef").toString();

        itemRef = new Firebase(itemKeyRef);

        String[] addressParts = itemKeyRef.split("/");
        shopKey = addressParts[5];
        secKey = addressParts[6];


        Log.e("CreatingMoveFrag", "ItemName: " + itemName + ", ItemQuan: " + itemQuantity + ", ItemRef: " + itemRef.toString());

        Log.e("CreatingMoveFrag", "ShopKey: " + shopKey + ", SecKey: " + secKey);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listKey = mPrefs.getString(Constants.KEY_LIST_ID, "");
        FIREBASE_MY_SHELVES_URL = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_SHELVES + "/" + listKey;
        shelfRef = new Firebase(FIREBASE_MY_SHELVES_URL);

        Log.e("CreatingMoveFrag", "ShelfRef: " + shelfRef.toString());

        shelfSnapShots = new SortedFirebaseArray(shelfRef);
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
        View rootView = inflater.inflate(R.layout.dialog_move_to_pantry, null);

        mItemName = (TextView) rootView.findViewById(R.id.move_to_pantry_itemname);
        mItemName.setText(itemName);

        List<Integer> quantities = new ArrayList<Integer>();
        for (int i = 0; i < itemQuantity; i++) {
            quantities.add(i+1);
        }

        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<Integer>(
                getActivity(), android.R.layout.simple_spinner_item, quantities);

        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQuantity = (Spinner) rootView.findViewById(R.id.change_quantity_spinner);
        mSpinnerQuantity.setAdapter(quantityAdapter);
        mSpinnerQuantity.setSelection(itemQuantity - 1);

        final List<String> shelves = new ArrayList<String>();
        shelves.add("");

        shelfSnapShots.setOnChangedListener(new SortedFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                Log.e("ShelfSnapChangeListener", "Index changed: " + index);

                Log.e("ShelfSnapChangeListener", index + "th element in mSSnaps is: " + shelfSnapShots.getItem(index).getValue(Shelf.class).getName() + " & key is: " + shelfSnapShots.getItem(index).getKey());
                shelves.add(index + 1, shelfSnapShots.getItem(index).getValue(Shelf.class).getName());

            }
        });

        ArrayAdapter<String> shelvesAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, shelves);

        shelvesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerShelf = (Spinner) rootView.findViewById(R.id.which_shelf_spinner);
        mSpinnerShelf.setAdapter(shelvesAdapter);

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_move_to_pantry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.e("PantryButton", "Pressed");
                        moveToPantry();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void moveToPantry() {

        int quantitySelected = mSpinnerQuantity.getSelectedItemPosition() + 1;
        int quantityRemaining = itemQuantity - quantitySelected;

        int shelfSelected = mSpinnerShelf.getSelectedItemPosition();

        Log.e("moveToPantry", "QuanSel: " + quantitySelected + ", QuanRem: " + quantityRemaining + ", shelf: " + shelfSelected);

        /* If the user actually enters a list name. */
        if (shelfSelected != 0) {
            Date now = new Date();
            long foreverLong = now.getTime() + (1000*365*24*60*60*1000);

            DataSnapshot shelfSnap = shelfSnapShots.getItem(shelfSelected - 1);

            PantryItem newPItem = new PantryItem(itemName, shelfSnap.getValue(Shelf.class).getName(), shopName, shopKey, secName, secKey, quantitySelected, foreverLong);

            /* Fetch User ID and set up Firebase address. */

            String FIREBASE_MY_PANTRY_URL = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + listKey + "/" + shelfSnap.getKey();

            // Get the reference to the root node in Firebase
            Firebase pItemRef = new Firebase(FIREBASE_MY_PANTRY_URL);

            /* Create a unique key for a new node and fetch that key. */
            Firebase newPItemRef = pItemRef.push();

            newPItemRef.setValue(newPItem);

            if (quantityRemaining <= 0) {
                itemRef.removeValue();
            } else {
                Item newItem = new Item(itemName, shopName, secName, quantityRemaining);
                itemRef.setValue(newItem);
            }
        }
    }
}
