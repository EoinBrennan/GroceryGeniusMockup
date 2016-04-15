/*
 * Firebase UI Bindings Android Library
 *
 * Copyright © 2015 Firebase - All Rights Reserved
 * https://www.firebase.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binaryform must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY FIREBASE AS IS AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL FIREBASE BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/* The following is a major adaptation of the FirebaseArrayAdapter from the Firebase UI Bindings
 * Android Library as mentioned in the copyright above.
 */

package com.team5.grocerygeniusmockup.Model.ShoppingListModel;

import android.app.Activity;
import android.app.DialogFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.team5.grocerygeniusmockup.Model.SortedFirebaseArray;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddSectionDialogFragment;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddShopDialogFragment;
import com.team5.grocerygeniusmockup.UI.OptionDialogs.RenameShopDialogFragment;
import com.team5.grocerygeniusmockup.Utilities.Constants;
import com.firebase.client.Firebase;

/**
 * This class is a specific way of backing an Android ExpandableListView with a GroceryGenius
 * Firebase location containing Shop items.
 *
 * It handles all of the child events at the given Firebase location. It marshals received data into
 * the Shop class type.
 */

public class FirebaseThreeLayerExpandableAdapter extends BaseExpandableListAdapter {
    private static final String LOG_TAG = FirebaseThreeLayerExpandableAdapter.class.getSimpleName();

    // Declare reused variables.
    protected int mLayout = R.layout.list_item_main_shop;
    protected Activity mActivity;

    // Declare FirebaseArrays.
    public SortedFirebaseArray mSnapshots;

    // The String for the address of the user's root node in Firebase.
    String FIREBASE_MY_NODE_URL;

    /** Constructor for outer layer of three layer ExListView.
     * @param activity The activity containing the ExpandableListView
     * @param parent A reference to the ExListView itself, to allow opening/closing of list.
     * @param FIREBASE_MY_NODE_URL the address of the user's root node in Firebase.
     */
    public FirebaseThreeLayerExpandableAdapter(Activity activity, ExpandableListView parent, String FIREBASE_MY_NODE_URL) {
        Log.d(LOG_TAG, "FirebaseThreeLayerExpandableAdapter Constructor");
        // Initialise activity, ExListView, and firebase address string.
        mActivity = activity;
        final ExpandableListView thisDad = parent;
        this.FIREBASE_MY_NODE_URL = FIREBASE_MY_NODE_URL;

        /* Firebase address for user's shops node is created, and a SortedFirebaseArray of the
         * information at that node is set up.
         */

        String FIREBASE_MY_URL_SHOPS = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHOPS;
        mSnapshots = new SortedFirebaseArray(new Firebase(FIREBASE_MY_URL_SHOPS));

        /**
         * Attaches a listener that is triggered by FirebaseArray whenever elements are
         * added, removed, moved or changed in the FirebaseArray.
         */
        mSnapshots.setOnChangedListener(new SortedFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                /**
                 * This listener triggers the BaseAdapter method notifyDataSetChanged.
                 * This notifies views reflecting the data set that they should refresh themselves.
                 */
                Log.d(LOG_TAG, "mSnapshots onChanged");
                notifyDataSetChanged();

                // We set the first shop, i.e. the most visited shop to be open automatically.
                thisDad.expandGroup(0);
            }
        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our listener and models.
        Log.d(LOG_TAG, "cleanup");
        mSnapshots.cleanup();
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group. This is just the expandable list view, there will only ever be one child per group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getChildView");
        // Fetch the shop at groupPosition, get it's key and name.
        String shopKey = getItemKey(groupPosition);
        String shopName = mSnapshots.getItem(groupPosition).getValue(Shop.class).getName();

        // Create an custom ExListView and set it equal to the sole child view of this group.
        InternalExpandableListView SecondLevelExLV = (InternalExpandableListView) convertView;
        if (SecondLevelExLV == null) {
            SecondLevelExLV = new InternalExpandableListView(mActivity);
        }

        /* Set the custom internal adapter for the ExListView by passing it relevant information
         * about it's context and the shop it's expanding on.
         */
        SecondLevelExLV.setAdapter(new FirebaseInternalExpandableAdapter(mActivity, SecondLevelExLV, shopName, shopKey, FIREBASE_MY_NODE_URL));
        SecondLevelExLV.setGroupIndicator(null);
        return SecondLevelExLV;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}. This
     * view inflates a custom-layout file containing buttons and a
     * menu controlling the relevant shop object.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getGroupView");
        // Fetch the relevant shop object for this groupPosition.
        DataSnapshot shopSS = mSnapshots.getItem(groupPosition);
        final Firebase shopRef = shopSS.getRef();
        final Shop model = shopSS.getValue(Shop.class);
        final String thisShopKey = shopSS.getKey();

