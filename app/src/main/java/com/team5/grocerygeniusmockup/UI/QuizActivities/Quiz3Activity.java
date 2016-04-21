

package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.UI.LoginActivity;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class Quiz3Activity extends ListActivity {

    String listItem[] = {"Other"};

    ArrayAdapter<String> myAdapter;
    TextView instructions;
    EditText et;
    ListView myList;
    String email;
    String listkey;
    String shopname;
    String shopkey;
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
            listkey = extras.getString(Constants.KEY_LIST_ID);
            shopname = extras.getString("shopname");
            shopkey = extras.getString("shopkey");
        }

        instructions = (TextView) findViewById(R.id.textView_newuser_addsection_inst);
        instructions.setText("What Sections of " + shopname + " do you visit?");

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
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //*
        Button addShop = (Button) findViewById(R.id.button_add_new_shop);

        addShop.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        ArrayAdapter<String> adapter = myAdapter;
                        String device;

                        device = et.getText().toString();
                        values.add(device);
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

                        // Set up Firebase for this shop's sections.
                        String FIREBASE_MY_URL_SECTIONS = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + listkey + "/" + shopkey;
                        Firebase secRef = new Firebase(FIREBASE_MY_URL_SECTIONS);

                        for (int i = 1; i < values.size(); i++) {
                            // Create a new Section object using user data and parent shop name.
                            Section newSec = new Section(values.get(i), shopname, i);

                            // Create a unique key for a new node and put the newly created section there.
                            Firebase newSecRef = secRef.push();
                            newSecRef.setValue(newSec);
                        }
                        //*
                        Intent myIntent = new Intent(Quiz3Activity.this, LoginActivity.class);

                        //send email and password
                        myIntent.putExtra("email", email);
                        myIntent.putExtra("password", password);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
