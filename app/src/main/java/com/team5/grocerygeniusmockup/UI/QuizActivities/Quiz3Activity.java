package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.MainActivityFragments.AddShopDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class Quiz3Activity extends ListActivity {

    String sections[] = {"Dairy","Meat","Bread"};

    ArrayAdapter<String> myAdapter;
    EditText et;
    ListView myList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_sections);
        et = (EditText)findViewById(R.id.enterShopName);

        List values = new ArrayList();
        for (int i = 0; i < sections.length; i++) {
            values.add(sections[i]);
        }

        //*fix this!!!
        myAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_shop_order,
                R.id.text_view_item_name,
                values
        );
        //*/

        myList = (ListView)findViewById(android.R.id.list);
        myList.setAdapter(myAdapter);

        //spinner setup
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner_shop_order);


        /* Spinner Drop down elements
        List<String> how_often = new ArrayList<String>();
        mySpinner.add("Automobile");
        mySpinner.add("Business Services");
        categories.add("Computers");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");
        //*/

        //*
        Button addShop = (Button) findViewById(R.id.button_add_new_shop);

        addShop.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        ArrayAdapter<String> adapter = myAdapter;
                        String device;

                        List myArrayList = new ArrayList();
                        device = et.getText().toString();
                        myArrayList.add(device);
                        adapter.add(device);
                        et.setText("");
                        et.setHint("Enter another section");

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onListItemClick( ListView l, View v, int position, long id)
    {
        CheckedTextView textView = (CheckedTextView)v;
        textView.setChecked(!textView.isChecked());
    }






}
