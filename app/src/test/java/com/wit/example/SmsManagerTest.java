//package com.wit.example;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;
//
//import android.telephony.SmsManager;
//
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoCursor;
//import com.wit.example.helpers.SmsHelper;
//import com.wit.example.models.BloodTypeEnum;
//
//import org.bson.conversions.Bson;
//import org.chromium.base.Callback;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.bson.Document;
//
//import java.util.ArrayList;
//
//import io.realm.mongodb.RealmResultTask;
//
//public class SmsManagerTest {
//
//    private SmsHelper smsHelper;
//    private SmsManager mockSmsManager;
//    private MongoCollection<Document> mockMongoCollection;
//    private RealmResultTask<MongoCursor<Document>> mockResultTask;
//    private MongoCursor<Document> mockCursor;
//
//    @Before
//    public void setUp() {
//        smsHelper = new SmsHelper();
//        mockSmsManager = mock(SmsManager.class);
//        mockMongoCollection = mock(MongoCollection.class);
//        mockResultTask = mock(RealmResultTask.class);
//        mockCursor = mock(MongoCursor.class);
//
//        // Mock SmsManager.getDefault() to return the mocked SmsManager
//        mockStatic(SmsManager.class);
//        when(SmsManager.getDefault()).thenReturn(mockSmsManager);
//        when(mockMongoCollection.find(any(Document.class))).thenReturn((FindIterable<Document>) mockResultTask);
//    }
//
//    @Test
//    public void testSendSms() {
//        String phoneNumber = "123456789";
//        String message = "Test message";
//
//        ArrayList<String> parts = new ArrayList<>();
//        parts.add(message);
//
//        // Mock the divideMessage method to return the parts list
//        when(mockSmsManager.divideMessage(message)).thenReturn(parts);
//
//        // Call the method under test
//        SmsHelper.sendSms(phoneNumber, message);
//
//        // Capture the arguments passed to sendMultipartTextMessage
//        ArgumentCaptor<String> phoneCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<ArrayList> partsCaptor = ArgumentCaptor.forClass(ArrayList.class);
//        verify(mockSmsManager).sendMultipartTextMessage(phoneCaptor.capture(), isNull(), partsCaptor.capture(), isNull(), isNull());
//
//        // Assert the correct phone number and message parts were sent
//        assertEquals(phoneNumber, phoneCaptor.getValue());
//        assertEquals(parts, partsCaptor.getValue());
//    }
//
////    @Test
////    public void testSmsTextBuilder() {
////        Document mockDocument = new Document();
////        mockDocument.append("fullname", "John Doe");
////        mockDocument.append("tajszam", "123456789");
////        mockDocument.append("allergiak", "Nuts");
////        mockDocument.append("bloodType", 2);  // Assume 2 is for some blood type
////
////        // Mock the result task success
////        when(mockResultTask.getAsync(any())).thenAnswer(invocation -> {
////            Object callback = invocation.getArgument(0);
////            // Simulate success in fetching the document
////            callback.onSuccess(mockCursor);
////            return null;
////        });
////        when(mockCursor.hasNext()).thenReturn(true);
////        when(mockCursor.next()).thenReturn(mockDocument);
////
////        // Call the method under test
////        yourActivity.smsTextBuilder(mockMongoCollection);
////
////        // Verify the personal details string is built correctly
////        String expectedDetails = "\nTeljes név: John Doe\n" +
////                "Társadalom biztosítási szám: 123456789\n" +
////                "Allergiák: Nuts\n" +
////                "Vércsoport: " + BloodTypeEnum.getText(2);  // BloodTypeEnum.getText assumed to be mocked as well
////
////        assertEquals(expectedDetails, yourActivity.persDetails);
////    }
//}
