package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.models.PersonalUserInfo;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoCollection;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.bson.Document;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    Button register_btn;
    EditText email_input;
    EditText password_input;
    EditText phone_input;

    EditText allergiak_input;
    EditText fullname_input;
    Spinner bloodtype_input;

    EditText tajszam_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        register_btn = findViewById(R.id.registerButton);
        email_input = findViewById(R.id.emailEditText);
        password_input = findViewById(R.id.passwordEditText);
        phone_input = findViewById(R.id.phoneEditText);
        fullname_input = findViewById(R.id.etFullName);
        allergiak_input = findViewById(R.id.etAllergies);
        bloodtype_input = findViewById(R.id.spinnerBloodType);
        tajszam_input = findViewById(R.id.etTAJ);

        register_btn.setOnClickListener((v) -> {
            PersonalUserInfo userInfo = new PersonalUserInfo(
                fullname_input.getText().toString(),
                    tajszam_input.getText().toString(),
                    phone_input.getText().toString(),
                    (int)bloodtype_input.getSelectedItemId(),
                    allergiak_input.getText().toString(),
                    email_input.getText().toString(),
                    password_input.getText().toString()

            );
            Register(userInfo);
        });


    }

    public void Register(PersonalUserInfo userInfo) {
        LoginActivity.app = new App(new AppConfiguration.Builder(LoginActivity.AppId).build());
        Credentials credentials = Credentials.emailPassword(userInfo.email, userInfo.password);
        LoginActivity.user = LoginActivity.app.currentUser();
        LoginActivity.app.getEmailPassword().registerUserAsync(userInfo.email,userInfo.password, it -> {
            if (it.isSuccess()){
                Log.v("User", "Sign up successfully");
                LoginActivity.app.loginAsync(credentials, new App.Callback<io.realm.mongodb.User>() {
                    @Override
                    public void onResult(App.Result<io.realm.mongodb.User> result) {
                        if (result.isSuccess()) {
                            Log.v("User", "Logged in successfully");
                            LoginActivity.user = LoginActivity.app.currentUser();
                            PushNewUser(userInfo);
                            Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Log.v("User", "Failed to login");
                        }
                    }
                });
            }
            else{
                Log.v("User", "Sign up failed");
            }
        });
    }

    public void PushNewUser(PersonalUserInfo userInfo) {
        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        MongoCollection<Document> mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");

        //LoginActivity.user = LoginActivity.app.currentUser();

        Document document = new Document()
                .append("email", userInfo.email)
                .append("password", userInfo.password)
                .append("phone", userInfo.phoneNumber)
                .append("latitude", "")
                .append("longitude", "")
                .append("allergiak", userInfo.allergiak)
                .append("fullname", userInfo.fullname)
                .append("medCond", userInfo.tajszam)
                .append("bloodType", userInfo.bloodtype)
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
