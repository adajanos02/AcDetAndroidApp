package com.wit.example.helpers;

import android.telephony.SmsManager;


public class SmsHelper {
    public static void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }


}
