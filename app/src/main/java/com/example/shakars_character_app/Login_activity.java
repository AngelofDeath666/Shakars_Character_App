package com.example.shakars_character_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_activity extends AppCompatActivity implements View.OnClickListener {

    Button login, createUser;
    TextView title, username, password, forgotPassword;
    EditText usernameInput, passwordInput;
    SharedPreferences sharedPref;
    String currentTheme, sharedPreference;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        //Buttons
        login = findViewById(R.id.loginButton);
        login.setOnClickListener(this);
        createUser = findViewById(R.id.loginCreateUser);
//        createUser.setOnClickListener(this);
        createUser.setVisibility(View.INVISIBLE);

        //Text Fields
        title = findViewById(R.id.loginAppTitle);
        username = findViewById(R.id.loginPageUsernameText);
        password = findViewById(R.id.loginPasswordText);
        forgotPassword = findViewById(R.id.loginForgotPasswordClickable);
        forgotPassword.setOnClickListener(this);

        //Edit Text Fields
        usernameInput = findViewById(R.id.loginUsernameEditText);
        passwordInput = findViewById(R.id.loginPasswordEditText);



    }



    @Override
    public void onClick(View v) {
        if (login.getId() == v.getId()) {
            v.startAnimation(buttonClick);
            Intent intent = new Intent(v.getContext(),FrontPage_activity.class);
            v.getContext().startActivity(intent);


        } else if (createUser.getId() == v.getId()){
            v.startAnimation(buttonClick);
            Intent intent = new Intent(v.getContext(),CreateUser_activity.class);
            v.getContext().startActivity(intent);
        }

    }
}
