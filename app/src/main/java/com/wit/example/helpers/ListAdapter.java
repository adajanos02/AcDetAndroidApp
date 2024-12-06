package com.wit.example.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wit.example.R;
import com.wit.example.models.AccidentInfo;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<AccidentInfo> {
    public ListAdapter(@NonNull Context context, ArrayList<AccidentInfo> dataArrayList) {
        super(context, R.layout.list_item, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        AccidentInfo listData = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listTitle = view.findViewById(R.id.listName);


        if (listData.image.equals("1")) {
            listImage.setImageResource(R.drawable.images);
        }
        else if (listData.image.equals("2")) {
            listImage.setImageResource(R.drawable.warning);
        }

        listTitle.setText(listData.title);

        return view;
    }

}
