package com.wit.example.helpers;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.wit.example.activities.LoginActivity;

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
