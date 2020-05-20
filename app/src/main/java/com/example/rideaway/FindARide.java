package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class FindARide extends AppCompatActivity implements LocationDialog.LocationDialogListener {

    TextView pickup, drop, timedate, passengers;
    public double pickuplat = 200, pickuplong = 200, droplat = 200, droplong = 200;
    ImageView minus, add, back;
    Button find;
    ArrayList<offerdetails> results;
    ProgressBar progressBar;
    ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_a_ride);

        pickup = findViewById(R.id.pickuplocation);
        drop = findViewById(R.id.droplocation);
        timedate = findViewById(R.id.timedate);
        minus = findViewById(R.id.minuspassenger);
        add = findViewById(R.id.addpassenger);
        passengers = findViewById(R.id.passengers);
        find = findViewById(R.id.findrides);
        back = findViewById(R.id.backfindaride);
        progressBar = findViewById(R.id.findarideprogressbar);
        constraintLayout=findViewById(R.id.cons6);

        results = new ArrayList<>();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(passengers.getText().toString()) == 10) {
                    MDToast.makeText(FindARide.this, "Maximum 10 passengers", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                } else {
                    passengers.setText("" + (Integer.valueOf(passengers.getText().toString()) + 1));
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(passengers.getText().toString()) == 1) {
                    MDToast.makeText(FindARide.this, "Minimum 1 passenger", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                } else {
                    passengers.setText("" + (Integer.valueOf(passengers.getText().toString()) - 1));
                }
            }
        });

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickuplong = 200;
                pickuplat = 200;
                LocationDialog dialog = new LocationDialog();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString("msg", "pickup");
                bundle.putString("ac", "findaride");
                dialog.setArguments(bundle);

                dialog.show(ft, LocationDialog.TAG);
            }
        });

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                droplong = 200;
                droplat = 200;
                LocationDialog dialog = new LocationDialog();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString("msg", "drop");
                bundle.putString("ac", "findaride");
                dialog.setArguments(bundle);

                dialog.show(ft, LocationDialog.TAG);
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find.setEnabled(false);
                results.clear();
                Animation shake = AnimationUtils.loadAnimation(FindARide.this, R.anim.shake);

                if (pickuplat == 200 || pickuplong == 200) {
                    pickup.startAnimation(shake);
                    MDToast.makeText(FindARide.this, "Select Pickup Location", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (droplat == 200 || droplong == 200) {
                    drop.startAnimation(shake);

                    MDToast.makeText(FindARide.this, "Select Drop Location", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (timedate.getText().toString().equals("Select Date And Time")) {
                    timedate.startAnimation(shake);
                    MDToast.makeText(FindARide.this, "Select Date And Time", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    find.setText("Searching Rides");

                    FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            DateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");
                            Date dateobj = null;


                            try {
                                dateobj = df.parse(timedate.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");
                            SimpleDateFormat format2 = new SimpleDateFormat("dd MMMM yyyy");


                            Log.i("date", String.valueOf(dateobj.getTime()));

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Date date = null;
                                try {
                                    date = format1.parse(dataSnapshot1.child("timeanddate").getValue(String.class));

                                    Log.i("date1", format2.format(date));
                                    Log.i("date2", df.format(dateobj));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (distance(dataSnapshot1.child("pickuplat").getValue(Double.class), dataSnapshot1.child("pickuplong").getValue(Double.class), pickuplat, pickuplong) <= 3 && distance(dataSnapshot1.child("droplat").getValue(Double.class), dataSnapshot1.child("droplong").getValue(Double.class), droplat, droplong) <= 3 && format2.format(date).equals(format2.format(dateobj)) && Integer.valueOf(dataSnapshot1.child("seats").getValue(String.class))>=Integer.valueOf(passengers.getText().toString())) {
                                    offerdetails offerdetails = new offerdetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), dataSnapshot1.child("price").getValue(String.class), dataSnapshot1.child("instant").getValue(String.class), dataSnapshot1.child("moreinfo").getValue(String.class), dataSnapshot1.child("userid").getValue(String.class), dataSnapshot1.child("pickuplat").getValue(Double.class), dataSnapshot1.child("pickuplong").getValue(Double.class), dataSnapshot1.child("droplat").getValue(Double.class), dataSnapshot1.child("droplong").getValue(Double.class));

                                    results.add(offerdetails);
                                }
                            }

                            if (results.size() > 0) {
                                Intent intent = new Intent(FindARide.this, SearchResults.class);
                                intent.putExtra("results", results);
                                intent.putExtra("from", pickup.getText().toString());
                                intent.putExtra("to", drop.getText().toString());
                                startActivity(intent);
                                customType(FindARide.this, "left-to-right");
                                finish();
                            } else {
                                find.setText("Find Rides");
                                find.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                MDToast.makeText(FindARide.this,"No Rides Found",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        timedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Select Date And Time",
                        "OK",
                        "Cancel"
                );

                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(false);
                dateTimeDialogFragment.setMinimumDateTime(Calendar.getInstance().getTime());
                dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
                dateTimeDialogFragment.setDefaultDateTime(Calendar.getInstance().getTime());

                try {
                    dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM YYYY", Locale.getDefault()));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                }

                // Set listener
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {

                        DateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

                        timedate.setText(df.format(date));
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        // Date is get on negative button click
                    }
                });

                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");

            }
        });

    }

    @Override
    public void onFinishEditDialog(double l1, double l2, String n) {
        if (n.equals("pickup")) {
            pickuplat = l1;
            pickuplong = l2;
        } else {
            droplat = l1;
            droplong = l2;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(FindARide.this, "right-to-left");
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
