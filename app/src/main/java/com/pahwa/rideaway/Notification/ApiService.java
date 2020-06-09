package com.pahwa.rideaway.Notification;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface ApiService{

@Headers(
        {
                "Content-Type:application/json",
                "Authorization:key=AAAAUXoMWQY:APA91bFydG0CFv9Mtej61AiHby7OCzF9R2zBHphS2oMNxV5g0h-ye6Ok2HNwif_CrW-9fcNwPiPP-Qs7ImINgJvsHSQ5ArTk2VD1IlRX-AqONxTTeqwzNQ7JClyrR404Qa8NFHFeN3zS"
        }
        )
@POST("fcm/send")
Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}