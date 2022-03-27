package com.nitj.nitjadminapp.firebaseNotificationJava;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFunctions {
    public static void sendNotification(PushNotification notification, Context context){
        ApiUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(@NonNull Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Notification Success", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "Notification Failed", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
