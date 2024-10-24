package com.wit.example.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.R;
import com.wit.example.databinding.TrafficinfoDetailedBinding;

public class DetailedActivity extends AppCompatActivity {
    TrafficinfoDetailedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TrafficinfoDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        if (intent != null){
            String title = intent.getStringExtra("title");
            String date = intent.getStringExtra("date");
            String address = intent.getStringExtra("address");
            String image = intent.getStringExtra("image");

            if (image.equals("1")) {
                binding.detailImage.setImageResource(R.drawable.images);
            }
            else if (image.equals("2")) {
                binding.detailImage.setImageResource(R.drawable.warning);
            }
            binding.descriptionDetail.setText(title);
            binding.dateDetail.setText(date);
            binding.addressDetail.setText(address);
        }
    }
}
