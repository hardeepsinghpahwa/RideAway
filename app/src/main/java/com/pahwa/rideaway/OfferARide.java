package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class OfferARide extends AppCompatActivity implements LocationDialog.LocationDialogListener {

    TextView pickup, drop, timedate, passengers, select;
    public double pickuplat = 200, pickuplong = 200, droplat = 200, droplong = 200;
    ImageView minus, add, back;
    FloatingActionButton find;
    FirebaseRecyclerAdapter<vehicledetails, VehicleViewHolder> firebaseRecyclerAdapter;
    String vname,vnumber;

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
        select = findViewById(R.id.selectvehicle);

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
                pickup.setEnabled(false);
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
                drop.setEnabled(false);
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

                        if ((new Date()).compareTo(date) < 0) {
                            DateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

                            timedate.setText(df.format(date));
                        } else {
                            MDToast.makeText(OfferARide.this, "Past Time can't be set", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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
                } else if (select.getText().toString().equals("Select")
                ) {
                    select.startAnimation(shake);
                    MDToast.makeText(OfferARide.this, "Select A Vehicle", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else {

                    Intent intent = new Intent(OfferARide.this, OfferRideDetails.class);

                    intent.putExtra("pickupname", pickup.getText().toString());
                    intent.putExtra("dropname", drop.getText().toString());
                    intent.putExtra("pickuplat", pickuplat);
                    intent.putExtra("pickuplong", pickuplong);
                    intent.putExtra("droplat", droplat);
                    intent.putExtra("droplong", droplong);
                    intent.putExtra("datetime", timedate.getText().toString());
                    intent.putExtra("seats", passengers.getText().toString());
                    intent.putExtra("vname",vname);
                    intent.putExtra("vnumber",vnumber);

                    startActivity(intent);

                    customType(OfferARide.this,"left-to-right");
                }
            }
        });


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText name, num;
                final Button add;
                final TextView no;
                final LottieAnimationView progressBar;
                ImageView back;
                final Dialog dialog = new Dialog(OfferARide.this);
                dialog.setContentView(R.layout.vehicledialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                name = dialog.findViewById(R.id.addvehiname);
                num = dialog.findViewById(R.id.addvehinumber);
                add=dialog.findViewById(R.id.addvehicle);
                no=dialog.findViewById(R.id.novehicles);
                progressBar=dialog.findViewById(R.id.recyclerprogress);
                back=dialog.findViewById(R.id.vehicleback);

                progressBar.bringToFront();

                final RecyclerView recyclerView = dialog.findViewById(R.id.vehichlerecyview);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child("Vehicles").exists())
                        {
                                no.setVisibility(View.VISIBLE);
                                no.bringToFront();
                                progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Vehicles");

                FirebaseRecyclerOptions<vehicledetails> options = new FirebaseRecyclerOptions.Builder<vehicledetails>()
                        .setQuery(query, new SnapshotParser<vehicledetails>() {
                            @NonNull
                            @Override
                            public vehicledetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new vehicledetails(snapshot.child("vehiclename").getValue(String.class), snapshot.child("vehiclenumber").getValue(String.class));
                            }
                        }).build();

                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<vehicledetails, VehicleViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final VehicleViewHolder holder, final int position, @NonNull vehicledetails model) {
                        holder.vehiclenumber.setText(model.getVehiclenumber());
                        holder.vehiclename.setText(model.getVehiclename());

                        holder.vehiclename.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                holder.select.setText("Save");
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        holder.vehiclenumber.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                holder.select.setText("Save");
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        holder.select.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(holder.select.getText().equals("Select"))
                                {
                                dialog.dismiss();


                                    select.setText(firebaseRecyclerAdapter.getItem(position).getVehiclename());
                                vname=firebaseRecyclerAdapter.getItem(position).getVehiclename();
                                vnumber=firebaseRecyclerAdapter.getItem(position).getVehiclenumber();
                            }
                            else if(holder.select.getText().equals("Save"))
                                {
                                    vehicledetails vehicledetails=new vehicledetails(holder.vehiclename.getText().toString(),holder.vehiclenumber.getText().toString());
                                    firebaseRecyclerAdapter.getRef(position).setValue(vehicledetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            holder.vehiclenumber.clearFocus();
                                            holder.vehiclename.clearFocus();
                                            if(task.isSuccessful())
                                            {
                                                MDToast.makeText(OfferARide.this, "Changes Saved", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                            }
                                            else {
                                                MDToast.makeText(OfferARide.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        holder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            firebaseRecyclerAdapter.notifyDataSetChanged();
                                            MDToast.makeText(OfferARide.this, "Vehicle Removed", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                            if(firebaseRecyclerAdapter.getItemCount()==0)
                                            {
                                                no.setVisibility(View.VISIBLE);
                                                no.bringToFront();
                                            }

                                        }
                                        else {
                                            MDToast.makeText(OfferARide.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                        }
                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicleitem, parent, false);
                        return new VehicleViewHolder(v);
                    }

                    @Override
                    public void onViewAttachedToWindow(@NonNull VehicleViewHolder holder) {
                        super.onViewAttachedToWindow(holder);


                        progressBar.setVisibility(View.GONE);


                    }
                };


                firebaseRecyclerAdapter.startListening();


                recyclerView.setLayoutManager(new LinearLayoutManager(OfferARide.this));
                recyclerView.setAdapter(firebaseRecyclerAdapter);
                recyclerView.scheduleLayoutAnimation();


                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add.setEnabled(false);
                        if (!name.getText().toString().equals("") && !num.getText().toString().equals("")) {

                            final AlertDialog alertDialog=new SpotsDialog.Builder()
                                    .setCancelable(false)
                                    .setContext(OfferARide.this)
                                    .setTheme(R.style.ProgressDialog)
                                    .setMessage("Adding Vehicle")
                                    .build();
                            alertDialog.show();

                            Map<String, String> vehicle = new HashMap<>();
                            vehicle.put("vehiclename", name.getText().toString());
                            vehicle.put("vehiclenumber",num.getText().toString());


                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Vehicles").child(UUID.randomUUID().toString()).setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    add.setEnabled(true);
                                    alertDialog.dismiss();

                                    recyclerView.smoothScrollToPosition(0);

                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(add.getWindowToken(), 0);

                                    if(task.isSuccessful())
                                    {
                                        no.setVisibility(View.GONE);

                                        MDToast.makeText(OfferARide.this,"Vehicle Added",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
                                        name.setText("");
                                        num.setText("");
                                        name.clearFocus();
                                        num.clearFocus();
                                    }
                                    else {
                                        MDToast.makeText(OfferARide.this,"Some Error Occured",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                                    }
                                }
                            });
                        }
                        else {
                            add.setEnabled(true);
                            MDToast.makeText(OfferARide.this,"Enter Details First",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                        }
                    }
                });



                dialog.show();
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
        customType(OfferARide.this, "fadein-to-fadeout");

    }

    private class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView vehiclename, vehiclenumber;
        Button select,delete;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            vehiclename = itemView.findViewById(R.id.vehiname);
            vehiclenumber = itemView.findViewById(R.id.vehinumber);
            select = itemView.findViewById(R.id.selectvehicle);
            delete=itemView.findViewById(R.id.deletevehicle);
        }
    }

}
