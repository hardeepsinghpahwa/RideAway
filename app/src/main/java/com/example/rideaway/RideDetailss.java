package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tiper.MaterialSpinner;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static maes.tech.intentanim.CustomIntent.customType;

public class RideDetailss extends AppCompatActivity {


    String uid, type;
    TextView from;
    TextView to;
    TextView time;
    TextView bookingconfirm, bookingrequests;
    TextView seats;
    TextView name;
    TextView rating, statusupdatetext;
    TextView vehicle, seatdetailstext, bookingtext;
    TextView moreinfo, cancel;
    ImageView propic, call, back;
    ProgressBar progressBar;
    String cost;
    DatabaseReference fromPath, toPath;
    ImageView imageView;
    int seat;
    MaterialSpinner ridestatus;
    Button updatestatus;
    List<String> categories;
    String st;

    FirebaseRecyclerAdapter<bookingdetails, BookingViewHolder> firebaseRecyclerAdapter;

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
        statusupdatetext = findViewById(R.id.statusupdatetext);


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
                        final offerdetails offerdetails = dataSnapshot.getValue(com.example.rideaway.offerdetails.class);

                        if (offerdetails != null) {

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


                                if (dataSnapshot.child("seats").getValue(String.class).equals("0")) {
                                    bookingrequests.setText("All Seats Booked");
                                    bookingrequests.setTextColor(getColor(R.color.green));
                                    bookingconfirm.setVisibility(View.GONE);
                                }

                                if (!dataSnapshot.child("Booking Requests").exists() && !dataSnapshot.child("Booking Requests").exists()) {
                                    bookingrequests.setText("No Bookings");
                                    bookingrequests.setTextColor(getColor(R.color.red));
                                }


                                if (dataSnapshot.child("Booking Requests").exists()) {
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
                                                            Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).resize(500, 500).into(holder.propic);
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
                                                                                        dialog.dismiss();
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

                                    if (dataSnapshot.child("seats").getValue(String.class).equals("0")) {
                                        bookingrequests.setText("All Seats Booked");
                                        bookingrequests.setTextColor(getColor(R.color.green));
                                    } else if (dataSnapshot.child("Booking Confirmed").getChildrenCount() == 1) {
                                        bookingconfirm.setVisibility(View.VISIBLE);
                                        bookingconfirm.setText(dataSnapshot.child("Booking Confirmed").getChildrenCount() + " booking confirmed");
                                    } else {
                                        bookingconfirm.setVisibility(View.VISIBLE);
                                        bookingconfirm.setText(dataSnapshot.child("Booking Confirmed").getChildrenCount() + " bookings confirmed");
                                    }
                                    bookingconfirm.setTextColor(getColor(R.color.green));

                                    bookingconfirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });


                                }
                            } else {

                                dataSnapshot.child("Booking Requests").getRef().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
                                                bookingrequests.setText("Booking Confirmed");
                                                bookingrequests.setTextColor(getColor(R.color.green));
                                                cancel.setText("Cancel Booking");


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

                                    final profiledetails profiledetails = dataSnapshot.getValue(com.example.rideaway.profiledetails.class);

                                    name.setText(profiledetails.getName());
                                    Picasso.get().load(profiledetails.getImage()).resize(100, 100).into(propic);
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
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    progressBar.setVisibility(View.GONE);
                    final offerdetails offerdetails = dataSnapshot.getValue(com.example.rideaway.offerdetails.class);

                    if (offerdetails != null) {

                        seats.setVisibility(View.GONE);
                        seatdetailstext.setVisibility(View.GONE);
                        bookingtext.setText("Status");

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

                                final profiledetails profiledetails = dataSnapshot.getValue(com.example.rideaway.profiledetails.class);

                                name.setText(profiledetails.getName());
                                Picasso.get().load(profiledetails.getImage()).resize(100, 100).into(propic);
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
}
