package com.wit.example.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;

import org.bson.BsonDocument;
import org.bson.Document;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class ContactsActivity extends AppCompatActivity {

    private EditText insertData;
    private Button insertBtn;

    private MongoCollection<Document> mongoCollection;
    private ArrayList<String> phone;
    private ArrayAdapter<String> adapter;
    private ListView list;

    ArrayList<String> strings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_list);

        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");

        list = findViewById(R.id.contact_list);
        phone = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                phone
        );
        getContactList();


        insertData = (EditText) findViewById(R.id.data);
        insertBtn = (Button) findViewById(R.id.upload_btn);
        Realm.init(getApplicationContext());




        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Document document = new Document().append("phone", insertData.getText().toString()).append("myid", "1234").append("userId", LoginActivity.user.getId());
                mongoCollection.insertOne(document).getAsync(result -> {
                    if (result.isSuccess()) {
                        Log.v("Data", "Data inserted successfully");
                        Toast.makeText(getApplicationContext(), "Data inserted", Toast.LENGTH_LONG).show();
                        getContactList();
                    }
                    else{
                        Log.v("Data", "Error: " + result.getError().toString());
                    }
                });

            }
        });
    }

    public void getContactList() {
        phone.clear();
        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");
        LoginActivity.user = LoginActivity.app.currentUser();


        Document queryFilter = new Document().append("userId",LoginActivity.user.getId());

        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();

        final String[] data = new String[1];
        data[0] = "";
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                while (results.hasNext()){
                    Document currentDoc = results.next();
                    if (currentDoc.getString("phone")!= null) {
                        phone.add(currentDoc.getString("phone"));
                        data[0] = data[0] + "\n" + (currentDoc.getString("phone"));
                        //list.setText(data[0]);
                    }
                    list.setAdapter(adapter);
                }


            }
            else{
                Log.v("Task error",task.getError().toString());
            }
        });


    }




}
