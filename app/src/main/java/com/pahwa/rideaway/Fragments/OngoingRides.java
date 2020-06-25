package com.pahwa.rideaway.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
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
import com.pahwa.rideaway.Notification.Data;
import com.pahwa.rideaway.R;
import com.pahwa.rideaway.RideDetailss;
import com.pahwa.rideaway.SearchResultDetails;
import com.pahwa.rideaway.SearchResults;
import com.pahwa.rideaway.ridedetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class OngoingRides extends Fragment {


    RecyclerView recyclerView;

    ArrayList<ridedetails> rides;
    LottieAnimationView progressBar, nodata;
    TextView nodatatext;
    SimpleDateFormat df;

    public OngoingRides() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ongoing_rides, container, false);
        df= new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");
        rides = new ArrayList<>();

        recyclerView = v.findViewById(R.id.ongoingridesrecyclerview);

        progressBar = v.findViewById(R.id.ongoingprogressbar);
        nodata = v.findViewById(R.id.nodata);
        nodatatext = v.findViewById(R.id.nodatatext);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RideAdapter());


        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rides.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("userid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        String status = "No Bookings";

                        if (dataSnapshot1.child("seats").getValue(String.class).equals("0")) {
                            status = "All Booked";
                        } else if (dataSnapshot1.child("Booking Requests").exists()) {
                            if (dataSnapshot1.child("Booking Requests").getChildrenCount() == 1) {
                                status = dataSnapshot1.child("Booking Requests").getChildrenCount() + " Booking Request";
                            } else {
                                status = dataSnapshot1.child("Booking Requests").getChildrenCount() + " Booking Requests";
                            }

                        } else if (dataSnapshot1.child("Booking Confirmed").exists()) {

                            if (dataSnapshot1.child("seats").getValue(String.class).equals("0")) {
                                status = "All Booked";
                            } else if (dataSnapshot1.child("Booking Confirmed").getChildrenCount() == 1) {
                                status = dataSnapshot1.child("Booking Confirmed").getChildrenCount() + " Booking Confirmed";
                            } else {
                                status = dataSnapshot1.child("Booking Confirmed").getChildrenCount() + " Bookings Confirmed";
                            }
                        }


                        Date date= null;
                        try {
                            date = df.parse(dataSnapshot1.child("timeanddate").getValue(String.class));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), date, dataSnapshot1.child("seats").getValue(String.class), "Offered Ride", status, dataSnapshot1.getKey());

                        rides.add(ridedetails);

                    } else if (dataSnapshot1.child("Booking Requests").exists()) {
                        Date date= null;
                        try {
                            date = df.parse(dataSnapshot1.child("timeanddate").getValue(String.class));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Requests").getChildren()) {
                            Log.i("req", dataSnapshot2.getKey());
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), date, dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Requested", dataSnapshot1.getKey());

                                rides.add(ridedetails);
                            }
                        }
                    } else if (dataSnapshot1.child("Booking Confirmed").exists()) {
                        Date date= null;
                        try {
                            date = df.parse(dataSnapshot1.child("timeanddate").getValue(String.class));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Confirmed").getChildren()) {
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), date, dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Confirmed", dataSnapshot1.getKey());

                                rides.add(ridedetails);
                            }
                        }
                    }

                }

                Collections.sort(rides, new Comparator<ridedetails>() {
                    public int compare(ridedetails o1, ridedetails o2) {
                        if (o1.getDate() == null || o2.getDate() == null)
                            return 0;
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });

                Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
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
        public void onBindViewHolder(@NonNull final RideViewHolder holder, final int position) {

            final ridedetails det = rides.get(position);

            holder.status.setText(det.getStatus());
            holder.from.setText(det.getFrom());
            holder.type.setText(det.getType());
            holder.to.setText(det.getTo());


            holder.time.setText(df.format(det.getDate()));

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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imageView.setTransitionName("thumbnailTransition");
                    Pair<View, String> pair1 = Pair.create((View) holder.imageView, holder.imageView.getTransitionName());

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair1);

                    Intent intent = new Intent(getActivity(), RideDetailss.class);

                    intent.putExtra("uid", det.getUid());
                    intent.putExtra("type", det.getType());
                    intent.putExtra("class", "ongoing");
                    startActivity(intent, optionsCompat.toBundle());
                    //customType(getActivity(), "left-to-right");
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
        ImageView imageView;
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
            imageView = itemView.findViewById(R.id.imageView6);

        }
    }

}