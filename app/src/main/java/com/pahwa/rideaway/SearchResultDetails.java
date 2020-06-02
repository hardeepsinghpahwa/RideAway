package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import it.sephiroth.android.library.numberpicker.NumberPicker;

import static maes.tech.intentanim.CustomIntent.customType;

public class SearchResultDetails extends AppCompatActivity {

    String uid;
    TextView from;
    TextView to;
    TextView time;
    TextView instant;
    TextView seats;
    TextView name;
    TextView rating;
    TextView vehicle;
    TextView book, moreinfo;
    ImageView propic, call, back;
    ProgressBar progressBar;
    String cost, in;
    String seatnumber;
    ImageView imageView;
    int seat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_details);

        uid = getIntent().getStringExtra("uid");

        from = findViewById(R.id.searchfrom);
        to = findViewById(R.id.searchto);
        time = findViewById(R.id.searchtime);
        instant = findViewById(R.id.searchinstantbook);
        seats = findViewById(R.id.searchseats);
        name = findViewById(R.id.searchname);
        rating = findViewById(R.id.searchrating);
        vehicle = findViewById(R.id.searchvehicle);
        book = findViewById(R.id.searchbook);
        propic = findViewById(R.id.searchpropic);
        call = findViewById(R.id.searchcall);
        progressBar = findViewById(R.id.searchprogressbar);
        back = findViewById(R.id.searchbackbutton);
        imageView = findViewById(R.id.imageView5);
        moreinfo = findViewById(R.id.searchmoreinfo);

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation));
        imageView.setTransitionName("thumbnailTransition");

        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                final offerdetails offerdetails = dataSnapshot.getValue(com.pahwa.rideaway.offerdetails.class);
                book.setEnabled(true);

                if (offerdetails != null) {
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(offerdetails.getUserid())) {
                        book.setVisibility(View.VISIBLE);
                    }
                    moreinfo.setText(offerdetails.getMoreinfo());

                    book.setText("Book ( ₹ " + offerdetails.getPrice() + " per seat )");

                    from.setText(offerdetails.getPickupname());
                    to.setText(offerdetails.getDropname());
                    time.setText(offerdetails.getTimeanddate());

                    seat = Integer.parseInt(offerdetails.getSeats());

                    if (offerdetails.getSeats().equals("1")) {
                        seats.setText(offerdetails.getSeats() + " seat available");
                    } else {
                        seats.setText(offerdetails.getSeats() + " seats available");
                    }
                    cost = offerdetails.getPrice();

                    if (offerdetails.getInstant().equals("no")) {
                        instant.setText("Request First");
                        in = "no";
                    } else {
                        instant.setText("Instant Booking");
                        in = "yes";
                    }

                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(offerdetails.getUserid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            vehicle.setText(offerdetails.getVehiclename());

                            final profiledetails profiledetails = dataSnapshot.getValue(com.pahwa.rideaway.profiledetails.class);

                            name.setText(profiledetails.getName());
                            Picasso.get().load(profiledetails.getImage()).resize(200, 200).into(propic);
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ActivityCompat.checkSelfPermission(SearchResultDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        ActivityCompat.requestPermissions(SearchResultDetails.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                                        return;
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + profiledetails.getPhone()));
                                        startActivity(intent);
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView amount;
                Button book;
                ImageView cancel;
                final NumberPicker numberPicker;
                final String booking;
                final ProgressBar progressBar;

                final Dialog dialog = new Dialog(SearchResultDetails.this);
                dialog.setContentView(R.layout.bookridedialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                amount = dialog.findViewById(R.id.bookamount);
                cancel = dialog.findViewById(R.id.bookcancel);
                book = dialog.findViewById(R.id.bookbutton);
                numberPicker = dialog.findViewById(R.id.bookseats);
                progressBar = dialog.findViewById(R.id.bookprogressbar);

                numberPicker.setMaxValue(seat);

                dialog.show();

                if (in.equals("no")) {
                    book.setText("Request Booking");
                    booking = "Booking Requests";
                } else {
                    book.setText("Book Instantly");
                    booking = "Booking Confirmed";
                }

                amount.setText("₹ 0");

                numberPicker.setNumberPickerChangeListener(new NumberPicker.OnNumberPickerChangeListener() {
                    @Override
                    public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
                        final Animation myAnim = AnimationUtils.loadAnimation(SearchResultDetails.this, R.anim.bounce2);
                        numberPicker.startAnimation(myAnim);

                        seatnumber = String.valueOf(i);

                        amount.setText("₹ " + Integer.parseInt(cost) * i);
                    }

                    @Override
                    public void onStartTrackingTouch(@NotNull NumberPicker numberPicker) {

                    }

                    @Override
                    public void onStopTrackingTouch(@NotNull NumberPicker numberPicker) {

                    }
                });


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("seats").getValue(String.class).equals("0")) {
                                    MDToast.makeText(SearchResultDetails.this, "No Seats Available For This Ride Now", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                } else if (seatnumber == null) {
                                    MDToast.makeText(SearchResultDetails.this, "Choose the number of seats", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                } else if (Integer.parseInt(dataSnapshot.child("seats").getValue(String.class)) < Integer.parseInt(seatnumber)) {
                                    MDToast.makeText(SearchResultDetails.this, "Only " + dataSnapshot.child("seats").getValue(String.class) + " Available Now", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                } else {
                                    progressBar.setVisibility(View.VISIBLE);

                                    Map<String, String> bookin = new HashMap<>();

                                    bookin.put("seats", seatnumber);

                                    FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child(booking).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(bookin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (in.equals("yes")) {

                                                    FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("seats").setValue(String.valueOf(Integer.parseInt(dataSnapshot.child("seats").getValue(String.class)) - Integer.parseInt(seatnumber)));

                                                    Intent intent = new Intent(SearchResultDetails.this, YourRideIsLive.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("class", "inbook");
                                                    startActivity(intent);
                                                    finish();
                                                    customType(SearchResultDetails.this, "bottom-to-up");
                                                } else {
                                                    Intent intent = new Intent(SearchResultDetails.this, YourRideIsLive.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("class", "reqbook");
                                                    startActivity(intent);
                                                    finish();
                                                    customType(SearchResultDetails.this, "bottom-to-up");

                                                }

                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                MDToast.makeText(SearchResultDetails.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MDToast.makeText(getApplicationContext(), "Permission granted", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                } else {
                    MDToast.makeText(getApplicationContext(), "Permission denied", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                return;
            }

        }
    }
}
