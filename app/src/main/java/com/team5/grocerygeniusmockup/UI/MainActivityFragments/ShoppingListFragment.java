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
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.FirebaseThreeLayerExpandableAdapter;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;
import java.util.ArrayList;

/**
 * A ShoppingList fragment which displays the user's shopping list as an expandable list view
 * within a larger expandable list view.
 */

public class ShoppingListFragment extends Fragment {

    private static final String LOG_TAG = ShoppingListFragment.class.getSimpleName();

    /* Declare UI elements. */
    ExpandableListView mExListView;

    /* Used to fetch the User's ID. */
    SharedPreferences mPrefs;

    String listKey;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Create fragment and pass bundle with data as it's arguments.
     * Right now there are no arguments but this allows us to alter the fragment easily in future.
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
        Log.d(LOG_TAG, "onCreate");

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
        View rootView = inflater.inflate(R.layout.fragment_shoppinglists, container, false);

        /* The vast majority of the work and UI interactions are contained with the customised
         * expandable list views and adapters. Here we initialise the outer list view, and pass it
         * the context for this fragment, a reference to itself so it can open and close elements
         * of the list, and the url for the user's GroceryGenius information on Firebase.
         */

        mExListView = (ExpandableListView) rootView.findViewById(R.id.shoppinglistview_outer_level);
        mExListView.setAdapter(new FirebaseThreeLayerExpandableAdapter(getActivity(), mExListView, listKey));

        return rootView;
    }

}
