package com.wit.example.helpers;

import android.util.Xml;

import com.wit.example.models.AccidentInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
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
                        Date date = inputFormat.parse(parser.nextText());
                        accidentInfo.date = outputFormat.format(date);
                    } else if ("description".equals(tagName)) {
                        accidentInfo.address = parseDescription(parser.nextText());
                    }
                    accidentInfo.image = "2";

                }
            } else if (eventType == XmlPullParser.END_TAG && "item".equals(tagName)) {
                accidentList.add(accidentInfo);
            }
            eventType = parser.next();
        }
        return accidentList;
    }

    public static String parseDescription(String description) {
        description = description.replaceAll("&lt;.*?&gt;", "").trim();

        String[] lines = description.split("\\r?\\n");
        List<String> extractedTexts = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                extractedTexts.add(line);
            }
        }

        return extractedTexts.get(1);
    }




}
