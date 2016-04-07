package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.team5.grocerygeniusmockup.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quiz3Activity extends Activity {

    private ListView listView1;
    EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz3);

        String[] myValues = {
                "Dairy",
                "Meat",
                "Bread"
        };

        final ArrayList<String> myList = new ArrayList<String>(Arrays.asList(myValues));

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
                        et.setHint("Enter another section");
                        adapter.notifyDataSetChanged();
                    }
                });
//*/

    }
}