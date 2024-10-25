package com.wit.example.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.helpers.MongoDbInitializer;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.mongodb.mongo.MongoCollection;

public class AddTrafficInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_traffic_info);

        EditText descriptionEdit = findViewById(R.id.descriptionEdit);
        EditText addressEdit = findViewById(R.id.addressEdit);
        Spinner infoTypeSpinner = findViewById(R.id.infoType);

        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener((v) -> {
            addTrafficInfo(
                    descriptionEdit.getText().toString(),
                    addressEdit.getText().toString(),
                    String.valueOf(infoTypeSpinner.getSelectedItemId())
            );
            Intent intent = new Intent(AddTrafficInfoActivity.this, TrafficInfoActivity.class);
            startActivity(intent);
        });
    }

    public void addTrafficInfo(String description, String address, String index) {
        LoginActivity.user = LoginActivity.app.currentUser();
        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "AccidentInfo");
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentTime = sdf.format(new Date(currentTimeMillis));

        Document document = new Document()
                .append("title", description)
                .append("address", address)
                .append("image", index)
                .append("date", currentTime);

        mongoCollection.insertOne(document).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("InsertTrafficInfo", "Inserted Data");

            } else {
                Log.v("InsertTrafficInfo", "Error" + result.getError().toString());
            }
        });
    }
}