        // Call out to subclass to marshall this model into the provided view
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(mLayout, parent, false);
        }

        /* Initialise the groupView's layout UI */
        TextView shopNameView = (TextView) convertView.findViewById(R.id.text_view_shop_name);
        shopNameView.setText(model.getName());

        /* An image button that creates a dialog fragment that lets the user add a shop to this
         * dialog.
         */
        ImageButton addSecBtn = (ImageButton) convertView.findViewById(R.id.button_add_section_to_shop);
        addSecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "addSecBtn (" + model.getName() + ") onClick");
                DialogFragment dialog = (DialogFragment) AddSectionDialogFragment.newInstance(model.getName(), thisShopKey);
                dialog.show(mActivity.getFragmentManager(), "AddSectionDialogFragment");
            }
        });

        // Custom menu button for the shop.
        final ImageButton shopMenuBtn = (ImageButton) convertView.findViewById(R.id.shop_options_button);
        shopMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "shopMenuBtn (" + model.getName() + ") onClick");
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mActivity, shopMenuBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_shop_options, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.visit_more_often:
                                /* If the shop isn't the most frequently visited option,
                                 * replace the shop with a new shop one step more frequently
                                 * visited.
                                 */
                                Log.d(LOG_TAG, "visit_more_often (" + model.getName() + ") onClick");
                                if (model.getFrequency() > 0) {
                                    Shop newShop = new Shop(model.getName(), model.getFrequency() - 1);
                                    shopRef.setValue(newShop);
                                }
                                break;
                            case R.id.visit_less_shop:
                                /* If the shop isn't the least frequently visited option,
                                 * replace the shop with a new shop one step less frequently
                                 * visited.
                                 */
                                Log.d(LOG_TAG, "visit_less_often (" + model.getName() + ") onClick");
                                if (model.getFrequency() < 6) {
                                    Shop newShop = new Shop(model.getName(), model.getFrequency() + 1);
                                    shopRef.setValue(newShop);
                                }
                                break;
                            case R.id.option_delete_shop:
                                /* If the user wishes to delete the shop, it and all of it's related
                                 * sections and items are removed from Firebase.
                                 */
                                Log.d(LOG_TAG, "delete_shop (" + model.getName() + ") onClick");
                                shopRef.removeValue();
                                Firebase secRef = new Firebase(FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + thisShopKey);
                                secRef.removeValue();
                                Firebase itemRef = new Firebase(FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_ITEMS + "/" + thisShopKey);
                                itemRef.removeValue();
                                break;
                            case R.id.rename_shop:
                                /* Create a dialog to collect a new name for the shop, which then
                                 * replaces the current shop at this location in Firebase.
                                 */
                                Log.d(LOG_TAG, "rename_shop (" + model.getName() + ") onClick");
                                DialogFragment rename_dialog = (DialogFragment) RenameShopDialogFragment.newInstance(shopRef.toString(), model.getFrequency());
                                rename_dialog.show(mActivity.getFragmentManager(), "RenameShopDialogFragment");
                                break;
                            case R.id.add_different_shop:
                                Log.d(LOG_TAG, "add_shop (" + model.getName() + ") onClick");
                                DialogFragment add_dialog = (DialogFragment) AddShopDialogFragment.newInstance();
                                add_dialog.show(mActivity.getFragmentManager(), "AddShopDialogFragment");
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
        return convertView;
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */

    @Override
    public int getChildrenCount(int groupPosition) {
        // This returns 1 because each child is in fact an expandable list view of it's own.
        Log.d(LOG_TAG, "getChildrenCount");
        return 1;
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */

    @Override
    public int getGroupCount() {
        Log.d(LOG_TAG, "getGroupCount");
        return mSnapshots.getCount();
    }

    /**
     * Gets the data associated with the given child within the given group.
     * This is not currently implemented.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition < mSnapshots.getCount()) {
            return mSnapshots.getItem(groupPosition).getValue(Shop.class);
        } else {
            return null;
        }
    }

    /**
     * Gets the key associated with the Firebase represenatation of the group item (shop).
     *
     * @param groupPosition the position of the group
     * @return the Firebase key for the specified group
     */

    public String getItemKey(int groupPosition) {
        Log.d(LOG_TAG, "getItemKey");
        if (groupPosition < mSnapshots.getCount()) {
            return mSnapshots.getItem(groupPosition).getKey();
        } else {
            return "";
        }
    }

    /**
     * Gets the Firebase reference associated with the group item (shop).
     *
     * @param groupPosition the position of the group
     * @return the Firebase reference for the specified group
     */

    public Firebase getRef(int groupPosition) {
        Log.d(LOG_TAG, "getRef");
        if (groupPosition < mSnapshots.getCount()) {
            return mSnapshots.getItem(groupPosition).getRef();
        } else {
            return null;
        }
    }

    /* The relevance of group and child IDs is explained here/
     * http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
     * However given the key value nature of this data, they are more or less unique in their
     * positions, and these methods have not been expanded on.
     */

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */

    @Override
    public long getGroupId(int groupPosition) {
        return mSnapshots.getItem(groupPosition).getKey().hashCode();
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     * @see BaseExpandableListAdapter hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
