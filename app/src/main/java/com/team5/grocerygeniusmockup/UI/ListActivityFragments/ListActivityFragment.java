package com.team5.grocerygeniusmockup.UI.ListActivityFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.team5.grocerygeniusmockup.Model.ListModel.FirebaseListListAdapter;
import com.team5.grocerygeniusmockup.Model.ListModel.List;
import com.team5.grocerygeniusmockup.Model.SortedFirebaseArray;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.MainActivity;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment {
    private static final String LOG_TAG = ListActivityFragment.class.getSimpleName();

    ListView mListView;
    FirebaseListListAdapter mListAdapter;

    String userEmail;
    String userID;

    SharedPreferences mPrefs;

    public ListActivityFragment() {
    }

    /**
     * Create fragment and pass bundle with data.
     */
    public static ListActivityFragment newInstance() {
        ListActivityFragment fragment = new ListActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialize instance variables with data from bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        if (getArguments() != null) {
        }
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userEmail = mPrefs.getString(Constants.USER_EMAIL, "");
        userID = mPrefs.getString(Constants.USER_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        initialiseScreen(rootView);

        Firebase listsRef = new Firebase(Constants.FIREBASE_URL_LISTS + "/" + userEmail);

        mListAdapter = new FirebaseListListAdapter(getActivity(), R.layout.list_list, listsRef) {
            @Override
            protected void populateView(View v, List model, String key) {
                super.populateView(v, model, key);
                final TextView lltv = (TextView) v.findViewById(R.id.list_list_textview);
                if (model != null) {
                    lltv.setText(model.getName());
                } else {
                    lltv.setText("Loading lists...");
                }
                final String thisKey = key;

                ImageButton removeBtn = (ImageButton) v.findViewById(R.id.list_list_deletebutton);
                removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent removeOtherRef = new Intent(getContext(), DeleteListService.class);
                        removeOtherRef.putExtra(Constants.KEY_LIST_ID, thisKey);
                        getContext().startService(removeOtherRef);

                        Firebase ref = new Firebase(Constants.FIREBASE_URL);

                         /* Make a map for the removal */
                        HashMap<String, Object> updatedRemoveListMap = new HashMap<String, Object>();

                        /* Remove the item by passing null */
                        updatedRemoveListMap.put("/" + Constants.FIREBASE_NODENAME_LISTS + "/" + userEmail + "/" +thisKey, null);
                        updatedRemoveListMap.put("/" + Constants.FIREBASE_NODENAME_ITEMS + "/" + thisKey, null);
                        updatedRemoveListMap.put("/" + Constants.FIREBASE_NODENAME_SHOPS + "/" + thisKey, null);
                        updatedRemoveListMap.put("/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + thisKey, null);
                        updatedRemoveListMap.put("/" + Constants.FIREBASE_NODENAME_PANTRY_ITEMS + "/" + thisKey, null);
                        updatedRemoveListMap.put("/" + Constants.FIREBASE_NODENAME_SHELVES + "/" + thisKey, null);
                        ref.updateChildren(updatedRemoveListMap);
                        notifyDataSetChanged();
                    }
                });
            }
        };
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Item " + position + " clicked.");
                List selectedList = mListAdapter.getItem(position);
                if (selectedList != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    /* Get the list ID using the adapter's get ref method to get the Firebase
                     * ref and then grab the key.
                     */
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(Constants.KEY_LIST_ID, mListAdapter.getRef(position).getKey());
                    editor.commit();

                    /* Starts an active showing the details for the selected list */
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    public void initialiseScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_lists);
    }
}
