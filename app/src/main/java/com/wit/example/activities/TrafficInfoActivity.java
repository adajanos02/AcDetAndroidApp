package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.databinding.ActivityMainBinding;
import com.wit.example.databinding.TrafficInfoBinding;
import com.wit.example.helpers.ListAdapter;
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
    private ArrayList<AccidentInfo> newsList = new ArrayList<>();

    private ArrayAdapter<AccidentInfo> adapter;

    TrafficInfoBinding binding;
    ListAdapter listAdapter;
    AccidentInfo accidentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TrafficInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                                currentDoc.getString("address"),
                                currentDoc.getString("image")
                        ));
                    }
                }
                listAdapter = new ListAdapter(TrafficInfoActivity.this, newsList);
                binding.newsList.setAdapter(listAdapter);
                binding.newsList.setClickable(true);

                binding.newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                        Intent intent = new Intent(TrafficInfoActivity.this, DetailedActivity.class);
                        intent.putExtra("image", newsList.get(i).image);
                        intent.putExtra("title", newsList.get(i).title);
                        intent.putExtra("date", newsList.get(i).date);
                        intent.putExtra("address", newsList.get(i).address);
                        startActivity(intent);
                    }
                });
            }
            else{
                Log.v("Task error",task.getError().toString());
            }
        });
    }
}
