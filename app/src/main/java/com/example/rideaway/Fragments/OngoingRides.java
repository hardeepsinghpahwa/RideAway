package com.example.rideaway.Fragments;

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
import android.widget.TextView;

import com.example.rideaway.R;
import com.example.rideaway.ridedetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OngoingRides extends Fragment {


    RecyclerView recyclerView;

    ArrayList<ridedetails> rides;

    public OngoingRides() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ongoing_rides, container, false);

        rides = new ArrayList<>();

        recyclerView = v.findViewById(R.id.ongoingridesrecyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rides.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("userid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        String status = "No Bookings";

                        if (String.valueOf(dataSnapshot1.child("Booking Confirmed").getChildrenCount()).equals(dataSnapshot1.child("seats").getValue(String.class))) {
                            status = "All Booked";
                        } else if (dataSnapshot1.child("Booking Requests").exists()) {
                            if (dataSnapshot1.child("Booking Requests").getChildrenCount() == 1) {
                                status = dataSnapshot1.child("Booking Requests").getChildrenCount() + " Booking Request";
                            } else {
                                status = dataSnapshot1.child("Booking Requests").getChildrenCount() + " Booking Requests";
                            }

                        } else if (dataSnapshot1.child("Booking Confirmed").exists()) {

                            if (dataSnapshot1.child("Booking Confirmed").getChildrenCount() == 1) {
                                status = dataSnapshot1.child("Booking Confirmed").getChildrenCount() + " Booking Confirmed";
                            } else {
                                status = dataSnapshot1.child("Booking Confirmed").getChildrenCount() + " Bookings Confirmed";
                            }
                        }

                        ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Offered Ride", status);

                        rides.add(ridedetails);

                    }

                    if (dataSnapshot1.child("Booking Requests").exists()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Requests").getChildren()) {
                            Log.i("req",dataSnapshot2.getKey());
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Requested");

                                rides.add(ridedetails);
                            }
                        }
                    }

                    if (dataSnapshot1.child("Booking Confirmed").exists()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Confirmed").getChildren()) {
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Confirmed");

                                rides.add(ridedetails);
                            }
                        }
                    }

                }

                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setAdapter(new RideAdapter());


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

            ridedetails det = rides.get(position);

            holder.status.setText(det.getStatus());
            holder.from.setText(det.getFrom());
            holder.type.setText(det.getType());
            holder.to.setText(det.getTo());
            holder.time.setText(det.getTime());

            if (det.getSeats().equals("1")) {
                holder.seats.setText(det.getSeats() + " seat left");
            } else {
                holder.seats.setText(det.getSeats() + " seats left");
            }

            if (det.getStatus().equals("No Bookings")) {
                holder.constraintLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.redlayer));
            } else if (det.getStatus().equals("All Booked") || det.getStatus().equals("Confirmed")) {
                holder.constraintLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.greenlayer));
            } else {
                holder.constraintLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.yellowlayer));
            }
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
