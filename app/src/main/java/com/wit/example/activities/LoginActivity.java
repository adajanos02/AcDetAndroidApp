package com.wit.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.wit.example.R;
import com.wit.example.models.PersonalUserInfo;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;


public class LoginActivity extends AppCompatActivity{
    static String AppId = "android_project-ibyjncm";

    public static User user;
    public static MongoDatabase mongoDatabase;
    public static MongoClient mongoClient;

    public static App app;


    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Realm.init(this);

        Button submitButton = findViewById(R.id.login_btn);
        Button registerButton = findViewById(R.id.signup_btn);

        submitButton.setOnClickListener((v) -> {
            EditText text = (EditText)findViewById(R.id.username_input);
            String username = text.getText().toString();
            EditText text2 = (EditText)findViewById(R.id.password_input);
            String password = text2.getText().toString();
            Login(username,password);
        });

        registerButton.setOnClickListener((v) -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });


    }

    public void Login(String email, String password) {

        app = new App(new AppConfiguration.Builder(LoginActivity.AppId).build());

        Credentials credentials = Credentials.emailPassword(email, password);

        app.loginAsync(credentials, new App.Callback<io.realm.mongodb.User>() {
            @Override
            public void onResult(App.Result<io.realm.mongodb.User> result) {
                if (result.isSuccess()) {
                    Log.v("User", "Logged in successfully");
                    user = app.currentUser();
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);

                }
                else {
                    Log.v("User", "Failed to login");
                }
            }
        });
    }



}