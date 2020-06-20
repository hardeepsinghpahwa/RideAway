package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pahwa.rideaway.Notification.SendNoti;
import com.squareup.picasso.Picasso;
import com.tiper.MaterialSpinner;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static maes.tech.intentanim.CustomIntent.customType;

public class RideDetailss extends AppCompatActivity {


    String uid, type, userid;
    TextView from;
    TextView to;
    TextView time;
    TextView bookingconfirm, bookingrequests;
    TextView seats;
    TextView name, paymentoptions;
    TextView rating, statusupdatetext;
    TextView vehicle, seatdetailstext, bookingtext, vehiclenum, paymenttext;
    TextView moreinfo, cancel;
    ImageView propic, call, back;
    ProgressBar progressBar;
    String cost;
    ConstraintLayout constraintLayout;
    DatabaseReference fromPath, toPath;
    ImageView imageView;
    int seat;
    MaterialSpinner ridestatus;
    Button updatestatus;
    List<String> categories;
    String st;

    FirebaseRecyclerAdapter<bookingdetails, BookingViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<vehicledetails, VehicleHolder> firebaseRecyclerAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detailss);


        categories = new ArrayList<>();

        uid = getIntent().getStringExtra("uid");
        type = getIntent().getStringExtra("type");

        from = findViewById(R.id.ridedetfrom);
        to = findViewById(R.id.ridedetto);
        time = findViewById(R.id.ridedettime);
        bookingconfirm = findViewById(R.id.ridedetbooking);
        bookingrequests = findViewById(R.id.ridedetbookingreq);
        seats = findViewById(R.id.ridedetseats);
        name = findViewById(R.id.ridedetname);
        rating = findViewById(R.id.ridedetrating);
        vehicle = findViewById(R.id.ridedetvehicle);
        moreinfo = findViewById(R.id.ridedetmoreinfo);
        propic = findViewById(R.id.ridedetpropic);
        call = findViewById(R.id.ridedetcall);
        back = findViewById(R.id.ridedetbackbutton);
        progressBar = findViewById(R.id.ridedetprogreebar);
        imageView = findViewById(R.id.imageView7);
        cancel = findViewById(R.id.ridedetcancel);
        seatdetailstext = findViewById(R.id.seatdetailstext);
        bookingtext = findViewById(R.id.bookingtext);
        updatestatus = findViewById(R.id.updatestatus);
        ridestatus = findViewById(R.id.ridestatus);
        paymenttext = findViewById(R.id.paymenttext);
        statusupdatetext = findViewById(R.id.statusupdatetext);
        constraintLayout = findViewById(R.id.cons10);
        vehiclenum = findViewById(R.id.ridedetvehiclenumber);
        paymentoptions = findViewById(R.id.ridedetpaymentoptions);

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation));
        imageView.setTransitionName("thumbnailTransition");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (getIntent().getStringExtra("class").equals("ongoing")) {

            try {

                FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        final offerdetails offerdetails = dataSnapshot.getValue(com.pahwa.rideaway.offerdetails.class);

                        userid = dataSnapshot.child("userid").getValue(String.class);

                        if (offerdetails != null) {

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

                                    final Dialog dialog = new Dialog(RideDetailss.this);
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


                                    recyclerView.setLayoutManager(new LinearLayoutManager(RideDetailss.this));
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
                                                    name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(RideDetailss.this, R.drawable.verify), null);
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


                            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(offerdetails.getUserid())) {

                                cancel.setText("Cancel Ride");
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(RideDetailss.this, CancelBooking.class);
                                        intent.putExtra("type", "offered");
                                        intent.putExtra("uid", uid);
                                        startActivity(intent);
                                        customType(RideDetailss.this, "left-to-right");
                                    }
                                });

                                DateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

                                try {
                                    Date date = df.parse(offerdetails.getTimeanddate());

                                    if (new Date().compareTo(date) >= 0) {
                                        statusupdatetext.setVisibility(View.VISIBLE);
                                        ridestatus.setVisibility(View.VISIBLE);
                                        cancel.setVisibility(View.GONE);
                                    } else {
                                        cancel.setVisibility(View.VISIBLE);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (dataSnapshot.child("status").exists()) {
                                    st = dataSnapshot.child("status").getValue(String.class);
                                    if (dataSnapshot.child("status").getValue(String.class).equals("Ride Started")) {
                                        categories.clear();
                                        categories.add("Ride Started");
                                        categories.add("Ride Completed");
                                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RideDetailss.this, android.R.layout.simple_spinner_item, categories);

                                        // Drop down layout style - list view with radio button
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                        // attaching data adapter to spinner
                                        ridestatus.setAdapter(dataAdapter);

                                        ridestatus.setSelection(0);
                                    }


                                } else {
                                    categories.clear();
                                    categories.add("Ride Started");
                                    categories.add("Ride Completed");
                                    categories.add("Ride Cancelled");

                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RideDetailss.this, android.R.layout.simple_spinner_item, categories);

                                    // Drop down layout style - list view with radio button
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    // attaching data adapter to spinner
                                    ridestatus.setAdapter(dataAdapter);
                                }
                                // Creating adapter for spinner


                                ridestatus.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @org.jetbrains.annotations.Nullable View view, int i, long l) {
                                        if (materialSpinner.getSelectedItem().toString().equals("st")) {
                                            updatestatus.setVisibility(View.GONE);
                                        } else {
                                            updatestatus.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

                                    }
                                });


                                updatestatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ridestatus.getSelectedItem().toString().equals("Ride Cancelled")) {
                                            Intent intent = new Intent(RideDetailss.this, CancelBooking.class);
                                            intent.putExtra("type", "offered");
                                            intent.putExtra("uid", uid);
                                            startActivity(intent);
                                            customType(RideDetailss.this, "left-to-right");

                                        } else if (ridestatus.getSelectedItem().toString().equals("Ride Completed")) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(RideDetailss.this);
                                            builder.setTitle("Change Status To Ride Completed? This will be the final update!")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            final RadioGroup radioGroup;
                                                            final RadioButton cash, paytm, gpay, phonepe;
                                                            final Button open;
                                                            final TextView phone, upi, title;
                                                            final ImageView copyphone, copyupi, cross;

                                                            final Dialog dialog1 = new Dialog(RideDetailss.this);
                                                            dialog1.setContentView(R.layout.paymentdialog);
                                                            dialog1.setCanceledOnTouchOutside(false);
                                                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            dialog1.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                                            dialog1.getWindow().setWindowAnimations(R.style.AppTheme_Exit);

                                                            radioGroup = dialog1.findViewById(R.id.paymentradiogroup);
                                                            cash = dialog1.findViewById(R.id.paymentcash);
                                                            paytm = dialog1.findViewById(R.id.paymentpaytm);
                                                            gpay = dialog1.findViewById(R.id.paymentgooglepay);
                                                            phonepe = dialog1.findViewById(R.id.paymentphonepe);
                                                            open = dialog1.findViewById(R.id.paymentopenapp);
                                                            phone = dialog1.findViewById(R.id.paymentphone);
                                                            upi = dialog1.findViewById(R.id.paymentupi);
                                                            copyphone = dialog1.findViewById(R.id.copynumber);
                                                            copyupi = dialog1.findViewById(R.id.copyupi);
                                                            cross = dialog1.findViewById(R.id.paymentcross);
                                                            title = dialog1.findViewById(R.id.paymenttitle);

                                                            title.setText("Payment Recieved By?");

                                                            cross.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog1.dismiss();
                                                                }
                                                            });

                                                            open.setText("Select");
                                                            copyphone.setVisibility(View.GONE);
                                                            copyupi.setVisibility(View.GONE);
                                                            phone.setVisibility(View.GONE);
                                                            upi.setVisibility(View.GONE);


                                                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.child("payment options").exists()) {

                                                                        if (dataSnapshot.child("payment options").child("Paytm").getValue(String.class).equals("1")) {
                                                                            paytm.setVisibility(View.VISIBLE);
                                                                        }

                                                                        if (dataSnapshot.child("payment options").child("PhonePe").getValue(String.class).equals("1")) {
                                                                            phonepe.setVisibility(View.VISIBLE);
                                                                        }

                                                                        if (dataSnapshot.child("payment options").child("GooglePay").getValue(String.class).equals("1")) {
                                                                            gpay.setVisibility(View.VISIBLE);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });


                                                            open.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                                                                        MDToast.makeText(RideDetailss.this, "Select a payment method first", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                    } else {
                                                                        String paymentmethod = "Cash";
                                                                        switch (radioGroup.getCheckedRadioButtonId()) {
                                                                            case R.id.paymentcash:
                                                                                paymentmethod = "Cash";
                                                                                break;

                                                                            case R.id.paymentpaytm:
                                                                                paymentmethod = "Paytm";
                                                                                break;

                                                                            case R.id.paymentgooglepay:
                                                                                paymentmethod = "GooglePay";
                                                                                break;

                                                                            case R.id.paymentphonepe:
                                                                                paymentmethod = "PhonePe";
                                                                                break;


                                                                        }

                                                                        final String finalPaymentmethod = paymentmethod;
                                                                        dataSnapshot.getRef().child("status").setValue("Ride Completed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                fromPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid);
                                                                                toPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(uid);

                                                                                fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot1) {
                                                                                        toPath.setValue(dataSnapshot1.getValue(), new DatabaseReference.CompletionListener() {
                                                                                            @Override
                                                                                            public void onComplete(@androidx.annotation.Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                                                                                                if (databaseError != null) {
                                                                                                    System.out.println("Copy failed");
                                                                                                    MDToast.makeText(RideDetailss.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                    cancel.setEnabled(true);
                                                                                                } else {

                                                                                                    fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                toPath.child("status").setValue("Ride Completed");
                                                                                                                toPath.child("payment").setValue(finalPaymentmethod);
                                                                                                                toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                    @Override
                                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                                                                                                        dataSnapshot2.child("Booking Confirmed").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                            @Override
                                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {
                                                                                                                                SendNoti sendNoti = new SendNoti();
                                                                                                                                sendNoti.sendNotification(getApplicationContext(), dataSnapshot4.getKey(), "Ride Completed!", "Status Update. The Offerer has marked the ride as Completed. Don't Forget to rate the ride.");
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                            }
                                                                                                                        });

                                                                                                                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(dataSnapshot2.child("userid").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                            @Override
                                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                                                                                                if (dataSnapshot3.child("offered").exists()) {
                                                                                                                                    dataSnapshot3.child("offered").getRef().setValue(String.valueOf(Integer.parseInt(dataSnapshot3.child("offered").getValue(String.class)) + 1));
                                                                                                                                } else {
                                                                                                                                    dataSnapshot3.child("offered").getRef().setValue("1");
                                                                                                                                }
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                            }
                                                                                                                        });

                                                                                                                        toPath.child("Booking Confirmed").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                            @Override
                                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {

                                                                                                                                for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                                                                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(dataSnapshot5.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                                                                                                            if (dataSnapshot3.child("found").exists()) {
                                                                                                                                                dataSnapshot3.child("found").getRef().setValue(String.valueOf(Integer.parseInt(dataSnapshot3.child("found").getValue(String.class)) + 1));
                                                                                                                                            } else {
                                                                                                                                                dataSnapshot3.child("found").getRef().setValue("1");
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

                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                    }
                                                                                                                });

                                                                                                                progressBar.setVisibility(View.GONE);
                                                                                                                cancel.setEnabled(true);
                                                                                                                MDToast.makeText(RideDetailss.this, "Ride Completed", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();


                                                                                                                Intent intent = new Intent(RideDetailss.this, Home.class);
                                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                                                                                startActivity(intent);

                                                                                                                customType(RideDetailss.this, "fadein-to-fadeout");
                                                                                                                finish();

                                                                                                            } else {
                                                                                                                progressBar.setVisibility(View.GONE);
                                                                                                                cancel.setEnabled(true);

                                                                                                                MDToast.makeText(RideDetailss.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                            dialog1.show();
                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });


                                            builder.show();
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RideDetailss.this);
                                            builder.setTitle("Change Status To Ride Started? Now Ride can't be cancelled!")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dataSnapshot.getRef().child("status").setValue("Ride Started").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    dataSnapshot.child("Booking Confirmed").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                                                SendNoti sendNoti = new SendNoti();
                                                                                sendNoti.sendNotification(getApplicationContext(), dataSnapshot1.getKey(), "Happy Journey!", "Status Update. The Offerer has marked the ride as Started. Have a great ride.");
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                                    if (task.isSuccessful()) {
                                                                        MDToast.makeText(RideDetailss.this, "Status Updated", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                                    } else {
                                                                        MDToast.makeText(RideDetailss.this, "Status Update Failed", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();

                                        }

                                    }
                                });

                                bookingconfirm.setVisibility(View.GONE);
                                bookingrequests.setVisibility(View.GONE);

                                if (dataSnapshot.child("seats").getValue(String.class).equals("0")) {
                                    bookingconfirm.setText("All Seats Booked");
                                    bookingconfirm.setTextColor(getColor(R.color.green));
                                    bookingconfirm.setVisibility(View.VISIBLE);
                                    bookingconfirm.setVisibility(View.VISIBLE);
                                } else if (!dataSnapshot.child("Booking Requests").exists() && !dataSnapshot.child("Booking Confirmed").exists()) {
                                    bookingconfirm.setText("No Bookings");
                                    bookingconfirm.setTextColor(getColor(R.color.red));
                                    bookingconfirm.setVisibility(View.VISIBLE);
                                }


                                if (dataSnapshot.child("Booking Requests").exists()) {
                                    bookingrequests.setVisibility(View.VISIBLE);
                                    if (dataSnapshot.child("Booking Requests").getChildrenCount() == 1) {
                                        bookingrequests.setText(dataSnapshot.child("Booking Requests").getChildrenCount() + " booking request");
                                    } else {
                                        bookingrequests.setText(dataSnapshot.child("Booking Requests").getChildrenCount() + " booking requests");
                                    }
                                    bookingrequests.setTextColor(getColor(R.color.yellow));

                                    bookingrequests.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            final Dialog dialog1 = new Dialog(RideDetailss.this);
                                            dialog1.setContentView(R.layout.bookingsdialog);
                                            dialog1.setCanceledOnTouchOutside(false);
                                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog1.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                                            Query query = FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("Booking Requests");
                                            FirebaseRecyclerOptions<bookingdetails> options = new FirebaseRecyclerOptions.Builder<bookingdetails>()
                                                    .setQuery(query, new SnapshotParser<bookingdetails>() {
                                                        @NonNull
                                                        @Override
                                                        public bookingdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                                            return new bookingdetails(snapshot.child("seats").getValue(String.class), snapshot.getKey());
                                                        }
                                                    }).build();


                                            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<bookingdetails, BookingViewHolder>(options) {
                                                @Override
                                                protected void onBindViewHolder(@NonNull final BookingViewHolder holder, int position, @NonNull final bookingdetails model) {
                                                    holder.seats.setText(model.getSeats() + " seats booking");

                                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                            Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).resize(200, 200).into(holder.propic);
                                                            holder.name.setText(dataSnapshot.child("name").getValue(String.class));

                                                            holder.accept.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RideDetailss.this);
                                                                    alertDialog.setTitle("Are you sure to accept");
                                                                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(final DialogInterface dialog, int which) {

                                                                            FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                                                    if (Integer.parseInt(dataSnapshot.child("seats").getValue(String.class)) >= Integer.parseInt(model.getSeats())) {
                                                                                        fromPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("Booking Requests").child(model.getUid());
                                                                                        toPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("Booking Confirmed").child(model.getUid());

                                                                                        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                                                                toPath.setValue(dataSnapshot1.getValue(), new DatabaseReference.CompletionListener() {
                                                                                                    @Override
                                                                                                    public void onComplete(@Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                                                                                                        if (databaseError != null) {
                                                                                                            System.out.println("Copy failed");
                                                                                                            MDToast.makeText(RideDetailss.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                                                                                        } else {

                                                                                                            fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("seats").setValue(String.valueOf(Integer.parseInt(dataSnapshot.child("seats").getValue(String.class)) - Integer.parseInt(model.getSeats()))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                SendNoti sendNoti = new SendNoti();
                                                                                                                                sendNoti.sendNotification(getApplicationContext(), model.getUid(), "Yay! Booking Request Accepted", "Your ride booking request is accepted by the offerer. Get ready for the ride.");

                                                                                                                                MDToast.makeText(RideDetailss.this, "Booking Done", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                                                                                                                FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                                        if (dataSnapshot.child("seats").getValue(String.class).equals("0")) {
                                                                                                                                            if (dataSnapshot.child("Booking Requests").exists()) {
                                                                                                                                                dataSnapshot.child("Booking Requests").getRef().removeValue();
                                                                                                                                                dialog.dismiss();
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                                    }
                                                                                                                                });

                                                                                                                            }
                                                                                                                        });
                                                                                                                    } else {
                                                                                                                        MDToast.makeText(RideDetailss.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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

                                                                                    } else {
                                                                                        MDToast.makeText(RideDetailss.this, "Not Enough Seats Available Now", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                        }
                                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                                }
                                                            });

                                                            holder.reject.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RideDetailss.this);
                                                                    alertDialog.setTitle("Are you sure to reject");
                                                                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(final DialogInterface dialog, int which) {

                                                                            FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("Booking Requests").child(model.getUid()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {

                                                                                        SendNoti sendNoti = new SendNoti();
                                                                                        sendNoti.sendNotification(getApplicationContext(), model.getUid(), "Oh! Booking Request Rejected", "Your ride booking request is rejected by the offerer. Try again or find some other ride.");

                                                                                        MDToast.makeText(RideDetailss.this, "Request Rejected", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                                                    } else {
                                                                                        MDToast.makeText(RideDetailss.this, "Some Error Occurred", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                                    }
                                                                                }
                                                                            });

                                                                        }
                                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }


                                                @NonNull
                                                @Override
                                                public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookingitem, parent, false);
                                                    return new BookingViewHolder(view);
                                                }
                                            };


                                            firebaseRecyclerAdapter.startListening();
                                            TextView title;
                                            RecyclerView recyclerView;
                                            ImageView cross;


                                            title = dialog1.findViewById(R.id.bookingtitle);
                                            recyclerView = dialog1.findViewById(R.id.bookingsrecyclerview);
                                            cross = dialog1.findViewById(R.id.bookingscross);

                                            recyclerView.setLayoutManager(new LinearLayoutManager(RideDetailss.this));
                                            recyclerView.setAdapter(firebaseRecyclerAdapter);
                                            title.setText("Booking Requests");
                                            title.setTextColor(getColor(R.color.yellow));


                                            cross.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog1.dismiss();
                                                    firebaseRecyclerAdapter.stopListening();
                                                }
                                            });


                                            dialog1.show();

                                        }
                                    });
                                }

                                if (dataSnapshot.child("Booking Confirmed").exists()) {
                                    bookingconfirm.setVisibility(View.VISIBLE);

                                    if (dataSnapshot.child("seats").getValue(String.class).equals("0")) {
                                        bookingconfirm.setText("All Seats Booked");
                                        bookingconfirm.setTextColor(getColor(R.color.green));
                                    } else if (dataSnapshot.child("Booking Confirmed").getChildrenCount() == 1) {
                                        bookingconfirm.setVisibility(View.VISIBLE);
                                        bookingconfirm.setText(dataSnapshot.child("Booking Confirmed").getChildrenCount() + " booking confirmed");
                                    } else {
                                        bookingconfirm.setVisibility(View.VISIBLE);
                                        bookingconfirm.setText(dataSnapshot.child("Booking Confirmed").getChildrenCount() + " bookings confirmed");
                                    }
                                    bookingconfirm.setTextColor(getColor(R.color.green));


                                }
                            } else {

                                dataSnapshot.child("Booking Requests").getRef().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                bookingrequests.setVisibility(View.VISIBLE);
                                                bookingrequests.setText("Booking Request Sent");
                                                bookingrequests.setTextColor(getColor(R.color.yellow));
                                                cancel.setText("Cancel Booking Request");
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dataSnapshot1.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    MDToast.makeText(RideDetailss.this, "Request Cancelled", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                                    finish();
                                                                } else {
                                                                    MDToast.makeText(RideDetailss.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                dataSnapshot.child("Booking Confirmed").getRef().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                bookingrequests.setVisibility(View.VISIBLE);
                                                bookingrequests.setText("Booking Confirmed");
                                                bookingrequests.setTextColor(getColor(R.color.green));
                                                cancel.setText("Cancel Booking");
                                                cancel.setVisibility(View.VISIBLE);

                                                vehiclenum.setText(offerdetails.getVehiclenumber());
                                                vehiclenum.setVisibility(View.VISIBLE);


                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(RideDetailss.this, CancelBooking.class);
                                                        intent.putExtra("type", "found");
                                                        intent.putExtra("uid", uid);
                                                        startActivity(intent);
                                                        customType(RideDetailss.this, "left-to-right");
                                                    }
                                                });

                                                DateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

                                                try {
                                                    Date date = df.parse(offerdetails.getTimeanddate());

                                                    if (new Date().compareTo(date) >= 0) {
                                                        cancel.setVisibility(View.VISIBLE);
                                                        cancel.setText("Pay Now");
                                                        cancel.setBackgroundColor(getColor(R.color.buttoncolor));
                                                        cancel.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                final RadioGroup radioGroup;
                                                                final RadioButton cash, paytm, gpay, phonepe;
                                                                final Button open;
                                                                final TextView phone, upi;
                                                                final ImageView copyphone, copyupi, cross;

                                                                final Dialog dialog = new Dialog(RideDetailss.this);
                                                                dialog.setContentView(R.layout.paymentdialog);
                                                                dialog.setCanceledOnTouchOutside(false);
                                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                                                dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);

                                                                radioGroup = dialog.findViewById(R.id.paymentradiogroup);
                                                                cash = dialog.findViewById(R.id.paymentcash);
                                                                paytm = dialog.findViewById(R.id.paymentpaytm);
                                                                gpay = dialog.findViewById(R.id.paymentgooglepay);
                                                                phonepe = dialog.findViewById(R.id.paymentphonepe);
                                                                open = dialog.findViewById(R.id.paymentopenapp);
                                                                phone = dialog.findViewById(R.id.paymentphone);
                                                                upi = dialog.findViewById(R.id.paymentupi);
                                                                copyphone = dialog.findViewById(R.id.copynumber);
                                                                copyupi = dialog.findViewById(R.id.copyupi);
                                                                cross = dialog.findViewById(R.id.paymentcross);

                                                                cross.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });

                                                                copyupi.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                                        ClipData clip = ClipData.newPlainText("UPI", upi.getText().toString());
                                                                        clipboard.setPrimaryClip(clip);
                                                                        Toast.makeText(RideDetailss.this, "UPI Id copied", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                                copyphone.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                                        ClipData clip = ClipData.newPlainText("Phone Number", phone.getText().toString().substring(3));
                                                                        clipboard.setPrimaryClip(clip);
                                                                        Toast.makeText(RideDetailss.this, "Phone Number copied", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                                FirebaseDatabase.getInstance().getReference().child("Profiles").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.child("payment options").exists()) {

                                                                            if (dataSnapshot.child("payment options").child("Paytm").getValue(String.class).equals("1")) {
                                                                                paytm.setVisibility(View.VISIBLE);
                                                                            }

                                                                            if (dataSnapshot.child("payment options").child("PhonePe").getValue(String.class).equals("1")) {
                                                                                phonepe.setVisibility(View.VISIBLE);
                                                                            }

                                                                            if (dataSnapshot.child("payment options").child("GooglePay").getValue(String.class).equals("1")) {
                                                                                gpay.setVisibility(View.VISIBLE);
                                                                            }

                                                                            if (!dataSnapshot.child("payment options").child("upi_id").getValue(String.class).equals("")) {
                                                                                upi.setVisibility(View.VISIBLE);
                                                                                upi.setText(dataSnapshot.child("payment options").child("upi_id").getValue(String.class));
                                                                                copyupi.setVisibility(View.VISIBLE);
                                                                            }

                                                                            phone.setText((dataSnapshot.child("phone").getValue(String.class)).substring(3));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });


                                                                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                    @Override
                                                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                                        open.setVisibility(View.VISIBLE);
                                                                        if (checkedId == R.id.paymentcash) {
                                                                            open.setText("Done");
                                                                        } else {
                                                                            open.setText("Open App");
                                                                        }
                                                                    }
                                                                });

                                                                open.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        String appPackageName;
                                                                        PackageManager pm;
                                                                        Intent appstart;

                                                                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                                                                            MDToast.makeText(RideDetailss.this,"Select A Payment Option",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                                                                        } else {
                                                                            switch (radioGroup.getCheckedRadioButtonId()) {
                                                                                case R.id.paymentcash:
                                                                                    dialog.dismiss();
                                                                                    break;

                                                                                case R.id.paymentpaytm:
                                                                                    appPackageName = "net.one97.paytm";
                                                                                    pm = getPackageManager();
                                                                                    appstart = pm.getLaunchIntentForPackage(appPackageName);
                                                                                    if (null != appstart) {
                                                                                        startActivity(appstart);
                                                                                    } else {
                                                                                        MDToast.makeText(RideDetailss.this, "Install PayTm on your device", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                                    }
                                                                                    break;

                                                                                case R.id.paymentgooglepay:
                                                                                    appPackageName = "com.google.apps.nbu.paisa.user";
                                                                                    pm = getPackageManager();
                                                                                    appstart = pm.getLaunchIntentForPackage(appPackageName);
                                                                                    if (null != appstart) {
                                                                                        startActivity(appstart);
                                                                                    } else {
                                                                                        MDToast.makeText(RideDetailss.this, "Install Google Pay on your device", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                                    }
                                                                                    break;

                                                                                case R.id.paymentphonepe:
                                                                                    appPackageName = "com.phonepe.app";
                                                                                    pm = getPackageManager();
                                                                                    appstart = pm.getLaunchIntentForPackage(appPackageName);
                                                                                    if (null != appstart) {
                                                                                        startActivity(appstart);
                                                                                    } else {
                                                                                        MDToast.makeText(RideDetailss.this, "Install PhonePe on your device", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                                                    }
                                                                                    break;
                                                                            }
                                                                        }
                                                                    }
                                                                });


                                                                dialog.show();

                                                            }
                                                        });
                                                    } else {
                                                        cancel.setVisibility(View.VISIBLE);
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                            moreinfo.setText(offerdetails.getMoreinfo());


                            from.setText(offerdetails.getPickupname());
                            to.setText(offerdetails.getDropname());
                            time.setText(offerdetails.getTimeanddate());

                            seat = Integer.parseInt(offerdetails.getSeats());

                            if (offerdetails.getSeats().equals("0")) {
                                seats.setText("No seats Available");
                            } else if (offerdetails.getSeats().equals("1")) {
                                seats.setText(offerdetails.getSeats() + " seat available");
                            } else {
                                seats.setText(offerdetails.getSeats() + " seats available");
                            }
                            cost = offerdetails.getPrice();


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
                                            name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(RideDetailss.this, R.drawable.verify), null);
                                            name.setCompoundDrawablePadding(5);
                                        }
                                    }

                                    if (dataSnapshot.child("payment options").exists()) {
                                        dataSnapshot.child("payment options").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.child("Paytm").getValue(String.class).equals("1")) {
                                                    paymentoptions.setText(paymentoptions.getText() + ", Paytm");
                                                }

                                                if (dataSnapshot.child("PhonePe").getValue(String.class).equals("1")) {
                                                    paymentoptions.setText(paymentoptions.getText() + ", PhonePe");
                                                }

                                                if (dataSnapshot.child("GooglePay").getValue(String.class).equals("1")) {
                                                    paymentoptions.setText(paymentoptions.getText() + ", GooglePay");
                                                }

                                                paymentoptions.setText(paymentoptions.getText() + ", UPI");

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    name.setText(profiledetails.getName());
                                    Picasso.get().load(profiledetails.getImage()).resize(200, 200).into(propic);
                                    call.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (ActivityCompat.checkSelfPermission(RideDetailss.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                ActivityCompat.requestPermissions(RideDetailss.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

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

            } catch (NullPointerException e) {
                throw e;
            }
        } else {

            FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    userid = dataSnapshot.child("userid").getValue(String.class);

                    paymenttext.setText("Payment Done Through");

                    paymentoptions.setText(dataSnapshot.child("payment").getValue(String.class));

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

                            final Dialog dialog = new Dialog(RideDetailss.this);
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


                            recyclerView.setLayoutManager(new LinearLayoutManager(RideDetailss.this));
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
                                            name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(RideDetailss.this, R.drawable.verify), null);
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


                    progressBar.setVisibility(View.GONE);
                    final offerdetails offerdetails = dataSnapshot.getValue(com.pahwa.rideaway.offerdetails.class);

                    if (offerdetails != null) {

                        seats.setVisibility(View.GONE);
                        seatdetailstext.setVisibility(View.GONE);
                        bookingtext.setText("Status");

                        bookingrequests.setVisibility(View.VISIBLE);
                        if (dataSnapshot.child("reason").exists()) {
                            bookingrequests.setText("Cancelled");
                            bookingrequests.setTextColor(getColor(R.color.red));
                        } else {
                            bookingrequests.setText("Completed");
                            bookingrequests.setTextColor(getColor(R.color.green));
                        }

                        moreinfo.setText(offerdetails.getMoreinfo());
                        from.setText(offerdetails.getPickupname());
                        to.setText(offerdetails.getDropname());
                        time.setText(offerdetails.getTimeanddate());
                        seat = Integer.parseInt(offerdetails.getSeats());
                        cost = offerdetails.getPrice();

                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(offerdetails.getUserid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                vehicle.setText(offerdetails.getVehiclename());

                                final profiledetails profiledetails = dataSnapshot.getValue(com.pahwa.rideaway.profiledetails.class);

                                if (dataSnapshot.child("rating").exists()) {
                                    String s = String.format("%.1f", Float.valueOf(dataSnapshot.child("rating").getValue(String.class)));

                                    rating.setText(s);
                                } else {
                                    rating.setText("0");
                                }

                                if (dataSnapshot.child("verified").exists()) {
                                    if (dataSnapshot.child("verified").getValue(String.class).equals("verified")) {
                                        name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(RideDetailss.this, R.drawable.verify), null);
                                        name.setCompoundDrawablePadding(5);
                                    }
                                }

                                name.setText(profiledetails.getName());
                                Picasso.get().load(profiledetails.getImage()).resize(200, 200).into(propic);
                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ActivityCompat.checkSelfPermission(RideDetailss.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            ActivityCompat.requestPermissions(RideDetailss.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

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

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(RideDetailss.this, "right-to-left");
    }

    private class BookingViewHolder extends RecyclerView.ViewHolder {

        TextView name, seats;
        ImageView propic;
        Button accept, reject;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.bookingpersonname);
            seats = itemView.findViewById(R.id.bookingseats);
            propic = itemView.findViewById(R.id.bookingitempropic);
            accept = itemView.findViewById(R.id.bookingaccept);
            reject = itemView.findViewById(R.id.bookingreject);

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


    private class VehicleHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public VehicleHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.vehiclee);
        }
    }
}
