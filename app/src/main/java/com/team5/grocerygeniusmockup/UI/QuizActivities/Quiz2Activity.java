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
import com.team5.grocerygeniusmockup.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Quiz2Activity extends Activity {

    String email;
    String password;
    Firebase ref;

    private ListView listView1;
    EditText et;
    ArrayList<String> myList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz3);

        String[] myValues = {
                "Tesco",
                "Aldi",
                "Lidl"
        };

        //get email and passwrod sent from Quiz1Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            password = extras.getString("password");
        }

        myList = new ArrayList<String>(Arrays.asList(myValues));

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
                ref = new Firebase("https://logintestcs353.firebaseio.com");

                ////////////////////////////////////////////
                Intent myIntent = new Intent(Quiz2Activity.this, Quiz3Activity.class);

                //send email and password
                myIntent.putExtra("email", email);
                myIntent.putExtra("password", password);

                startActivity(myIntent);
            }
        });
//*/

    }
}