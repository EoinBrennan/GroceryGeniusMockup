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

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.PantryModel.FirebaseTwoLayerExpandableAdapter;
import com.team5.grocerygeniusmockup.Model.PantryModel.PantryItem;
import com.team5.grocerygeniusmockup.Model.PantryModel.Shelf;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class PantryFragment extends Fragment {
    ExpandableListView mExListView;

    /* Fetch the User's ID. */

    SharedPreferences mPrefs;

    /* Firebase address for this user's node. */

    String listKey;

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
        listKey = mPrefs.getString(Constants.KEY_LIST_ID, "");


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
        mExListView.setAdapter(new FirebaseTwoLayerExpandableAdapter(getActivity(), mExListView, listKey));

        return rootView;
    }

}
