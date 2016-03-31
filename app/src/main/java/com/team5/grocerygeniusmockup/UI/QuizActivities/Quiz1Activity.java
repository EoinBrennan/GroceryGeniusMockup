package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.team5.grocerygeniusmockup.R;

/**
 * Created by User on 30/03/2016.
 */
public class Quiz1Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        final EditText email_box = (EditText) findViewById(R.id.promptEmail);
        final EditText password_box = (EditText) findViewById(R.id.promptPassword);
        final EditText confirm_password = (EditText) findViewById(R.id.confirmPassword);

        Button next_button = (Button) findViewById(R.id.next_button);


        next_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String passwordConfirm = confirm_password.getText().toString();
                        String emailInput = email_box.getText().toString();
                        String passwordInput = password_box.getText().toString();

                        Intent logInIntent = new Intent(Quiz1Activity.this, Quiz2Activity.class);
                        startActivity(logInIntent);
                    }
                }
        );
    }





}
