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

package com.team5.grocerygeniusmockup.Model;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.InternalExpandableListView;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddItemDialogFragment;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;

/**
 * This class is a generic way of backing an Android ListView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type. Extend this class and provide an implementation of <code>populateView</code>, which will be given an
 * instance of your list item mLayout and an instance your class that holds your data. Simply populate the view however
 * you like and this class will handle updating the list as the data changes.
 * <p/>
 * <blockquote><pre>
 * {@code
 *     Firebase ref = new Firebase("https://<yourapp>.firebaseio.com");
 *     ListAdapter adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, android.R.layout.two_line_list_item, mRef)
 *     {
 *         protected void populateView(View view, ChatMessage chatMessage)
 *         {
 *             ((TextView)view.findViewById(android.R.id.text1)).setText(chatMessage.getName());
 *             ((TextView)view.findViewById(android.R.id.text2)).setText(chatMessage.getMessage());
 *         }
 *     };
 *     listView.setListAdapter(adapter);
 * }
 * </pre></blockquote>
 */
public class FirebaseTwoLayerExpandableAdapter extends BaseExpandableListAdapter {

    protected int secLayout;
    protected int itemLayout;
    protected Activity mActivity;
    public SortedFirebaseArray mShelfSnapshots;
    public ArrayList<SortedFirebaseArray> mItemSnapshots = new ArrayList<SortedFirebaseArray>();
    public String shopKey;
    public String shopName;
    SharedPreferences mPrefs;
    String FIREBASE_MY_NODE_URL;
    InternalExpandableListView parent;

    protected int shelfLayout;

