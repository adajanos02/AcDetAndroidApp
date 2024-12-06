package com.wit.example.activities;

import static com.wit.example.helpers.News.parseXML;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.databinding.TrafficInfoBinding;
import com.wit.example.databinding.TrafficInfoPoliceBinding;
import com.wit.example.helpers.ListAdapter;
import com.wit.example.helpers.MongoDbInitializer;
import com.wit.example.helpers.News;
import com.wit.example.models.AccidentInfo;
import java.io.InputStream;
import java.util.ArrayList;
import io.realm.Realm;

public class PoliceTrafficInfoActivity extends AppCompatActivity {
    private ArrayList<AccidentInfo> newsList = new ArrayList<>();
    TrafficInfoPoliceBinding binding;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TrafficInfoPoliceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getNewsFromXml();

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
                    listAdapter = new ListAdapter(PoliceTrafficInfoActivity.this, accidentList);
                    binding.newsList.setAdapter(listAdapter);
                    binding.newsList.setClickable(true);

                    binding.newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                            Intent intent = new Intent(PoliceTrafficInfoActivity.this, DetailedActivity.class);
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


}
