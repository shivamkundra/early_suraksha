package com.example.earlysuraksha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

//import com.example.earlysuraksha.Location.UploadWorker;
//import com.example.earlysuraksha.Location.WorkerUtils;

import com.example.earlysuraksha.retrofit.myservice;

import java.time.Duration;
import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {
    private WorkRequest uploadWorkRequest;
    myservice client;
    public MyReceiver() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        Log.d("receiverinside", "onReceive: "+"likh dia");
        try {
            if (bundle != null) {
                final Object[] pduArray = (Object[]) bundle.get("pdus");
                for (Object pdu : pduArray) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String senderNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Duration duration= Duration.ofSeconds(15);
                    Toast.makeText(context, senderNumber, Toast.LENGTH_SHORT).show();
                    uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                            .setInitialDelay(duration)
                            .build();

                    if (senderNumber.equals("57575701")||senderNumber.equals("+917011768133")||senderNumber.equals("+916392855172")||senderNumber.equals("5")|| senderNumber.equals("+918826609487")){
                        WorkerUtils.makeStatusNotification("Alert Danger !!",context);
                        //work manager enqueued
                        WorkManager
                                .getInstance(context)
                                .enqueue(uploadWorkRequest);

                        String phoneNumber = "1234567890";
                        String textMessage = "Sent from your Twilio trial account - This is to inform you that our system has detected severe lightning conditions in your postal region kindly take precautionary steps"; // replace with the message you want to send

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);

                        } catch (Exception e) {
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                 e.printStackTrace();
                        }

                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "oops!", Toast.LENGTH_SHORT).show();
        }
    }
}
