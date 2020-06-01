package com.example.rideaway.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.rideaway.R;
import com.example.rideaway.RideDetailss;
import com.example.rideaway.ridedetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static maes.tech.intentanim.CustomIntent.customType;


public class PastRides extends Fragment {


    RecyclerView recyclerView;

    ArrayList<ridedetails> rides;
    LottieAnimationView progressBar,nodata;
    TextView nodatatext;

    public PastRides() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_past_rides, container, false);

        rides = new ArrayList<>();

        recyclerView = v.findViewById(R.id.recyclerviewhistory);

        progressBar = v.findViewById(R.id.progressbarhistory);
        nodata = v.findViewById(R.id.nodatahistory);
        nodatatext = v.findViewById(R.id.nodatatexthistory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RideAdapter());


        FirebaseDatabase.getInstance().getReference().child("Rides").child("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rides.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("userid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        String status = "Cancelled";

                        if (dataSnapshot1.child("reason").exists()) {
                            ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Offered Ride", status, dataSnapshot1.getKey());

                            rides.add(ridedetails);
                        } else {
                            status = "Completed";
                            ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Offered Ride", status, dataSnapshot1.getKey());

                            rides.add(ridedetails);
                        }

                    }

                    else if (dataSnapshot1.child("Booking Requests").exists()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Requests").getChildren()) {
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                if(dataSnapshot1.child("reason").exists())
                                {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Cancelled", dataSnapshot1.getKey());

                                    rides.add(ridedetails);
                                }
                                else {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Completed", dataSnapshot1.getKey());

                                    rides.add(ridedetails);
                                }

                            }
                        }
                    }

                    else if (dataSnapshot1.child("Booking Confirmed").exists()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Confirmed").getChildren()) {
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if(dataSnapshot1.child("reason").exists())
                                {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Cancelled", dataSnapshot1.getKey());

                                    rides.add(ridedetails);
                                }
                                else {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Completed", dataSnapshot1.getKey());

                                    rides.add(ridedetails);
                                }
                            }
                        }
                    }

                }

                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();

                if (recyclerView.getAdapter().getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);
                    nodatatext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return v;
    }


    class RideAdapter extends RecyclerView.Adapter<RideViewHolder> {

        @NonNull
        @Override
        public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rideitem, parent, false);
            return new RideViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {

            final ridedetails det = rides.get(position);

            holder.status.setText(det.getStatus());
            holder.from.setText(det.getFrom());
            holder.type.setText(det.getType());
            holder.to.setText(det.getTo());
            holder.time.setText(det.getTime());

            if (det.getStatus().equals("Cancelled")) {
                holder.constraintLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.redlayer));
            } else if (det.getStatus().equals("Completed") || det.getStatus().equals("Confirmed")) {
                holder.constraintLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.greenlayer));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RideDetailss.class);

                    intent.putExtra("uid", det.getUid());
                    intent.putExtra("type", det.getType());
                    intent.putExtra("class","history");

                    startActivity(intent);
                    customType(getActivity(), "left-to-right");
                }
            });
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RideViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            progressBar.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
            nodatatext.setVisibility(View.GONE);
        }


        @Override
        public int getItemCount() {
            return rides.size();
        }
    }

    private class RideViewHolder extends RecyclerView.ViewHolder {

        TextView from, to, time, seats, type, status;
        ConstraintLayout constraintLayout;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);

            from = itemView.findViewById(R.id.ridefrom);
            to = itemView.findViewById(R.id.rideto);
            time = itemView.findViewById(R.id.ridetime);
            seats = itemView.findViewById(R.id.rideseats);
            type = itemView.findViewById(R.id.ridetype);
            status = itemView.findViewById(R.id.ridestatus);
            constraintLayout = itemView.findViewById(R.id.cons8);

        }
    }
}
