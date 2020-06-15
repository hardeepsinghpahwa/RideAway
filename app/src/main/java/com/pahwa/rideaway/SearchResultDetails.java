package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pahwa.rideaway.Notification.SendNoti;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
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
    TextView bookbutton, moreinfo;
    ImageView propic, call, back;
    ProgressBar progressBar;
    String cost, in;
    String seatnumber;
    ImageView imageView;
    int seat;
    String useruid;

    FirebaseRecyclerAdapter<vehicledetails, VehicleHolder> firebaseRecyclerAdapter2;
    ConstraintLayout constraintLayout;

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
        bookbutton = findViewById(R.id.searchbook);
        propic = findViewById(R.id.searchpropic);
        call = findViewById(R.id.searchcall);
        progressBar = findViewById(R.id.searchprogressbar);
        back = findViewById(R.id.searchbackbutton);
        imageView = findViewById(R.id.imageView5);
        moreinfo = findViewById(R.id.searchmoreinfo);
        constraintLayout = findViewById(R.id.cons11);

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation));
        imageView.setTransitionName("thumbnailTransition");


        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Booking Requests").exists()) {
                    dataSnapshot.child("Booking Requests").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    bookbutton.setText("Booking Request Sent");
                                    bookbutton.setBackgroundColor(getColor(R.color.yellow));
                                    bookbutton.setEnabled(false);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                if (dataSnapshot.child("Booking Confirmed").exists()) {
                    dataSnapshot.child("Booking Confirmed").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    bookbutton.setBackgroundColor(getColor(R.color.green));
                                    bookbutton.setText("Booking Already Done");
                                    bookbutton.setEnabled(false);
                                }
                            }
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

        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                final offerdetails offerdetails = dataSnapshot.getValue(com.pahwa.rideaway.offerdetails.class);
                bookbutton.setEnabled(true);

                useruid = dataSnapshot.child("userid").getValue(String.class);

                if (offerdetails != null) {
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(offerdetails.getUserid())) {
                        bookbutton.setVisibility(View.VISIBLE);
                    }


                    constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            constraintLayout.setEnabled(false);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    constraintLayout.setEnabled(true);
                                }
                            }, 1000);

                            final TextView name, phone, occupation, offered, found, gender, age, vehicletext, ratingnum;
                            final ImageView cross, propic;
                            final RatingBar ratingBar;
                            final RecyclerView recyclerView;


                            final Dialog dialog = new Dialog(SearchResultDetails.this);
                            dialog.setContentView(R.layout.profiledialog);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);

                            name = dialog.findViewById(R.id.profiledialogname);
                            phone = dialog.findViewById(R.id.profiledialogphone);
                            occupation = dialog.findViewById(R.id.profiledialogocuupation);
                            vehicletext = dialog.findViewById(R.id.profilevehicletext);
                            offered = dialog.findViewById(R.id.profiledialogoffered);
                            found = dialog.findViewById(R.id.profiledialogfound);
                            gender = dialog.findViewById(R.id.profiledialoggender);
                            age = dialog.findViewById(R.id.profiledialogage);
                            cross = dialog.findViewById(R.id.profiledialogcross);
                            propic = dialog.findViewById(R.id.profiledialogpic);
                            ratingBar = dialog.findViewById(R.id.profiledialograting);
                            recyclerView = dialog.findViewById(R.id.profiledialogrecyclerview);
                            ratingnum = dialog.findViewById(R.id.profiledialogratingnum);

                            cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    firebaseRecyclerAdapter2.stopListening();
                                }
                            });

                            Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(dataSnapshot.child("userid").getValue(String.class)).child("Vehicles");
                            FirebaseRecyclerOptions<vehicledetails> options = new FirebaseRecyclerOptions.Builder<vehicledetails>()
                                    .setQuery(query, new SnapshotParser<vehicledetails>() {
                                        @NonNull
                                        @Override
                                        public vehicledetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                            return new vehicledetails(snapshot.child("vehiclename").getValue(String.class), snapshot.child("vehiclenumber").getValue(String.class));
                                        }
                                    }).build();

                            firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<vehicledetails, VehicleHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull VehicleHolder holder, int position, @NonNull vehicledetails model) {
                                    holder.textView.setText(model.getVehiclename());
                                    Log.i("name", model.getVehiclename());
                                }

                                @NonNull
                                @Override
                                public VehicleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle, parent, false);
                                    return new VehicleHolder(v);
                                }
                            };

                            firebaseRecyclerAdapter2.startListening();


                            recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultDetails.this));
                            recyclerView.setAdapter(firebaseRecyclerAdapter2);
                            firebaseRecyclerAdapter2.notifyDataSetChanged();


                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(dataSnapshot.child("userid").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    profiledetails profiledetails = dataSnapshot.getValue(com.pahwa.rideaway.profiledetails.class);

                                    if (!dataSnapshot.child("Vehicles").exists()) {
                                        vehicletext.setText("No Vehicles Added");
                                    }

                                    if (dataSnapshot.child("Ratings").exists()) {
                                        if (dataSnapshot.child("Ratings").getChildrenCount() == 1) {
                                            ratingnum.setText("( " + dataSnapshot.child("Ratings").getChildrenCount() + " rating )");
                                        } else {
                                            ratingnum.setText("( " + dataSnapshot.child("Ratings").getChildrenCount() + " ratings )");
                                        }
                                        ratingBar.setRating(Float.valueOf(dataSnapshot.child("rating").getValue(String.class)));
                                    } else {
                                        ratingnum.setText("( No ratings )");
                                    }

                                    if (dataSnapshot.child("verified").exists()) {
                                        if (dataSnapshot.child("verified").getValue(String.class).equals("verified")) {
                                            name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(SearchResultDetails.this, R.drawable.verify), null);
                                            name.setCompoundDrawablePadding(5);
                                        }
                                    }


                                    name.setText(profiledetails.getName());
                                    phone.setText(profiledetails.getPhone());
                                    occupation.setText(profiledetails.getOccupation());
                                    gender.setText(profiledetails.getGender());
                                    if (dataSnapshot.child("offered").exists()) {
                                        offered.setText(dataSnapshot.child("offered").getValue(String.class));
                                    } else {
                                        offered.setText("0");
                                    }

                                    if (dataSnapshot.child("found").exists()) {
                                        found.setText(dataSnapshot.child("found").getValue(String.class));
                                    } else {
                                        found.setText("0");
                                    }

                                    Picasso.get().load(profiledetails.getImage()).resize(300, 300).into(propic);

                                    DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);

                                    progressBar.setVisibility(View.GONE);
                                    try {

                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(df.parse(dataSnapshot.child("birthday").getValue(String.class)));

                                        String a = getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                                        age.setText(a + " years");

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            dialog.show();
                        }
                    });


                    moreinfo.setText(offerdetails.getMoreinfo());

                    bookbutton.setText("Book ( ₹ " + offerdetails.getPrice() + " per seat )");

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

                            if (dataSnapshot.child("Ratings").exists()) {
                                String s = String.format("%.1f", Float.valueOf(dataSnapshot.child("rating").getValue(String.class)));

                                rating.setText(s);
                            } else {
                                rating.setText("0");
                            }

                            if (dataSnapshot.child("verified").exists()) {
                                if (dataSnapshot.child("verified").getValue(String.class).equals("verified")) {
                                    name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(SearchResultDetails.this, R.drawable.verify), null);
                                    name.setCompoundDrawablePadding(5);
                                }
                            }

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

        bookbutton.setOnClickListener(new View.OnClickListener() {
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
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_rightleft);

                amount = dialog.findViewById(R.id.bookamount);
                cancel = dialog.findViewById(R.id.bookcancel);
                book = dialog.findViewById(R.id.bookbutton);
                numberPicker = dialog.findViewById(R.id.bookseats);
                progressBar = dialog.findViewById(R.id.bookprogressbar);

                numberPicker.setMaxValue(seat);

                if (dialog != null) {
                    dialog.show();
                }

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

                                                    if (dataSnapshot.child("seats").getValue(String.class).equals(seatnumber)) {
                                                        SendNoti sendNoti = new SendNoti();
                                                        sendNoti.sendNotification(getApplicationContext(), useruid, "You have a Booking", "Whao! All seats of this ride are booked now. Get yourself ready for the ride.");

                                                    } else {
                                                        SendNoti sendNoti = new SendNoti();
                                                        sendNoti.sendNotification(getApplicationContext(), useruid, "You have a Booking", seatnumber + " seats for this ride are booked.");
                                                    }

                                                    FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("seats").setValue(String.valueOf(Integer.parseInt(dataSnapshot.child("seats").getValue(String.class)) - Integer.parseInt(seatnumber)));

                                                    Intent intent = new Intent(SearchResultDetails.this, YourRideIsLive.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("class", "inbook");
                                                    startActivity(intent);
                                                    finish();
                                                    customType(SearchResultDetails.this, "bottom-to-up");
                                                    dialog.dismiss();
                                                } else {

                                                    SendNoti sendNoti = new SendNoti();
                                                    sendNoti.sendNotification(getApplicationContext(), useruid, "You have a Booking Request", "A new booking request has been recieved. Please check and respond to the request.");


                                                    Intent intent = new Intent(SearchResultDetails.this, YourRideIsLive.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("class", "reqbook");
                                                    startActivity(intent);
                                                    finish();
                                                    customType(SearchResultDetails.this, "bottom-to-up");
                                                    dialog.dismiss();
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

    private class VehicleHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public VehicleHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.vehiclee);
        }
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
