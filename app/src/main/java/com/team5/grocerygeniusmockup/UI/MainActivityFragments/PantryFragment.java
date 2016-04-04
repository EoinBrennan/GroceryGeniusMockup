package com.team5.grocerygeniusmockup.UI.MainActivityFragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.team5.grocerygeniusmockup.Model.FirebaseTwoLayerExpandableAdapter;
import com.team5.grocerygeniusmockup.Model.Item;
import com.team5.grocerygeniusmockup.Model.Shelf;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.FirebaseThreeLayerExpandableAdapter;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PantryFragment extends Fragment {
    ExpandableListView mExListView;

    /* Fetch the User's ID. */

    SharedPreferences mPrefs;

    /* Firebase address for this user's node. */

    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    public PantryFragment() {
        // Required empty public constructor
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static PantryFragment newInstance() {
        PantryFragment fragment = new PantryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Fetch User ID and set up Firebase address. */

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FIREBASE_MY_NODE_URL += "/" + mPrefs.getString("UserID", null);

        /** Test
         */

        Boolean alreadyActivated = mPrefs.getBoolean("First", false);

        final String FIREBASE_MY_PANTRY_SHELVES = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHELVES;
        final Firebase shelfRef = new Firebase(FIREBASE_MY_PANTRY_SHELVES);

        if (!alreadyActivated) {
            Log.e("Pantry", "First!");
            Shelf fridge = new Shelf("Fridge", 1);
            Shelf cupboard = new Shelf("Cupboard", 2);

            Firebase fridgeRef = shelfRef.push();
            String fridgeKey = fridgeRef.getKey();
            fridgeRef.setValue(fridge);

            Firebase cupBRef = shelfRef.push();
            String cupBKey = cupBRef.getKey();
            cupBRef.setValue(cupboard);

            Item milk = new Item("Milk", "", "", 1);
            Item cheese = new Item("Cheese", "", "", 1);
            String fridgeItems = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + fridgeKey;
            Firebase fridgeitemsRef = new Firebase(fridgeItems);
            Firebase milkRef = fridgeitemsRef.push();
            milkRef.setValue(milk);
            Firebase cheeseRef = fridgeitemsRef.push();
            cheeseRef.setValue(cheese);

            Item biscuits = new Item("Biscuits", "", "", 1);
            String cupBItems = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + cupBKey;
            Firebase cupBItemsRef = new Firebase(cupBItems);
            Firebase biscRef = cupBItemsRef.push();
            biscRef.setValue(biscuits);

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("First", true);
            editor.commit();
        }

        /**
         * Initialize UI elements
         */

        /* The list of shops. */
        View rootView = initialiseScreen(inflater, container);

        return rootView;
    }

    public View initialiseScreen(LayoutInflater inflater, ViewGroup container) {
        /* The list of shops. */
        View rootView = inflater.inflate(R.layout.fragment_pantry, container, false);

        mExListView = (ExpandableListView) rootView.findViewById(R.id.pantry_shelves);
        mExListView.setAdapter(new FirebaseTwoLayerExpandableAdapter(getActivity(), mExListView, FIREBASE_MY_NODE_URL));

        return rootView;
    }

}
