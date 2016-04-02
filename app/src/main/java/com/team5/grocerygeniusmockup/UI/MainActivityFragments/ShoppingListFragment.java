package com.team5.grocerygeniusmockup.UI.MainActivityFragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.team5.grocerygeniusmockup.Model.ShoppingListModel.FirebaseThreeLayerExpandableAdapter;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {
    ListView mListView;
    ExpandableListView mExListView;

    /* Fetch the User's ID. */

    SharedPreferences mPrefs;

    /* Firebase address for this user's node. */

    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;

    /* Section stuff */

    ArrayList<String> sectionKeys;
    ArrayList<String> sectionNames;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
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

        /**
         * Initialize UI elements
         */

        /* The list of shops. */
        View rootView = initialiseScreen(inflater, container);

        return rootView;
    }

    public View initialiseScreen(LayoutInflater inflater, ViewGroup container) {
        String FIREBASE_MY_URL_SHOPS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHOPS;

        /* The list of shops. */
        View rootView = inflater.inflate(R.layout.fragment_shoppinglists, container, false);

        mExListView = (ExpandableListView) rootView.findViewById(R.id.parent_level);
        mExListView.setAdapter(new FirebaseThreeLayerExpandableAdapter(getActivity(), mExListView, FIREBASE_MY_NODE_URL));

        return rootView;
    }

}
