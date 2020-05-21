package com.example.rideaway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static maes.tech.intentanim.CustomIntent.customType;

public class YourRideIsLive extends AppCompatActivity {

    Button ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ride_is_live);

        ok=findViewById(R.id.okaybutton);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(YourRideIsLive.this,Home.class);
                startActivity(intent);
                finish();
                customType(YourRideIsLive.this,"fadein-to-fadeout");
            }
        });
    }
}
