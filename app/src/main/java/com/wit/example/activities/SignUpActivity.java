package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoCollection;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bson.Document;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    Button register_btn;
    EditText email_input;
    EditText password_input;
    EditText phone_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        register_btn = findViewById(R.id.registerButton);
        email_input = findViewById(R.id.emailEditText);
        password_input = findViewById(R.id.passwordEditText);
        phone_input = findViewById(R.id.phoneEditText);

        register_btn.setOnClickListener((v) -> {
            Register(email_input.getText().toString(), password_input.getText().toString(), phone_input.getText().toString());
        });


    }

    public void Register(String email, String password, String phoneNumber) {
        LoginActivity.app = new App(new AppConfiguration.Builder(LoginActivity.AppId).build());


        Credentials credentials = Credentials.emailPassword(email, password);
        LoginActivity.user = LoginActivity.app.currentUser();


        LoginActivity.app.getEmailPassword().registerUserAsync(email,password, it -> {
            if (it.isSuccess()){
                Log.v("User", "Sign up successfully");

                LoginActivity.app.loginAsync(credentials, new App.Callback<io.realm.mongodb.User>() {
                    @Override
                    public void onResult(App.Result<io.realm.mongodb.User> result) {
                        if (result.isSuccess()) {
                            Log.v("User", "Logged in successfully");
                            LoginActivity.user = LoginActivity.app.currentUser();
                            PushNewUser(email, password, phoneNumber);
                            Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                            startActivity(intent);

                        }
                        else {
                            Log.v("User", "Failed to login");
                        }
                    }
                });
//                Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
//                startActivity(intent);
            }
            else{
                Log.v("User", "Sign up failed");
            }
        });
    }

    public void PushNewUser(String email, String password, String phoneNumber) {
        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        MongoCollection<Document> mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");

        //LoginActivity.user = LoginActivity.app.currentUser();

        Document document = new Document()
                .append("email", email)
                .append("password", password)
                .append("phone", phoneNumber)
                .append("latitude", "")
                .append("longitude", "")
                .append("userId", LoginActivity.user.getId());
        mongoCollection.insertOne(document).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("Data", "Data inserted successfully");
            }
            else{
                Log.v("Data", "Error: " + result.getError().toString());
            }
        });
    }
}
