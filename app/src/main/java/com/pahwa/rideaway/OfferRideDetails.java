package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pahwa.rideaway.CalculateDistance.CalculateDistanceTime;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class OfferRideDetails extends AppCompatActivity {

    EditText price, moreinfo;
    CheckBox yes, no;
    TextView recommendedprice,instanttext;
    ConstraintLayout constraintLayout;
    Button makeoffer;
    ImageView back;
    ProgressBar progressBar;
    Animation shake;
    String distance;
    NetworkBroadcast networkBroadcast;
    String pickupname,dropname,timedate,seats,vname,vnumber;
    double pickuplat,pickuplong,droplat,droplong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride_details);


        pickuplat=getIntent().getDoubleExtra("pickuplat",0);
        pickuplong=getIntent().getDoubleExtra("pickuplong",0);
        droplat=getIntent().getDoubleExtra("droplat",0);
        droplong=getIntent().getDoubleExtra("droplong",0);
        seats=getIntent().getStringExtra("seats");
        pickupname=getIntent().getStringExtra("pickupname");
        dropname=getIntent().getStringExtra("dropname");
        timedate=getIntent().getStringExtra("datetime");
        vname=getIntent().getStringExtra("vname");
        vnumber=getIntent().getStringExtra("vnumber");

        progressBar=findViewById(R.id.makeofferprogressbar);
        price = findViewById(R.id.priceperseat);
        moreinfo = findViewById(R.id.moreinfo);
        yes = findViewById(R.id.instantbookingyes);
        no = findViewById(R.id.instantbookingno);
        recommendedprice = findViewById(R.id.recommendedprice);
        constraintLayout = findViewById(R.id.cons5);
        makeoffer = findViewById(R.id.makeoffer);
        back=findViewById(R.id.additiondetailsback);
        instanttext=findViewById(R.id.instanttext);

        shake = AnimationUtils.loadAnimation(OfferRideDetails.this, R.anim.shake);

        yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                hidekeyboard();
                price.clearFocus();
                moreinfo.clearFocus();
                if (isChecked && (no.isChecked())) {
                    no.setChecked(false);
                    yes.setTextColor(getColor(R.color.buttoncolor));
                    no.setTextColor(getColor(R.color.bluelite));

                } else if (isChecked) {
                    yes.setTextColor(getColor(R.color.buttoncolor));
                } else if (!isChecked) {
                    yes.setTextColor(getColor(R.color.bluelite));
                }
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidekeyboard();
                price.clearFocus();
                moreinfo.clearFocus();
            }
        });


        no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                hidekeyboard();
                price.clearFocus();
                moreinfo.clearFocus();
                if (isChecked && (yes.isChecked())) {
                    yes.setChecked(false);
                    no.setTextColor(getColor(R.color.buttoncolor));
                    yes.setTextColor(getColor(R.color.bluelite));

                } else if (isChecked) {
                    no.setTextColor(getColor(R.color.buttoncolor));
                } else if (!isChecked) {
                    no.setTextColor(getColor(R.color.bluelite));
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final CalculateDistanceTime distance_task = new CalculateDistanceTime(OfferRideDetails.this);

        distance_task.getDirectionsUrl(new LatLng(pickuplat,pickuplong), new LatLng(droplat,droplong));

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                distance=time_distance[0];
                Log.i("dist",distance);
            }

        });

        recommendedprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


        makeoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(price.getText().toString().length()==0)
                {
                    price.startAnimation(shake);
                    MDToast.makeText(OfferRideDetails.this,"Enter Price", MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                else if(!yes.isChecked() && !no.isChecked())
                {
                    instanttext.startAnimation(shake);
                    MDToast.makeText(OfferRideDetails.this,"Select One (Instant Booking)", MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                else if(moreinfo.getText().toString().equals(""))
                {
                    moreinfo.startAnimation(shake);
                    MDToast.makeText(OfferRideDetails.this,"Enter Some More Information", MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                else {
                    makeoffer.setEnabled(false);
                    makeoffer.setText("Working On It");

                    progressBar.setVisibility(View.VISIBLE);

                    String in="no";

                    if(yes.isChecked())
                    {
                        in="yes";
                    }
                    else if(no.isChecked()){
                        in="no";
                    }

                    offerdetails offerdetails=new offerdetails(pickupname,dropname,timedate,seats,price.getText().toString(),in,moreinfo.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getUid(),vname,vnumber,pickuplat,pickuplong,droplat,droplong);

                    FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(UUID.randomUUID().toString()).setValue(offerdetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                finish();

                                Intent intent=new Intent(OfferRideDetails.this,YourRideIsLive.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                customType(OfferRideDetails.this,"bottom-to-up");
                            }
                            else {
                                makeoffer.setEnabled(false);
                                makeoffer.setText("Make Offer");

                                progressBar.setVisibility(View.GONE);
                                MDToast.makeText(OfferRideDetails.this,"Some Error Occurred. Please Try Again",MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                            }
                        }
                    });

                }
            }
        });

    }

    private void hidekeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(OfferRideDetails.this,"right-to-left");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        networkBroadcast=new NetworkBroadcast();
        this.registerReceiver(networkBroadcast, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkBroadcast);
    }
}
