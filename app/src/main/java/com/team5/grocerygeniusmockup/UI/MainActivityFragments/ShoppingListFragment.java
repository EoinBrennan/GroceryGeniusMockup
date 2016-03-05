package com.team5.grocerygeniusmockup.UI.MainActivityFragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.FirebaseListAdapter;
import com.team5.grocerygeniusmockup.Model.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {
    ListView mListView;
    FirebaseListAdapter<Shop> mShopListAdapter;

    /* Fetch the User's ID. */

    SharedPreferences mPrefs;

    /* Firebase address for this user's node. */

    String FIREBASE_MY_NODE_URL = Constants.FIREBASE_URL_NODE;


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
        View rootView = inflater.inflate(R.layout.fragment_shoppinglists, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_view_shopping_lists);

        final Firebase rootRef = new Firebase(FIREBASE_MY_NODE_URL);
        mShopListAdapter = new FirebaseListAdapter<Shop>(getActivity(),
                Shop.class, R.layout.single_active_list, rootRef) {
            @Override
            protected void populateView(View v, Shop model) {
                super.populateView(v, model);
                TextView listNameView = (TextView) v.findViewById(R.id.text_view_list_name);
                listNameView.setText(model.getName());
            }
        };
        mListView.setAdapter(mShopListAdapter);

        return rootView;
    }

}
