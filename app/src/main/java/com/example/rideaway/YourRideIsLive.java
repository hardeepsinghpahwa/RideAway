package com.example.rideaway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class YourRideIsLive extends AppCompatActivity {

    Button ok;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ride_is_live);

        ok = findViewById(R.id.okaybutton);
        textView = findViewById(R.id.textView4);

        if (getIntent().getStringExtra("class") != null) {
            if (getIntent().getStringExtra("class").equals("inbook")) {

                textView.setText("Your Seats Are Booked. Get Your Bags Packed !");

            } else if (getIntent().getStringExtra("class").equals("reqbook")) {
                textView.setText("Booking Request Has Been Sent. Please Wait For The Confirmation !");

            }
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YourRideIsLive.this, Home.class);
                startActivity(intent);
                finish();
                customType(YourRideIsLive.this, "fadein-to-fadeout");
            }
        });

    }
}
