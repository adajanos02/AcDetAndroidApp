package com.wit.example.helpers;

import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.wit.example.activities.LoginActivity;
import com.wit.example.models.BloodTypeEnum;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;


public class SmsHelper {

    public String persDetails = "";

    public static void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        Log.v("SMS", "SMS sent!" + phoneNumber);

    }

    public void smsTextBuilder(MongoCollection<Document> mongoCollection) {

        Document query = new Document().append("userId", LoginActivity.user.getId());

        RealmResultTask<MongoCursor<Document>> result = mongoCollection.find(query).iterator();
        StringBuilder builder = new StringBuilder();
        result.getAsync(task -> {
            if (task.isSuccess()){
                Document currentDoc = task.get().next();
                builder.append("\nTeljes név: ").append(currentDoc.getString("fullname")).append("\n");
                builder.append("Egészségügyi állapot: ").append(currentDoc.getString("medCond")).append("\n");
                builder.append("Allergiák: ").append(currentDoc.getString("allergiak")).append("\n");
                builder.append("Vércsoport: ").append(BloodTypeEnum.getText(currentDoc.getInteger("bloodType"))).append("\n");
                persDetails = builder.toString();
            }
        });
    }

    public static String gpsLinkGenerator(double latitude, double longitude) {
       return "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
    }

}
