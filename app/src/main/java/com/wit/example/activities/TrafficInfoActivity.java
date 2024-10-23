package com.wit.example.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.helpers.MongoDbInitializer;
import com.wit.example.models.AccidentInfo;

import org.bson.Document;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class TrafficInfoActivity extends AppCompatActivity {

    private ListView list;
    private ArrayList<AccidentInfo> newsList;

    private ArrayAdapter<AccidentInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_info);

        list = findViewById(R.id.news_list);
        newsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                newsList
        );

        getAccidentNews();
        Realm.init(getApplicationContext());
    }

    public void getAccidentNews() {
        newsList.clear();
        LoginActivity.user = LoginActivity.app.currentUser();
        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "AccidentInfo");
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find().iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                while (results.hasNext()){
                    Document currentDoc = results.next();
                    if (currentDoc.getString("title")!= null) {
                        newsList.add( new AccidentInfo(
                                currentDoc.getString("title"),
                                currentDoc.getString("date"),
                                currentDoc.getString("address")
                        ));
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
