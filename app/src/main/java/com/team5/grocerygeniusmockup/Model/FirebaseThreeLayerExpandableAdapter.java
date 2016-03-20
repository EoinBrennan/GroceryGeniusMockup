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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;
import com.firebase.client.Firebase;

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
 *
 */
public class FirebaseThreeLayerExpandableAdapter extends BaseExpandableListAdapter {

    protected int mLayout;
    protected Activity mActivity;
    public SortedFirebaseArray mSnapshots;
    String FIREBASE_MY_NODE_URL;

    /**
     * @param activity    The activity containing the ListView
     */
    public FirebaseThreeLayerExpandableAdapter(Activity activity, ExpandableListView parent, String FIREBASE_MY_NODE_URL) {

        mLayout = R.layout.list_item_main_shop;
        mActivity = activity;
        final ExpandableListView thisDad = parent;

        this.FIREBASE_MY_NODE_URL = FIREBASE_MY_NODE_URL;
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
                notifyDataSetChanged();

                for (int i = 0; i < mSnapshots.getCount(); i++) {
                    thisDad.expandGroup(i);
                }
            }
        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mSnapshots.cleanup();
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
        return 1;//mTitles[groupPosition].length;
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */

    @Override
    public int getGroupCount() {
        return mSnapshots.getCount();
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
        /*String shopKey = getItemKey(groupPosition);

        String MY_FIREBASE_SHOP_SECTIONS = Constants.FIREBASE_URL_MINE + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + shopKey;

        SortedFirebaseArray mSectionSnapshots = new SortedFirebaseArray(new Firebase(MY_FIREBASE_SHOP_SECTIONS));

        mSectionSnapshots.setOnChangedListener(new SortedFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                /**
                 * This listener triggers the BaseAdapter method notifyDataSetChanged.
                 * This notifies views reflecting the data set that they should refresh themselves.


                notifyDataSetChanged();
            }
        });

        return mSectionSnapshots.getItem(childPosition).getValue(Section.class); */

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
        return mSnapshots.getItem(groupPosition).getValue(Shop.class);
    }

    public String getItemKey(int groupPosition) {
        return mSnapshots.getItem(groupPosition).getKey();
    }

    public Firebase getRef(int groupPosition) {
        return mSnapshots.getItem(groupPosition).getRef();
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
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */

    @Override
    public long getGroupId(int groupPosition) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(groupPosition).getKey().hashCode();
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String shopKey = getItemKey(groupPosition);
        String shopName = mSnapshots.getItem(groupPosition).getValue(Shop.class).getName();

        InternalExpandableListView SecondLevelExLV = (InternalExpandableListView) convertView;

        if(SecondLevelExLV == null) {
            SecondLevelExLV = new InternalExpandableListView(mActivity);
        }

        SecondLevelExLV.setAdapter(new FirebaseInternalExpandableAdapter(mActivity, SecondLevelExLV, shopName, shopKey, FIREBASE_MY_NODE_URL));

        /* Expands all inner items.
        for (int i = 0; i < sections; i++) {
            SecondLevelExLV.expandGroup(i);
        } */

        SecondLevelExLV.setGroupIndicator(null);
        return SecondLevelExLV;
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
        /* if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(mLayout, parent, false);
        } */

        Shop model = mSnapshots.getItem(groupPosition).getValue(Shop.class);

        // Call out to subclass to marshall this model into the provided view

        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(mLayout, parent, false);
        }

        TextView shopNameView = (TextView) convertView.findViewById(R.id.text_view_shop_name);
        shopNameView.setText(model.getName());

       /* Button addSecBtn = (Button) convertView.findViewById(R.id.button_add_section_to_shop);

        final Shop thisShop = model;

        addSecBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                        //DialogFragment dialog = (DialogFragment) AddItemDialogFragment.newInstance(thisShop.getName(), shopKey);
                        //dialog.show(getActivity().getFragmentManager(), "AddItemDialogFragment");
            }
        });*/

        return convertView;

        //populateView(convertView, model, groupPosition);

        //return convertView;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p/>
     * Your implementation should populate the view using the data contained in the model.
     * You should implement either this method or the other populateView(View, Object)} method
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
     * You should implement either this method or the other  populateView(View, Object, int)} method
     * but not both.
     *
     * @param v     The view to populate
     * @param model The object containing the data used to populate the view
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
