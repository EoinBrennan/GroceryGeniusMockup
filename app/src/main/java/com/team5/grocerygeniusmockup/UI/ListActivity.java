package com.team5.grocerygeniusmockup.UI;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.ListActivityFragments.AddListDialogFragment;
import com.team5.grocerygeniusmockup.Utilities.Constants;

public class ListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ListActivity.class.getSimpleName();

    String userEmail;
    String userID;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);
        userEmail = mPrefs.getString(Constants.USER_EMAIL, "");
        userID = mPrefs.getString(Constants.USER_ID, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings_new_list: showAddListDialog();
                break;
            case R.id.action_logout:
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.remove(Constants.KEY_LIST_ID);
                editor.remove(Constants.USER_ID);
                editor.remove(Constants.USER_EMAIL);
                editor.commit();
                Intent intent = new Intent(ListActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public void showAddListDialog() {
        DialogFragment dialog = (DialogFragment) AddListDialogFragment.newInstance(userEmail, userID);
        dialog.show(ListActivity.this.getFragmentManager(), "AddListDialogFragment");
    }

}
