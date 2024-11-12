package com.wit.example.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.helpers.MongoDbInitializer;

import org.bson.Document;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText tajEditText;
    private Spinner bloodTypeSpinner;
    private EditText allergiesEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_settings);
        getPersonalDatas();
        fullNameEditText = findViewById(R.id.etFullName);
        tajEditText = findViewById(R.id.etTAJ);
        bloodTypeSpinner = findViewById(R.id.spinnerBloodType);
        allergiesEditText = findViewById(R.id.etAllergies);


        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBloodType = bloodTypeSpinner.getSelectedItem().toString();

                if (selectedBloodType.equals("Válassza ki vércsoportját")) {
                    Toast.makeText(PersonalInfoActivity.this, "Kérjük, válassza ki a vércsoportját!", Toast.LENGTH_SHORT).show();
                } else {
                   updatePersonalData();
                   Toast.makeText(PersonalInfoActivity.this, "Adatok mentve!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getPersonalDatas() {
        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "Location");

        Document query = new Document().append("userId", LoginActivity.user.getId());
        RealmResultTask<MongoCursor<Document>> result = mongoCollection.find(query).iterator();
        result.getAsync(task -> {
            if (task.isSuccess()){
                Document currentDoc = task.get().next();
                fullNameEditText.setText(currentDoc.getString("fullname"));
                tajEditText.setText(currentDoc.getString("medCond"));
                allergiesEditText.setText(currentDoc.getString("allergiak"));
                bloodTypeSpinner.setSelection(currentDoc.getInteger("bloodType"));
            }
        });

    }


    public void updatePersonalData() {
        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "Location");

        Document query = new Document().append("userId", LoginActivity.user.getId());
        Document update = new Document().append("$set",
                new Document().append("fullname", fullNameEditText.getText().toString())
                        .append("medCond", tajEditText.getText().toString())
                        .append("allergiak", allergiesEditText.getText().toString())
                        .append("bloodType", (int)bloodTypeSpinner.getSelectedItemId()));

        mongoCollection.updateOne(query, update).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("UpdateFunction", "Updated Data");

            } else {
                Log.v("UpdateFunction", "Error" + result.getError().toString());
            }
        });
    }
}
