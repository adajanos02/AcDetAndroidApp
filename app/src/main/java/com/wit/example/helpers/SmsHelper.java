package com.wit.example.helpers;

import android.telephony.SmsManager;
import android.util.Log;


public class SmsHelper {
    public static void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Log.v("SMS", "SMS sent!" + phoneNumber);
    }


}
