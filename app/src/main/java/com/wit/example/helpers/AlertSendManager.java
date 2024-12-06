package com.wit.example.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.wit.example.activities.LoginActivity;
import com.wit.example.activities.StartRideActivity;
import org.bson.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class AlertSendManager extends AppCompatActivity {

   private Context context;
   public AlertSendManager(Context context){
       this.context = context;
   }

    public void userAlert(double latitude, double longitude) {


        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "Location");

        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find().iterator();
        LoginActivity.user = LoginActivity.app.currentUser();

        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                while (results.hasNext()) {
                    Document currentDoc = results.next();
                    if (!Objects.equals(currentDoc.getString("userId"), LoginActivity.user.getId())) {
                        if (currentDoc.getDouble("latitude") != null) {
                            double resultLat = currentDoc.getDouble("latitude");
                            double resultLong = currentDoc.getDouble("longitude");
                            DistanceCalculator disCal = new DistanceCalculator();

                            double distance = disCal.greatCircleInKilometers(latitude, longitude, resultLat, resultLong);

                            Log.v("DISTANCE", String.valueOf(distance));
                            if (distance < 30) {
                                SmsHelper.sendSms(currentDoc.getString("phone"), "Baleset történt ezen a címen: "
                                        + getAddressFromCoordinates(latitude, longitude)
                                        + StartRideActivity.smsHelper.persDetails
                                        + SmsHelper.gpsLinkGenerator(latitude, longitude));
                                Toast.makeText(getApplicationContext(), "Sent: " + currentDoc.getString("phone"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            } else {
                Log.v("Task error", task.getError().toString());
            }
        });
    }


    public void sendSmsToContacts(double latitude, double longitude) {

        LoginActivity.user = LoginActivity.app.currentUser();
        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "Contacts");

        Document queryFilter = new Document().append("userId", LoginActivity.user.getId());

        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                while (results.hasNext()) {
                    Document currentDoc = results.next();
                    if (currentDoc.getString("phone") != null) {
                        SmsHelper.sendSms(currentDoc.getString("phone"), "Baleset történt ezen a címen: "
                                + getAddressFromCoordinates(latitude, longitude)
                                + StartRideActivity.smsHelper.persDetails
                                + SmsHelper.gpsLinkGenerator(latitude, longitude));
                        Toast.makeText(context, "Sent: " + currentDoc.getString("phone"), Toast.LENGTH_LONG).show();
                    }

                }
            } else {
                Log.v("Task error", task.getError().toString());
            }
        });
    }

    public void pushAccidentInfo(double latitude, double longitude) {
        LoginActivity.user = LoginActivity.app.currentUser();
        MongoCollection<Document> mongoCollection = MongoDbInitializer.initialize("mongodb-atlas", "User", "AccidentInfo");
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentTime = sdf.format(new Date(currentTimeMillis));
        Document document = new Document()
                .append("title", "Baleset")
                .append("address", getAddressFromCoordinates(latitude, longitude))
                .append("image", "1")
                .append("date", currentTime);

        mongoCollection.insertOne(document).getAsync(result -> {
            if (result.isSuccess()){
                Log.v("Data", "Data inserted successfully");
            }
            else {
                Log.v("Data", "Error: " + result.getError().toString());
            }
        });

    }

    public String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
