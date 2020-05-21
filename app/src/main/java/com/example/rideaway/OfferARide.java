package com.example.rideaway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class OfferARide extends AppCompatActivity implements LocationDialog.LocationDialogListener {

    TextView pickup, drop, timedate, passengers;
    public double pickuplat = 200, pickuplong = 200, droplat = 200, droplong = 200;
    ImageView minus, add, back;
    FloatingActionButton find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_a_ride);


        pickup = findViewById(R.id.pickuplocationofferaride);
        drop = findViewById(R.id.droplocationofferaride);
        timedate = findViewById(R.id.timedateofferaride);
        minus = findViewById(R.id.minuspassengerofferaride);
        add = findViewById(R.id.addpassengerofferaride);
        passengers = findViewById(R.id.passengersofferaride);
        find = findViewById(R.id.nextofferaride);
        back = findViewById(R.id.backofferaride);

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
                    MDToast.makeText(OfferARide.this, "Maximum 10 passengers", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                } else {
                    passengers.setText("" + (Integer.valueOf(passengers.getText().toString()) + 1));
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(passengers.getText().toString()) == 1) {
                    MDToast.makeText(OfferARide.this, "Minimum 1 passenger", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
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
                bundle.putString("ac", "offeraride");

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
                bundle.putString("ac", "offeraride");
                dialog.setArguments(bundle);

                dialog.show(ft, LocationDialog.TAG);
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

                        if ((new Date()).compareTo(date) <0)
                        {
                            DateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

                            timedate.setText(df.format(date));
                        }
                        else {
                            MDToast.makeText(OfferARide.this,"Past Time can't be set",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                        }

                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        // Date is get on negative button click
                    }
                });

                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");

            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake = AnimationUtils.loadAnimation(OfferARide.this, R.anim.shake);

                if (pickuplat == 200 || pickuplong == 200) {
                    pickup.startAnimation(shake);
                    MDToast.makeText(OfferARide.this, "Select Pickup Location", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (droplat == 200 || droplong == 200) {
                    drop.startAnimation(shake);

                    MDToast.makeText(OfferARide.this, "Select Drop Location", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (timedate.getText().toString().equals("Select Date And Time")) {
                    timedate.startAnimation(shake);
                    MDToast.makeText(OfferARide.this, "Select Date And Time", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else {

                    Intent intent=new Intent(OfferARide.this,OfferRideDetails.class);

                    intent.putExtra("pickupname",pickup.getText().toString());
                    intent.putExtra("dropname",drop.getText().toString());
                    intent.putExtra("pickuplat",pickuplat);
                    intent.putExtra("pickuplong",pickuplong);
                    intent.putExtra("droplat",droplat);
                    intent.putExtra("droplong",droplong);
                    intent.putExtra("datetime",timedate.getText().toString());
                    intent.putExtra("seats",passengers.getText().toString());

                    startActivity(intent);
                }
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
        customType(OfferARide.this,"right-to-left");

    }
}
