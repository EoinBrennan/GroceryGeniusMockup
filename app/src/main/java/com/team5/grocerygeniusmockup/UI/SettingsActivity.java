package com.team5.grocerygeniusmockup.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import com.team5.grocerygeniusmockup.R;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences mPrefs;
    CheckBox deleteCheckBox;
    CheckBox pantryCheckBox;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = mPrefs.edit();

        boolean delete_checked = mPrefs.getBoolean("delete_checked", false);
        boolean move_to_pantry_checked = mPrefs.getBoolean("move_to_pantry_checked", false);

        deleteCheckBox = (CheckBox) findViewById(R.id.settings_delete_item_check);
        deleteCheckBox.setChecked(delete_checked);

        pantryCheckBox = (CheckBox) findViewById(R.id.settings_move_item_check);
        pantryCheckBox.setChecked(move_to_pantry_checked);
    }

    public void choseDeleteItems (View v) {
        deleteCheckBox.setChecked(true);
        pantryCheckBox.setChecked(false);

        editor.putBoolean("delete_checked", true);
        editor.putBoolean("move_to_pantry_checked", false);
        editor.commit();
    }

    public void choseMoveItems (View v) {
        deleteCheckBox.setChecked(false);
        pantryCheckBox.setChecked(true);

        editor.putBoolean("delete_checked", false);
        editor.putBoolean("move_to_pantry_checked", true);
        editor.commit();
    }

}
