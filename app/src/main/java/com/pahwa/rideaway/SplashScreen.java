package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

public class SplashScreen extends AppCompatActivity {

    NetworkBroadcast networkBroadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent service = new Intent(getApplicationContext(), RideService.class);
                startService(service);

                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                networkBroadcast=new NetworkBroadcast();
                SplashScreen.this.registerReceiver(networkBroadcast, filter);

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                startActivity(new Intent(SplashScreen.this, Home.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashScreen.this, SetupProfile.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }
        }, 2000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkBroadcast);
    }
}