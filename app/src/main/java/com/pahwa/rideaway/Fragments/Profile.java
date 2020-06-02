package com.pahwa.rideaway.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pahwa.rideaway.MainActivity;
import com.pahwa.rideaway.R;
import com.pahwa.rideaway.SetupProfile;
import com.pahwa.rideaway.profiledetails;
import com.pahwa.rideaway.vehicledetails;
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
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {


    Button logout;
    TextView offered,found,age,gender,rating,phone,name,occupation;
    ImageView vehicles,profilepic,edit,notifications;
    ProgressBar progressBar;
    FirebaseRecyclerAdapter<vehicledetails, VehicleViewHolder> firebaseRecyclerAdapter;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);


        logout=v.findViewById(R.id.logout);
        offered=v.findViewById(R.id.profileoffered);
        found=v.findViewById(R.id.profilefound);
        age=v.findViewById(R.id.profileage);
        gender=v.findViewById(R.id.profilegender);
        rating=v.findViewById(R.id.profilerating);
        phone=v.findViewById(R.id.profilephone);
        vehicles=v.findViewById(R.id.profilevehicles);
        profilepic=v.findViewById(R.id.profilepicture);
        name=v.findViewById(R.id.profilename);
        progressBar=v.findViewById(R.id.profileprogressbar);
        occupation=v.findViewById(R.id.profileoccupation);
        edit=v.findViewById(R.id.editprofile);
        notifications=v.findViewById(R.id.notifications);

        progressBar.bringToFront();

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                profiledetails profiledetails=dataSnapshot.getValue(com.pahwa.rideaway.profiledetails.class);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), SetupProfile.class);
                        intent.putExtra("ac","edit");
                        startActivity(intent);
                        customType(getActivity(),"left-to-right");
                    }
                });

                gender.setText(profiledetails.getGender());
                Picasso.get().load(profiledetails.getImage()).resize(300,300).into(profilepic);
                phone.setText(profiledetails.getPhone());
                name.setText(profiledetails.getName());
                occupation.setText(profiledetails.getOccupation());

                if(dataSnapshot.child("offeredrides").exists())
                {
                    offered.setText(dataSnapshot.child("offeredrides").getValue(String.class));
                }
                else {
                    offered.setText("0");
                }

                if(dataSnapshot.child("foundrides").exists())
                {
                    found.setText(dataSnapshot.child("foundrides").getValue(String.class));
                }
                else {
                    found.setText("0");
                }

                DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);

                progressBar.setVisibility(View.GONE);
                try {

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(dataSnapshot.child("birthday").getValue(String.class)));

                    String a=getAge(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));

                    age.setText(a+" years");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(dataSnapshot.child("rating").exists())
                {
                    rating.setText(dataSnapshot.child("rating").getValue(String.class));
                }
                else {
                    rating.setText("0");
                }

                vehicles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText name, num;
                        final Button add;
                        final TextView no;
                        final LottieAnimationView progressBar;
                        ImageView back;
                        final Dialog dialog = new Dialog(getActivity());
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

                                holder.select.setVisibility(View.GONE);


                                holder.delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();
                                                    MDToast.makeText(getActivity(), "Vehicle Removed", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                    if(firebaseRecyclerAdapter.getItemCount()==0)
                                                    {
                                                        no.setVisibility(View.VISIBLE);
                                                        no.bringToFront();
                                                    }

                                                }
                                                else {
                                                    MDToast.makeText(getActivity(), "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

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


                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(firebaseRecyclerAdapter);
                        recyclerView.scheduleLayoutAnimation();


                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                add.setEnabled(false);
                                if (!name.getText().toString().equals("") && !num.getText().toString().equals("")) {

                                    final AlertDialog alertDialog=new SpotsDialog.Builder()
                                            .setCancelable(false)
                                            .setContext(getActivity())
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

                                            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(add.getWindowToken(), 0);

                                            if(task.isSuccessful())
                                            {
                                                no.setVisibility(View.GONE);

                                                MDToast.makeText(getActivity(),"Vehicle Added",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
                                                name.setText("");
                                                num.setText("");
                                                name.clearFocus();
                                                num.clearFocus();
                                            }
                                            else {
                                                MDToast.makeText(getActivity(),"Some Error Occured",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                                            }
                                        }
                                    });
                                }
                                else {
                                    add.setEnabled(true);
                                    MDToast.makeText(getActivity(),"Enter Details First",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                                }
                            }
                        });



                        dialog.show();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                customType(getActivity(),"left-to-right");

                MDToast.makeText(getActivity(),"Loggod Out Successfully",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
            }
        });


        return v;
    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
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
