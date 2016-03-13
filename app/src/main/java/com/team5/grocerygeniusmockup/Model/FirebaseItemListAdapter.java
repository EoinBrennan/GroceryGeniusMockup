package com.team5.grocerygeniusmockup.Model;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Query;

/**
 * Created by user on 13/03/2016.
 */
public class FirebaseItemListAdapter<T> extends FirebaseListAdapter<T> {
    public FirebaseItemListAdapter(Activity activity, Class<T> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
        }

        String key = mSnapshots.getItem(position).getKey();

        T model = mSnapshots.getItem(position).getValue(mModelClass);
        // Call out to subclass to marshall this model into the provided view

        populateView(view, model, key);

        return view;
    }

    protected void populateView(View v, T model, String key) {

    }
}
