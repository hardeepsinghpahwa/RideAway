package com.pahwa.rideaway.Notification;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import retrofit2.Call;
import retrofit2.Callback;

public class SendNoti {
    ApiService apiService;

    public void sendNotification(final Context context, String uid, final String title, final String body) {

        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String usertoken=dataSnapshot.child("token").getValue(String.class);

                Data data = new Data(title, body);
                NotificationSender sender = new NotificationSender(data, usertoken);
                apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {


                    @Override
                    public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                MDToast.makeText(context, "Failed ", MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
