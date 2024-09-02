package com.wit.example.helpers;

import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;


public class SmsHelper {
    public static void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        //smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        Log.v("SMS", "SMS sent!" + phoneNumber);
    }


}
