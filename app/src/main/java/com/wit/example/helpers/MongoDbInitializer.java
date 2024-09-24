package com.wit.example.helpers;

import com.wit.example.activities.LoginActivity;

import org.bson.Document;

import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MongoDbInitializer {

    public static MongoCollection<Document> initialize(String client, String database, String collection) {
        MongoClient mongoClient = LoginActivity.user.getMongoClient(client);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        return mongoDatabase.getCollection(collection);
    }
}
