package com.wit.example.activities;

import static com.wit.example.helpers.News.parseXML;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.databinding.ActivityMainBinding;
import com.wit.example.databinding.TrafficInfoBinding;
import com.wit.example.helpers.ListAdapter;
import com.wit.example.helpers.MongoDbInitializer;
import com.wit.example.helpers.News;
import com.wit.example.models.AccidentInfo;

import org.bson.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class TrafficInfoActivity extends AppCompatActivity {
    private ArrayList<AccidentInfo> newsList = new ArrayList<>();
    TrafficInfoBinding binding;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TrafficInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener((v) -> {
            Intent intent = new Intent(TrafficInfoActivity.this, AddTrafficInfoActivity.class);
            startActivity(intent);
        });

        getAccidentNews();
        //getNewsFromXml();

        Realm.init(getApplicationContext());
    }

    public void getNewsFromXml() {
        new Thread(() -> {
            try {
                InputStream inputStream = News.getXMLData("https://www.police.hu/hu/rss/Baleseti%20h%C3%ADrek");
                ArrayList<AccidentInfo> accidentList = parseXML(inputStream);
                for (int i = 0; i < accidentList.size(); i++) {
                    newsList.add(accidentList.get(i));
                }
                runOnUiThread(() -> {
                    listAdapter = new ListAdapter(TrafficInfoActivity.this, accidentList);
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
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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
