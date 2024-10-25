package com.wit.example.helpers;

import android.util.Xml;

import com.wit.example.models.AccidentInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class News {
    public static InputStream getXMLData(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getInputStream();
    }

    public static ArrayList<AccidentInfo> parseXML(InputStream inputStream) throws Exception {
        ArrayList<AccidentInfo> accidentList = new ArrayList<>();
        AccidentInfo accidentInfo = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "UTF-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(tagName)) {
                    accidentInfo = new AccidentInfo();
                } else if (accidentInfo != null) {
                    if ("title".equals(tagName)) {
                        accidentInfo.title = (parser.nextText());
                    } else if ("pubDate".equals(tagName)) {
                        accidentInfo.date = (parser.nextText());
                    } else if ("description".equals(tagName)) {
                        accidentInfo.address = (parser.nextText());
                    }
                    accidentInfo.image = "1";

                }
            } else if (eventType == XmlPullParser.END_TAG && "item".equals(tagName)) {
                accidentList.add(accidentInfo);
            }
            eventType = parser.next();
        }
        return accidentList;
    }

}
