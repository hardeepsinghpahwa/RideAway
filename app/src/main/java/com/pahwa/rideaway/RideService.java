package com.pahwa.rideaway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahwa.rideaway.Notification.SendNoti;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RideService extends Service {

    SimpleDateFormat df;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if (dataSnapshot1.child("userid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Date date = null;
                        try {
                            date = df.parse(dataSnapshot1.child("timeanddate").getValue(String.class));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date date1 = new Date();

                        if (date1.compareTo(date)==0) {

                            SendNoti sendNott=new SendNoti();
                            sendNott.sendNotification(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getUid(),"Ride Starting Time Has Arrived!","Your Ride Starting Time has arrived.Do not Forget to update your ride status. Have a great journey");

                        }
                        else if(date1.after(date))
                        {
                            if(!dataSnapshot1.child("status").exists() )
                            {
                                long diff = date1.getTime() - date.getTime();
                                long seconds = diff / 1000;
                                long minutes = seconds / 60;
                                long hours = minutes / 60;
                                long days = hours / 24;

                                Log.i("min", String.valueOf(minutes));
                                if(minutes>5)
                                {
                                    final DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(dataSnapshot1.getKey());
                                    final DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(dataSnapshot1.getKey());
                                    fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot1) {
                                            toPath.setValue(dataSnapshot1.getValue(), new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@androidx.annotation.Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                                                    if (databaseError != null) {
                                                        System.out.println("Copy failed");
                                                    } else {
                                                        toPath.child("reason").setValue("Auto Cancelled");

                                                        fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    SendNoti sendNoti = new SendNoti();
                                                                    sendNoti.sendNotification(getApplicationContext(), dataSnapshot1.child("userid").getValue(String.class), "Ride Auto Cancelled!", "Your ride has been auto cancelled due to no status update.");

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
