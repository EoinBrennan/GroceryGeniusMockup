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

package com.team5.grocerygeniusmockup.Model.ListModel;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.team5.grocerygeniusmockup.Model.SortedFirebaseArray;

/**
 * This class is a generic way of backing an Android ListView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type. Extend this class and provide an implementation of <code>populateView</code>, which will be given an
 * instance of your list item mLayout and an instance your class that holds your data. Simply populate the view however
 * you like and this class will handle updating the list as the data changes.
 *
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

public abstract class FirebaseListListAdapter extends BaseAdapter {

    protected int mLayout;
    protected Activity mActivity;
    SortedFirebaseArray mSnapshots;


    /**
     * @param activity    The activity containing the ListView
     * @param modelLayout This is the layout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     */
    public FirebaseListListAdapter(Activity activity, int modelLayout, Query ref) {
        mLayout = modelLayout;
        mActivity = activity;
        mSnapshots = new SortedFirebaseArray(ref);
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
            }
        });
    }
    /**
     * @param activity    The activity containing the ListView
     * @param modelLayout This is the layout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     */
    public FirebaseListListAdapter(Activity activity, int modelLayout, Firebase ref) {
        this(activity, modelLayout, (Query) ref);
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mSnapshots.cleanup();
    }

    @Override
    public int getCount() {
        return mSnapshots.getCount();
    }

    @Override
    public List getItem(int i) {
        try {
            return mSnapshots.getItem(i).getValue(List.class);
        } catch (FirebaseException e) {
            Log.e("FirebaseListListAdapter", "Error:" + e.toString());
        }
        return null;
    }


    public Firebase getRef(int position) { return mSnapshots.getItem(position).getRef(); }

    @Override
    public long getItemId(int i) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        try {
            return mSnapshots.getItem(i).getKey().hashCode();
        } catch (FirebaseException e) {
            Log.e("FirebaseListListAdapter", "Error:" + e.toString());
        }
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
        }

        String key = "";
        List model = null;

        try {
            model = mSnapshots.getItem(position).getValue(List.class);
            key = mSnapshots.getItem(position).getKey();
        } catch (FirebaseException e) {
            Log.e("FirebaseListListAdapter", "Error: " + e.toString());
        }

        // Call out to subclass to marshall this model into the provided view
        populateView(view, model, key, position);
        return view;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param v         The view to populate
     * @param model     The object containing the data used to populate the view
     * @param position  The position in the list of the view being populated
     */
    protected void populateView(View v, List model, String key, int position) {
        populateView(v, model, key);
    }

    /**
     * This is a backwards compatible version of populateView.
     * <p>
     *
     * @param v         The view to populate
     * @param model     The object containing the data used to populate the view
     */
    protected void populateView(View v, List model, String key) {

    }
}
