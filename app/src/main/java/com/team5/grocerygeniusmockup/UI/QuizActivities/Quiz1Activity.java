package com.team5.grocerygeniusmockup.UI.QuizActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.team5.grocerygeniusmockup.R;

import java.util.Map;

/**
 * Created by User on 30/03/2016.
 */

//quiz1activity now goes to quiz3activity instead of quiz2activity
//So the order is Quiz1Acitivty => Quiz3Activity => Quiz2Activity
public class Quiz1Activity extends AppCompatActivity {

    EditText email_box;
    EditText password_box;
    EditText confirm_password;
    String passwordInput;
    String passwordConfirm;
    String email;
    Firebase ref;
    Boolean canMoveOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        email_box = (EditText) findViewById(R.id.promptEmail);
        password_box = (EditText) findViewById(R.id.promptPassword);
        confirm_password = (EditText) findViewById(R.id.confirmPassword);

        canMoveOn = false;

        Button next_button = (Button) findViewById(R.id.next_button);

        next_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        email = email_box.getText().toString();
                        passwordInput = password_box.getText().toString();
                        passwordConfirm = confirm_password.getText().toString();

                        Boolean isPasswordValidated = validatePassword(passwordInput, passwordConfirm);

                        if(!isPasswordValidated){
                            Toast.makeText(getApplicationContext(), "Please confirm your password correctly",
                                    Toast.LENGTH_LONG).show();
                            password_box.setText("");
                            confirm_password.setText("");
                        }else{
                           /////////////use data to create new account///////
                            ref = new Firebase("https://logintestcs353.firebaseio.com");
                            ref.createUser(email, passwordInput, new Firebase.ValueResultHandler<Map<String, Object>>() {
                                @Override
                                public void onSuccess(Map<String, Object> result) {
                                    Toast.makeText(getApplicationContext(), "Account successfully created",
                                            Toast.LENGTH_LONG).show();

                                    //create intent
                                    Intent logInIntent = new Intent(Quiz1Activity.this, Quiz2Activity.class);

                                    //send data
                                    logInIntent.putExtra("email", email);
                                    logInIntent.putExtra("password", passwordInput);

                                    //start activity
                                    startActivity(logInIntent);

                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    //there was an error
                                    Toast.makeText(getApplicationContext(), "An error occurred creating the account",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    public boolean validatePassword(String pass1, String pass2){
                        boolean validated = false;
                        if(pass1.equals(pass2)){
                            validated = true;
                        }
                        return validated;
                    }
                }
        );
    }





}
