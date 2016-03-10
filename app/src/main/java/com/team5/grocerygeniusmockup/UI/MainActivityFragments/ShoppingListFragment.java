package com.team5.grocerygeniusmockup.UI.MainActivityFragments;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.team5.grocerygeniusmockup.Model.FirebaseListAdapter;
import com.team5.grocerygeniusmockup.Model.Item;
import com.team5.grocerygeniusmockup.Model.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.MainActivity;
import com.team5.grocerygeniusmockup.Utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {
    ListView mListView;
    FirebaseListAdapter<Shop> mShopListAdapter;
    FirebaseListAdapter<Item> mItemListAdapter;

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
        View rootView = initialiseScreen(inflater, container);

        return rootView;
    }

    public View initialiseScreen(LayoutInflater inflater, ViewGroup container) {
        String FIREBASE_MY_URL_SHOPS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHOPS;

        /* The list of shops. */
        View rootView = inflater.inflate(R.layout.fragment_shoppinglists, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view_shopping_lists);

        final Firebase rootRef = new Firebase(FIREBASE_MY_URL_SHOPS);

        mShopListAdapter = new FirebaseListAdapter<Shop>(getActivity(),
                Shop.class, R.layout.list_item_main_shop, rootRef) {
            @Override
            protected void populateView(View v, Shop model) {
                super.populateView(v, model);
                TextView shopNameView = (TextView) v.findViewById(R.id.text_view_shop_name);
                shopNameView.setText(model.getName());

                Button addItemBtn = (Button) v.findViewById(R.id.button_add_item_to_shop);
                final Shop thisShop = model;

                addItemBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment dialog = (DialogFragment) AddItemDialogFragment.newInstance(thisShop.getName());
                        dialog.show(getActivity().getFragmentManager(), "AddItemDialogFragment");
                    }
                });

                ListView itemListView = (ListView) v.findViewById(R.id.item_list_view);

                String FIREBASE_MY_URL_ITEMS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_ITEMS;
                final Firebase itemRef = new Firebase(FIREBASE_MY_URL_ITEMS);

                mItemListAdapter = new FirebaseListAdapter<Item>(getActivity(),
                        Item.class, R.layout.list_item_main_items, itemRef) {
                    @Override
                    protected void populateView(View v, Item model) {
                        super.populateView(v, model);
                        TextView itemName = (TextView) v.findViewById(R.id.text_view_item_name);
                        itemName.setText(model.getName());
                        int valCheck = this.getCount();

                        SharedPreferences.Editor editor = mPrefs.edit();

                        if (mPrefs.getInt("Items", -1) > -1) {
                            editor.remove("Items");
                            editor.commit();
                        }

                        editor.putInt("Items", valCheck);
                        editor.commit();
                        Log.w("ItemListPopView", "" + valCheck);
                    }
                };
                itemListView.setAdapter(mItemListAdapter);

                int rows = mPrefs.getInt("Items", 1);
                Log.w("ShopAdapter", "" + rows);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) itemListView.getLayoutParams();
                lp.height = 160 * rows;
                itemListView.setLayoutParams(lp);

                final View viewRef = v;

                itemRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        int rows = mPrefs.getInt("Items", 0);
                        AbsListView.LayoutParams llp = (AbsListView.LayoutParams) viewRef.getLayoutParams();
                        llp.height = 150 + 180 * rows;
                        viewRef.setLayoutParams(llp);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        int rows = mPrefs.getInt("Items", 0);
                        AbsListView.LayoutParams llp = (AbsListView.LayoutParams) viewRef.getLayoutParams();
                        llp.height = 150 + 160 * rows;
                        viewRef.setLayoutParams(llp);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        int rows = mPrefs.getInt("Items", 0);
                        AbsListView.LayoutParams llp = (AbsListView.LayoutParams) viewRef.getLayoutParams();
                        llp.height = 150 + 160 * rows;
                        viewRef.setLayoutParams(llp);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        int rows = mPrefs.getInt("Items", 0);
                        AbsListView.LayoutParams llp = (AbsListView.LayoutParams) viewRef.getLayoutParams();
                        llp.height = 150 + 160 * rows;
                        viewRef.setLayoutParams(llp);

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        };
        mListView.setAdapter(mShopListAdapter);
        Log.w("initScreenOfFrag", "here");

        return rootView;
    }

}
