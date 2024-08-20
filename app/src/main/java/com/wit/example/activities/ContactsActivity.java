package com.wit.example.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private TextView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        //getContactList();
        LoginActivity.mongoClient = LoginActivity.user.getMongoClient("mongodb-atlas");
        LoginActivity.mongoDatabase = LoginActivity.mongoClient.getDatabase("User");
        mongoCollection = LoginActivity.mongoDatabase.getCollection("Location");

        list = findViewById(R.id.contact_list);



        insertData = (EditText) findViewById(R.id.data);
        insertBtn = (Button) findViewById(R.id.upload_btn);

        Realm.init(this);

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mongoCollection.insertOne(new Document("userId", LoginActivity.user.getId()).append("phone", insertData.getText().toString())).getAsync(result -> {
                    if (result.isSuccess()) {
                        Log.v("Data", "Data inserted successfully");
                        Toast.makeText(getApplicationContext(), "Data inserted", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Log.v("Data", "Error: " + result.getError().toString());
                    }
                });
                getContactList();
            }
        });
    }

    public void getContactList() {
        Document queryFilter = new Document().append("userId", LoginActivity.user.getId());

        RealmResultTask<MongoCursor<Document>> findContact = mongoCollection.find(queryFilter).iterator();
        final String[] data = new String[1];
        findContact.getAsync(task -> {
            if (task.isSuccess()){
                MongoCursor<Document> result = task.get();
                while(result.hasNext()) {
                    Document currentDoc = result.next();
                    if(currentDoc.getString("data")!= null){
                        data[0] = data[0] + currentDoc.getString("data");
                        list.setText(data[0]);
                    }
                }
                if (!result.hasNext()){
                    Log.v("Result", "Could not file data");
                }
            }
            else {
                Log.v("Task Error", task.getError().toString());
            }
        });
    }




}
