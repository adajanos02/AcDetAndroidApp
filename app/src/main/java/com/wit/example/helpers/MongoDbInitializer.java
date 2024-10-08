package com.wit.example.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.wit.example.activities.LoginActivity;
import com.wit.example.activities.StartRideActivity;

import org.bson.Document;

import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MongoDbInitializer extends AppCompatActivity {

    public static MongoCollection<Document> initialize(String client, String database, String collection) {
        LoginActivity.user = LoginActivity.app.currentUser();
        MongoClient mongoClient = LoginActivity.user.getMongoClient(client);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        return mongoDatabase.getCollection(collection);
    }

    public void updateLocation(double latitude, double longitude) {

        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "Location");

        Document query = new Document().append("userId", LoginActivity.user.getId());
        Document update = new Document().append("$set",
                new Document().append("latitude", latitude)
                        .append("longitude", longitude));
        Log.v("userId", LoginActivity.user.getId());

        mongoCollection.updateOne(query, update).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("UpdateFunction", "Updated Data");

            } else {
                Log.v("UpdateFunction", "Error" + result.getError().toString());
            }
        });


    }


}