    /**
     * @param activity The activity containing the ListView
     */
    public FirebaseTwoLayerExpandableAdapter(Activity activity, ExpandableListView parent, String FIREBASE_MY_NODE_URL) {

        shelfLayout = R.layout.pantry_shelf;
        itemLayout = R.layout.list_item_main_items;
        mActivity = activity;
        final ExpandableListView thisDad = parent;

        this.FIREBASE_MY_NODE_URL = FIREBASE_MY_NODE_URL;
        String MY_FIREBASE_SHELVES = FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_SHELVES;

        mActivity = activity;
        mShelfSnapshots = new SortedFirebaseArray(new Firebase(MY_FIREBASE_SHELVES));

        /**
         * Attaches a listener that is triggered by FirebaseArray whenever elements are
         * added, removed, moved or changed in the FirebaseArray.
         */

        mShelfSnapshots.setOnChangedListener(new SortedFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                Log.e("ShelfSnapChangeListener", "Index changed: " + index);

                if (type == EventType.Added) {
                    mItemSnapshots.add(index, null);
                }

                /**
                 * This listener triggers the BaseAdapter method notifyDataSetChanged.
                 * This notifies views reflecting the data set that they should refresh themselves.
                 */
                Log.e("ShelfSnapChangeListener", "size of SSnaps" + mShelfSnapshots.getCount());
                for (int i = 0; i < mShelfSnapshots.getCount(); i++) {
                    Log.e("ShelfSnapChangeListener", i + "th element in mSSnaps is: " + mShelfSnapshots.getItem(i).getValue(Section.class).getName() + " & key is: " + mShelfSnapshots.getItem(i).getKey());
                }

                //notifyDataSetChanged();

                generateItems();

                for (int i = 0; i < mShelfSnapshots.getCount(); i++) {
                    thisDad.expandGroup(i);
                }
            }
        });
    }

    public void generateItems() {
        Log.e("GenItems", "ShelfSnaps size: " + mShelfSnapshots.getCount());

        for (int i = 0; i < mShelfSnapshots.getCount(); i++) {
            String shelfKey = mShelfSnapshots.getItem(i).getKey();
            Log.e("GenItems", "Current shelf Key: " + shelfKey);

            String FIREBASE_THIS_SHELVES_ITEMS = FIREBASE_MY_NODE_URL + "/" +
                    Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + shelfKey;
            Log.e("GenItems", "URL for items in this shelf: " + FIREBASE_THIS_SHELVES_ITEMS);

            final SortedFirebaseArray thisItem = new SortedFirebaseArray(new Firebase(FIREBASE_THIS_SHELVES_ITEMS));
            Log.e("GenItems", "SortedFirebaseArray reference: " + thisItem.toString());

            final int z = i;
            Log.e("GenItems", "Which shelf?: " + (z + 1) + "th");

            thisItem.setOnChangedListener(
                    new SortedFirebaseArray.OnChangedListener() {
                        @Override
                        public void onChanged(EventType type, int index, int oldIndex) {

                            try {
                                mItemSnapshots.set(z, thisItem);
                            } catch (IndexOutOfBoundsException e) {
                                try {
                                    mItemSnapshots.add(z, thisItem);
                                } catch (IndexOutOfBoundsException e1) {
                                }
                            }

                            try {
                                Log.e("ItemInShelfListener", "This item's name: " + mItemSnapshots.get(z).getItem(0).getValue(Item.class).getName());
                                Log.e("ItemInShelfListener", "How many shelves have item listeners:" + mItemSnapshots.size());
                                for (int j = 0; j < mItemSnapshots.size(); j++) {
                                    if (mItemSnapshots.get(j) != null) {
                                        Log.e("ItemInShelfListener", "How many items in the " + j + "th shelf: " + mItemSnapshots.get(j).getCount());
                                        for (int k = 0; k < mItemSnapshots.get(j).getCount(); k++) {
                                            Log.e("ItemInShelfListener", k + "th item in the " + j + "th shelf: " + mItemSnapshots.get(j).getItem(k).getValue(Item.class).getName());
                                        }
                                    }
                                }
                                notifyDataSetChanged();
                            } catch (IndexOutOfBoundsException e) {
                                Log.e("ItemInShelfListener", "This item doesn't exist yet.");
                            }
                        }
                    }
            );
        }
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mShelfSnapshots.cleanup();
        for (int i = 0; i < mItemSnapshots.size(); i++) {
            mItemSnapshots.get(i).cleanup();
        }
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
        Log.e("getChildrenCount", "group position: " + groupPosition);
        if (mItemSnapshots.size() != 0 && mItemSnapshots.get(groupPosition) != null && groupPosition < mItemSnapshots.size()) {
            Log.e("ChildrenCount", "value: " + mItemSnapshots.get(groupPosition).getCount());
            return mItemSnapshots.get(groupPosition).getCount();
        } else {
            Log.e("getChildrenCount", "empty Item Snap");
            return 0;
        }
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */

    @Override
    public int getGroupCount() {
        Log.e("getGroupCount", "" + mShelfSnapshots.getCount());
        return mShelfSnapshots.getCount();
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItemSnapshots.get(groupPosition).getItem(childPosition).getValue(Item.class);
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */

    @Override
    public Object getGroup(int groupPosition) {
        return mShelfSnapshots.getItem(groupPosition).getValue(Section.class);
    }

    public String getShelfKey(int groupPosition) {
        return mShelfSnapshots.getItem(groupPosition).getKey();
    }

    public Firebase getShelfRef(int groupPosition) {
        return mShelfSnapshots.getItem(groupPosition).getRef();
    }

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
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mShelfSnapshots.getItem(groupPosition).getKey().hashCode();
    }


    /**
     * Gets a View that displays the data for the given child within the given
     * group.
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
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, ViewGroup parent) {
        if (mItemSnapshots.get(groupPosition) != null && mItemSnapshots.get(groupPosition).getCount() != 0) {

            Item model = mItemSnapshots.get(groupPosition).getItem(childPosition).getValue(Item.class);

            // Call out to subclass to marshall this model into the provided view

            convertView = mActivity.getLayoutInflater().inflate(itemLayout, parent, false);
            final View thisView = convertView;
            final ViewGroup thisParent = parent;

            TextView itemNameView = (TextView) convertView.findViewById(R.id.text_view_item_name);
            itemNameView.setText(model.getName());
            /*
            ImageButton rmvItemBtn = (ImageButton) convertView.findViewById(R.id.remove_item_button);
            final InternalExpandableListView thisMom = this.parent;
            final boolean[] pressed = {false};
            rmvItemBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e("RemoveItemBtnClicked","Button pressed before?: " + pressed[0]);
                    try {
                        if (!pressed[0]) {
                            Log.e("RemoveItemBtnClicked","Removing item " + childPosition + " from group " + groupPosition);
                            mItemSnapshots.get(groupPosition).getItem(childPosition).getRef().removeValue();
                            pressed[0] = true;
                            if (getChildrenCount(groupPosition) < 2) {
                                Log.e("RemoveItemBtnClicked", "Closing group " + childPosition);
                                thisMom.collapseGroup(groupPosition);
                            }
                            generateItems();
                        }
                    } catch (IndexOutOfBoundsException e) {

                    }
                }
            });*/
            return convertView;
        } else {
            TextView row;
            try {
                row = (TextView) convertView;
            } catch (ClassCastException e) {
                convertView = null;
                row = (TextView) convertView;
            }

            if (row == null) {
                row = new TextView(mActivity);
            }

            if (mItemSnapshots.get(groupPosition) != null && mItemSnapshots.size() != 0) {
                row.setText(mItemSnapshots.get(groupPosition).getItem(childPosition).getValue(Item.class).getName());
            } else {
                row.setText("Loading");
            }
            row.setPadding(15, 5, 5, 5);
            row.setBackgroundColor(Color.YELLOW);
            row.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.FILL_PARENT));
            return row;
        }
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
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
        final Shelf model = mShelfSnapshots.getItem(groupPosition).getValue(Shelf.class);

        // Call out to subclass to marshall this model into the provided view

        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(shelfLayout, parent, false);
        }

        TextView itemNameView = (TextView) convertView.findViewById(R.id.text_view_shelf_name);
        itemNameView.setText(model.getName());
        /*
        final Button secMenuBtn = (Button) convertView.findViewById(R.id.section_options_button);

        secMenuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mActivity, secMenuBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_section_options, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.option_delete_section:
                                String thisSecKey = mSectionSnapshots.getItem(groupPosition).getKey();
                                mSectionSnapshots.getItem(groupPosition).getRef().removeValue();
                                Firebase itemRef = new Firebase(FIREBASE_MY_NODE_URL + "/" + Constants.FIREBASE_NODENAME_ITEMS + "/" + shopKey + "/" + thisSecKey);
                                itemRef.removeValue();
                                mItemSnapshots.remove(groupPosition);
                                generateItems();
                                break;
                            default:
                                Toast.makeText(mActivity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method

        Button addItemBtn = (Button) convertView.findViewById(R.id.button_add_item_to_section);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment dialog = (DialogFragment) AddItemDialogFragment.newInstance(shopName, shopKey, model.getName(), mSectionSnapshots.getItem(groupPosition).getKey());
                dialog.show(mActivity.getFragmentManager(), "AddItemDialogFragment");
            }
        });
        */
        return convertView;
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
