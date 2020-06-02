package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tiper.MaterialSpinner;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static maes.tech.intentanim.CustomIntent.customType;

public class CancelBooking extends AppCompatActivity {

    String ridettype,uid;
    TextView canceltext;
    MaterialSpinner materialSpinner;
    ConstraintLayout constraintLayout;
    EditText reason;
    Button cancel;
    ImageView back;
    ProgressBar progressBar;
    DatabaseReference fromPath,toPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_booking);

        ridettype=getIntent().getStringExtra("type");
        uid=getIntent().getStringExtra("uid");
        canceltext=findViewById(R.id.canceltext);
        materialSpinner=findViewById(R.id.ridestatus);
        constraintLayout=findViewById(R.id.cons9);
        reason=findViewById(R.id.reason);
        cancel=findViewById(R.id.cancelbutton);
        back=findViewById(R.id.cancelback);
        progressBar=findViewById(R.id.cancelprogressbar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
                reason.clearFocus();
            }
        });

        if(ridettype.equals("offered"))
        {
            canceltext.setText("Cancel Ride");
        }else {
            canceltext.setText("Cancel Booking");
        }

        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Spinner Drop down elements
        final List<String> categories = new ArrayList<String>();

        if(ridettype.equals("offered")) {
            categories.add("Vehicle Broke Down");
            categories.add("Some Emergency");
            categories.add("Change Of Plans");
            categories.add("Injury Or Illness");
            categories.add("Bad Weather");
            categories.add("Other");
        }
        else {
            categories.add("Some Emergency");
            categories.add("Change Of Plans");
            categories.add("Injury Or Illness");
            categories.add("Other");
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        materialSpinner.setAdapter(dataAdapter);

        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                if(ridettype.equals("offered"))
                {
                    if(i==5)
                    {
                        reason.setVisibility(View.VISIBLE);
                    }
                    else {
                        reason.setVisibility(View.GONE);
                    }
                }
                else {
                    if(i==3)
                    {
                        reason.setVisibility(View.VISIBLE);
                    }
                    else {
                        reason.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel.setEnabled(false);
                if(materialSpinner.getSelectedItem()==null)
                {
                    cancel.setEnabled(true);
                    MDToast.makeText(CancelBooking.this,"Choose A Reason First",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (ridettype.equals("offered")) {
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
                                            MDToast.makeText(CancelBooking.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                            progressBar.setVisibility(View.GONE);
                                            cancel.setEnabled(true);
                                        } else {

                                            fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressBar.setVisibility(View.GONE);
                                                        cancel.setEnabled(true);
                                                        MDToast.makeText(CancelBooking.this, "Cancellation Succesful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                                        databaseReference.child("reason").setValue(materialSpinner.getSelectedItem().toString());

                                                        Intent intent=new Intent(CancelBooking.this,Home.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                        startActivity(intent);

                                                        customType(CancelBooking.this,"fadein-to-fadeout");
                                                        finish();

                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        cancel.setEnabled(true);

                                                        MDToast.makeText(CancelBooking.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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
                    else {
                        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(uid).child("Booking Confirmed").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    cancel.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    MDToast.makeText(CancelBooking.this, "Booking Cancelled", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                    Intent intent=new Intent(CancelBooking.this,Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);
                                    customType(CancelBooking.this,"fadein-to-fadeout");
                                }
                                else {
                                    MDToast.makeText(CancelBooking.this, "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                    progressBar.setVisibility(View.GONE);
                                    cancel.setEnabled(true);

                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(CancelBooking.this,"right-to-left");
    }
}
