

package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class Quiz3Activity extends ListActivity {

    String listItem[] = {"First shop","Second shop","third shop"};

    ArrayAdapter<String> myAdapter;
    EditText et;
    ListView myList;
    String email;
    String password;
    ArrayList<String> values;
    Firebase ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz2);

        //get email and password sent from Quiz1Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            password = extras.getString("password");
        }

        et = (EditText)findViewById(R.id.enterShopName);

        values = new ArrayList();
        for (int i = 0; i < listItem.length; i++) {
            values.add(listItem[i]);
        }

        //*
        myAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                values
        );
        //*/

        myList = (ListView)findViewById(android.R.id.list);
        myList.setAdapter(myAdapter);


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
                        et.setHint("Enter another section name");

                        adapter.notifyDataSetChanged();
                    }
                }
        );

        Button next = (Button) findViewById(R.id.next_button);

        next.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        ////////Firebase uploads go here///////////
                        ref = new Firebase("https://logintestcs353.firebaseio.com");

                        ////////////////////////////////////////////

                        //*
                        Intent myIntent = new Intent(Quiz3Activity.this, LoginActivity.class);

                        //send email and password
                        myIntent.putExtra("email", email);
                        myIntent.putExtra("password", password);

                        //logInIntent.putExtra("isTrue", someData);
                        startActivity(myIntent);
                        //*/
                    }
                }
        );
    }

    @Override
    protected void onListItemClick( ListView l, View v, int position, long id)
    {
        CheckedTextView textView = (CheckedTextView)v;
        textView.setChecked(!textView.isChecked());
    }

}
