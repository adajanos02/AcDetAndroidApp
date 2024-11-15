package com.wit.example;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.telephony.SmsManager;

import com.mongodb.client.MongoCursor;
import com.wit.example.helpers.SmsHelper;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.options.FindOptions;

public class SmsTest {
    @Test
    public void testGpsLinkGenerator() {
        double latitude = 47.497913;
        double longitude = 19.040236;

        String expected = "https://www.google.com/maps/search/?api=1&query=47.497913,19.040236";
        String result = SmsHelper.gpsLinkGenerator(latitude, longitude);

        assertEquals(expected, result);
    }

}
