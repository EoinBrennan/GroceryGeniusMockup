package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.Activity;
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
import com.team5.grocerygeniusmockup.Model.PantryModel.Shelf;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Section;
import com.team5.grocerygeniusmockup.Model.ShoppingListModel.Shop;
import com.team5.grocerygeniusmockup.R;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Quiz2Activity extends Activity {

    String email;
    String listkey;
    String password;
    Firebase ref;

    private ListView listView1;
    EditText et;
    ArrayList<String> myList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz3);

        //get email and passwrod sent from Quiz1Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            listkey = extras.getString(Constants.KEY_LIST_ID);
            password = extras.getString("password");
        }

        myList = new ArrayList<>();
        myList.add("Tesco");
        myList.add("Aldi");
        myList.add("Lidl");

        final ListViewCustomAdapter adapter = new ListViewCustomAdapter(this,
                R.layout.listview_item_row, myList);


        listView1 = (ListView) findViewById(R.id.quizlistView);

        View header = (View) getLayoutInflater().inflate(R.layout.listview_header_row, null);
        listView1.addHeaderView(header);

        listView1.setAdapter(adapter);

        Button addSection = (Button) findViewById(R.id.submit_section);
        et = (EditText) findViewById(R.id.prompt_section);
//*
        addSection.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String input;
                        input = et.getText().toString();
                        myList.add(input);

                        et.setText("");
                        et.setHint("Enter another shop");
                        adapter.notifyDataSetChanged();
                    }
                });

        Button nextButton = (Button) findViewById(R.id.button_quiz2activity_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ////////Firebase uploads go here///////////
                int mostFrequent = 7;
                String mfShop ="";
                String mfKey ="";

                // Set up a ref in the shops section of the user's Firebase.
                String FIREBASE_MY_URL_SHOPS = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_SHOPS + "/" + listkey;

                // Initialise a Firebase object at the shops node.
                Firebase shopRef = new Firebase(FIREBASE_MY_URL_SHOPS);

                for (int i = 0; i < myList.size(); i++) {
                    int thisFreq = adapter.getFrequency(i);
                    Shop newShop = new Shop(myList.get(i), thisFreq);

                    // Create a unique key for a new child and fetch it's key.
                    Firebase newShopRef = shopRef.push();
                    String newShopKey = newShopRef.getKey();

                    // Store the Shop object at the new child node.
                    newShopRef.setValue(newShop);

                    if (thisFreq < mostFrequent) {
                        mostFrequent = thisFreq;
                        mfShop = myList.get(i);
                        mfKey = newShopKey;
                    }

                    // Add a default section to the newly created shop by following the same process.
                    Section defaultSection = new Section("Other", myList.get(i), 0);
                    String FIREBASE_MY_URL_SHOPSECTION = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_SECTIONS + "/" + listkey + "/" + newShopKey;
                    Firebase sectionRef = new Firebase(FIREBASE_MY_URL_SHOPSECTION);
                    Firebase defaultSecRef = sectionRef.push();
                    defaultSecRef.setValue(defaultSection);
                }
                ////////////////////////////////////////////

                Shelf fridge = new Shelf("Fridge", 1);
                Shelf cupboard = new Shelf("Cupboard", 2);

                // Set up a Firebase address and node reference for the user's shelves.
                String FIREBASE_MY_URL_SHELVES = Constants.FIREBASE_URL + "/" + Constants.FIREBASE_NODENAME_SHELVES + "/" + listkey;
                Firebase shelfRef = new Firebase(FIREBASE_MY_URL_SHELVES);

                // Create a unique key for a new node and place the shelf item at that node.
                Firebase fridgeRef = shelfRef.push();
                fridgeRef.setValue(fridge);

                Firebase cupBRef = shelfRef.push();
                cupBRef.setValue(cupboard);

                Intent myIntent = new Intent(Quiz2Activity.this, Quiz3Activity.class);

                //send email and password
                myIntent.putExtra("email", email);
                myIntent.putExtra("password", password);
                myIntent.putExtra(Constants.KEY_LIST_ID, listkey);
                myIntent.putExtra("shopname", mfShop);
                myIntent.putExtra("shopkey", mfKey);

                startActivity(myIntent);
            }
        });
//*/

    }
}