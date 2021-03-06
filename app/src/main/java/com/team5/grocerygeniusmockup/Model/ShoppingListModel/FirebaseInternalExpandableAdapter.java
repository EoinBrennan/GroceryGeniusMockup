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

package com.team5.grocerygeniusmockup.Model.ShoppingListModel;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.team5.grocerygeniusmockup.Model.SortedFirebaseArray;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddItemDialogFragment;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.MoveToPantryDialogFragment;
import com.team5.grocerygeniusmockup.UI.OptionDialogs.RenameSectionDialogFragment;
import com.team5.grocerygeniusmockup.Utilities.Constants;
import com.firebase.client.Firebase;

import org.w3c.dom.Text;

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
public class FirebaseInternalExpandableAdapter extends BaseExpandableListAdapter {

    protected int secLayout;
    protected int itemLayout;
    protected Activity mActivity;
    public SortedFirebaseArray mSectionSnapshots;
    public ArrayList<SortedFirebaseArray> mItemSnapshots = new ArrayList<SortedFirebaseArray>();
    public String shopKey;
    public String shopName;
    SharedPreferences mPrefs;
    String listKey;
    InternalExpandableListView parent;

    /**
     * @param activity The activity containing the ListView
     */
    public FirebaseInternalExpandableAdapter(Activity activity, InternalExpandableListView parent, String shopName, String shopKey, String listKey) {

        this.listKey = listKey;
        String MY_FIREBASE_SECTIONS = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + listKey  + "/" + shopKey;

        Log.e("ShopKey", shopKey);

        this.parent = parent;
        final InternalExpandableListView thisMom = parent;

        this.shopKey = shopKey;
        this.shopName = shopName;
        mActivity = activity;
        mSectionSnapshots = new SortedFirebaseArray(new Firebase(MY_FIREBASE_SECTIONS));

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        /**
         * Attaches a listener that is triggered by FirebaseArray whenever elements are
         * added, removed, moved or changed in the FirebaseArray.
         */

        mSectionSnapshots.setOnChangedListener(new SortedFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                Log.e("SecSnapsChangeListener", "Index changed: " + index);

                if (type == EventType.Added) {
                    mItemSnapshots.add(index, null);
                }

                /**
                 * This listener triggers the BaseAdapter method notifyDataSetChanged.
                 * This notifies views reflecting the data set that they should refresh themselves.
                 */
                Log.e("SecSnapsChangeListener", "size of SSnaps" + mSectionSnapshots.getCount());
                for (int i = 0; i < mSectionSnapshots.getCount(); i++) {
                    Log.e("SecSnapsChangeListerner", i + "th element in mSSnaps is: " + mSectionSnapshots.getItem(i).getValue(Section.class).getName() + " & key is: " + mSectionSnapshots.getItem(i).getKey());
                }

                notifyDataSetChanged();

                if (type == EventType.Added) {
                    generateItems(true);
                } else {
                    generateItems(false);
                }

                for (int i = 0; i < mSectionSnapshots.getCount(); i++) {
                    thisMom.expandGroup(i);
                }
            }
        });
    }

    public void generateItems(boolean added) {
        Log.e("GenItems", "SecSnaps size: " + mSectionSnapshots.getCount());

        for (int i = 0; i < mSectionSnapshots.getCount(); i++) {
            String sectionKey = mSectionSnapshots.getItem(i).getKey();
            Log.e("GenItems", "Current section Key: " + sectionKey);

            String FIREBASE_THIS_SECTIONS_ITEMS = Constants.FIREBASE_URL + "/" +
                    Constants.FIREBASE_NODENAME_ITEMS + "/" + listKey + "/" + shopKey + "/" + sectionKey;
            Log.e("GenItems", "URL for items in this section: " + FIREBASE_THIS_SECTIONS_ITEMS);

            final SortedFirebaseArray thisItem = new SortedFirebaseArray(new Firebase(FIREBASE_THIS_SECTIONS_ITEMS));
            Log.e("GenItems", "SortedFirebaseArray reference: " + thisItem.toString());

            final int z = i;
            Log.e("GenItems", "Which section in this shop?: " + (z + 1) + "th");

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
                                    /*for (int i = 0; i < z; i++) {
                                        try {
                                            mItemSnapshots.get(i);
                                        } catch (IndexOutOfBoundsException e2) {
                                            mItemSnapshots.add(i, null);
                                        }
                                    }*/
                                }
                            }

                            try {
                                Log.e("ItemInSectionListener", "This item's name: " + mItemSnapshots.get(z).getItem(0).getValue(Item.class).getName());
                                Log.e("ItemInSectionListener", "How many sections have item listeners:" + mItemSnapshots.size());
                                for (int j = 0; j < mItemSnapshots.size(); j++) {
                                    if (mItemSnapshots.get(j) != null) {
                                        Log.e("ItemInSectionListener", "How many items in the " + j + "th section: " + mItemSnapshots.get(j).getCount());
                                        for (int k = 0; k < mItemSnapshots.get(j).getCount(); k++) {
                                            Log.e("ItemInSectionListener", k + "th item in the " + j + "th section: " + mItemSnapshots.get(j).getItem(k).getValue(Item.class).getName());
                                        }
                                    }
                                }
                                notifyDataSetChanged();
                            } catch (IndexOutOfBoundsException e) {
                                Log.e("ItemInSectionListener", "This item doesn't exist yet.");
                            }
                        }
                    }
            );
        }

        /* Open the sections in this shop if items are added. */

        if (added) {
            for (int i = 0; i < mSectionSnapshots.getCount(); i++) {
                parent.expandGroup(i);
            }
        }
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mSectionSnapshots.cleanup();
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
        Log.e("getGroupCount", "" + mSectionSnapshots.getCount());
        return mSectionSnapshots.getCount();
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
        return mSectionSnapshots.getItem(groupPosition).getValue(Section.class);
    }

    public String getSectionKey(int groupPosition) {
        return mSectionSnapshots.getItem(groupPosition).getKey();
    }

    public Firebase getRef(int groupPosition) {
        return mSectionSnapshots.getItem(groupPosition).getRef();
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
        return groupPosition;
    }

    /*
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
        }

        T model = mSnapshots.getItem(position).getValue(mModelClass);
        // Call out to subclass to marshall this model into the provided view

        populateView(view, model, position);

        return view;
    } */

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

            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_main_items, parent, false);
            final View thisView = convertView;
            final ViewGroup thisParent = parent;

            TextView itemNameView = (TextView) convertView.findViewById(R.id.text_view_item_name);
            itemNameView.setText(model.getName());

            final TextView itemQuantityView = (TextView) convertView.findViewById(R.id.text_view_item_quantity);
            itemQuantityView.setText("" + model.getQuantity());

            ImageButton increaseQBtn = (ImageButton) convertView.findViewById(R.id.shop_quantity_up_button);
            increaseQBtn.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when the imagebutton for increasing quantity has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    try {
                        DataSnapshot thisGuy = mItemSnapshots.get(groupPosition).getItem(childPosition);
                        Firebase thisItemRef = thisGuy.getRef();
                        Item currentItem = thisGuy.getValue(Item.class);
                        Item newItem = new Item(currentItem.getName(), currentItem.getShop(), currentItem.getSection(), currentItem.getQuantity() + 1);
                        thisItemRef.setValue(newItem);
                        itemQuantityView.setText("" + newItem.getQuantity());
                        Log.i("IncQuantityBtnClicked", currentItem.getName() + " had its quantity increased by one");
                        generateItems(false);
                    } catch (IndexOutOfBoundsException e) {

                    }
                }
            });

            ImageButton decreaseQBtn = (ImageButton) convertView.findViewById(R.id.shop_quantity_down_button);
            decreaseQBtn.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when the imagebutton for increasing quantity has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    try {
                        DataSnapshot thisGuy = mItemSnapshots.get(groupPosition).getItem(childPosition);
                        Firebase thisItemRef = thisGuy.getRef();
                        Item currentItem = thisGuy.getValue(Item.class);
                        if (currentItem.getQuantity() > 1 && Integer.parseInt(itemQuantityView.getText().toString()) == currentItem.getQuantity()) {
                            Item newItem = new Item(currentItem.getName(), currentItem.getShop(), currentItem.getSection(), currentItem.getQuantity() - 1);
                            thisItemRef.setValue(newItem);
                            itemQuantityView.setText("" + newItem.getQuantity());
                            Log.i("DecQuantityBtnClicked", currentItem.getName() + " had its quantity decreased by one");
                            generateItems(false);
                        }
                    } catch (IndexOutOfBoundsException e) {

                    }
                }
            });

            ImageButton rmvItemBtn = (ImageButton) convertView.findViewById(R.id.remove_item_button);
            final InternalExpandableListView thisMom = this.parent;
            final boolean[] pressed = {false};
            rmvItemBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Boolean deleteItemCheck = mPrefs.getBoolean("delete_checked", false);
                    Boolean pantryItemCheck = mPrefs.getBoolean("move_to_pantry_checked", false);

                    if (deleteItemCheck == true || (deleteItemCheck == false && pantryItemCheck == false)) {
                        Log.e("RemoveItemBtnClicked", "Button pressed before?: " + pressed[0]);
                        try {
                            if (!pressed[0]) {
                                Log.e("RemoveItemBtnClicked", "Removing item " + childPosition + " from group " + groupPosition);
                                mItemSnapshots.get(groupPosition).getItem(childPosition).getRef().removeValue();

                                pressed[0] = true;
                                if (getChildrenCount(groupPosition) < 2) {
                                    Log.e("RemoveItemBtnClicked", "Closing group " + childPosition);
                                    thisMom.collapseGroup(groupPosition);
                                }
                                generateItems(false);
                            }
                        } catch (IndexOutOfBoundsException e) {

                        }
                    } else if (pantryItemCheck == true) {
                        Log.e("MoveItemBtnClicked", "Button pressed before?: " + pressed[0]);
                        try {
                            if (!pressed[0]) {
                                Log.e("MoveItemBtnClicked", "Moving item " + childPosition + " from group " + groupPosition + " to pantry.");
                                DataSnapshot thisItemSS = mItemSnapshots.get(groupPosition).getItem(childPosition);
                                Item thisItem = thisItemSS.getValue(Item.class);

                                Log.e("The Item Ref", thisItemSS.getRef().toString());

                                DialogFragment move_to_pantry_dialog = (DialogFragment) MoveToPantryDialogFragment.newInstance(thisItem.getName(), thisItem.getShop(), thisItem.getSection(), thisItem.getQuantity(), thisItemSS.getRef().toString());
                                move_to_pantry_dialog.show(mActivity.getFragmentManager(), "MoveToPantryDialogFragment");

                                pressed[0] = true;
                                if (getChildrenCount(groupPosition) < 2) {
                                    Log.e("MoveItemBtnClicked", "Closing group " + childPosition);
                                    thisMom.collapseGroup(groupPosition);
                                }
                                generateItems(false);
                            }
                        } catch (IndexOutOfBoundsException e) {

                        }
                    }
                }
            });
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
        final Section model = mSectionSnapshots.getItem(groupPosition).getValue(Section.class);

        // Call out to subclass to marshall this model into the provided view

        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_main_section, parent, false);
        }

        TextView itemNameView = (TextView) convertView.findViewById(R.id.text_view_section_name);
        itemNameView.setText(model.getName());

        final ImageButton secMenuBtn = (ImageButton) convertView.findViewById(R.id.section_options_button);

        final boolean[] removed = {false};

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

                        Section thisSection = mSectionSnapshots.getItem(groupPosition).getValue(Section.class);
                        Firebase thisSecRef = mSectionSnapshots.getItem(groupPosition).getRef();
                        int order = thisSection.getOrder();

                        switch (id) {
                            case R.id.visit_earlier:
                                if (order > 1) {
                                    Section newSec = new Section(thisSection.getName(), thisSection.getShop(), order - 1);
                                    thisSecRef.setValue(newSec);
                                    generateItems(false);
                                }
                                break;
                            case R.id.visit_later:
                                if (order > 0) {
                                    Section newSec = new Section(thisSection.getName(), thisSection.getShop(), order + 1);
                                    thisSecRef.setValue(newSec);
                                    generateItems(false);
                                }
                                break;
                            case R.id.option_delete_section:
                                if (!removed[0] && order != 0) {
                                    String thisSecKey = mSectionSnapshots.getItem(groupPosition).getKey();
                                    mSectionSnapshots.getItem(groupPosition).getRef().removeValue();
                                    Firebase itemRef = new Firebase(Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_ITEMS + "/" + listKey + "/" + shopKey + "/" + thisSecKey);
                                    itemRef.removeValue();
                                    mItemSnapshots.remove(groupPosition);
                                    removed[0] = true;
                                } else if (order == 0) {
                                    Toast.makeText(mActivity, "You cannot delete the default Section.", Toast.LENGTH_SHORT).show();
                                }
                                generateItems(false);
                                break;
                            case R.id.rename_section:
                                DialogFragment rename_sec_dialog = (DialogFragment) RenameSectionDialogFragment.newInstance(thisSecRef.toString(), thisSection.getShop(), order);
                                rename_sec_dialog.show(mActivity.getFragmentManager(), "RenameSectionDialogFragment");
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

        ImageButton addItemBtn = (ImageButton) convertView.findViewById(R.id.button_add_item_to_section);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment dialog = (DialogFragment) AddItemDialogFragment.newInstance(shopName, shopKey, model.getName(), mSectionSnapshots.getItem(groupPosition).getKey());
                dialog.show(mActivity.getFragmentManager(), "AddItemDialogFragment");
            }
        });

        return convertView;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p/>
     * Your implementation should populate the view using the data contained in the model.
     * You should implement either this method or the other  populateView(View, Object)} method
     * but not both.
     *
     * @param v        The view to populate
     * @param model    The object containing the data used to populate the view
     * @param position The position of the object populating the view
     */
    protected void populateView(View v, Shop model, int position) {
        populateView(v, model);
    }

    /**
     * This is a backwards compatible version of populateView.
     * <p/>
     * You should implement either this method or the other populateView(View, Object, int)} method
     * but not both.
     *
     * @param v     The view to populate
     * @param model The object containing the data used to populate the view
     *              populateView(View, Object, int)
     */
    protected void populateView(View v, Shop model) {

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
