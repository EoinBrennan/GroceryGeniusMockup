package com.team5.grocerygeniusmockup.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.ListActivityFragments.ShareListDialogFragment;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddShelfDialogFragment;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddShopDialogFragment;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.PantryFragment;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.ShoppingListFragment;
import com.team5.grocerygeniusmockup.Utilities.Constants;

/**
 * Represents the home screen of the app which has a {@link ViewPager} with a
 * ShoppingListsFragment and PantryFragment.
 */

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /* Setting up the multiple screens. */

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    /* Used to fetch the User's ID. */

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate");

        /* Set up tabs, toolbar. */
        setUpScreen();

        /* Initialise preferences first time the app runs. */

        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!mPrefs.getBoolean("prefs_initialisted", false)) {
            Log.d(LOG_TAG, "initiliasing prefs");
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("prefs_initialised", true);
            editor.putBoolean("delete_checked", true);
            editor.putBoolean("move_to_pantry_checked", false);
            editor.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings_new_shop: showAddShopDialog();
                break;
            case R.id.settings_new_shelf: showAddShelfDialog();
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.settings_share_list:
                DialogFragment dialog = (DialogFragment) ShareListDialogFragment.newInstance();
                dialog.show(MainActivity.this.getFragmentManager(), "ShareListDialogFragment");
                break;
            case R.id.action_logout:
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.remove(Constants.KEY_LIST_ID);
                editor.remove(Constants.USER_ID);
                editor.remove(Constants.USER_EMAIL);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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

    /* Set up the layout that hosts the various fragments of the main activity. */

    public void setUpScreen() {
        Log.d(LOG_TAG, "setUpScreen");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Create the adapter that will return a fragment for each of the primary sections of the
         * activity.
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout with the adapter just initialised.
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void showAddShopDialog() {
        DialogFragment dialog = (DialogFragment) AddShopDialogFragment.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), "AddShopDialogFragment");
    }

    public void showAddShelfDialog() {
        DialogFragment dialog = (DialogFragment) AddShelfDialogFragment.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), "AddShelfDialogFragment");
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            /**
             * Set fragment to different fragments depending on position in ViewPager
             */
            switch (position) {
                case 0:
                    fragment = ShoppingListFragment.newInstance();
                    break;
                case 1:
                    fragment = PantryFragment.newInstance();
                    break;
            }

            return fragment;
        }

        // Set to only return two, as that's how many pages are in the MainActivity.

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.shopping_list_frag_title);
                case 1:
                    return getResources().getString(R.string.pantry_frag_title);
            }
            return null;
        }
    }
}
