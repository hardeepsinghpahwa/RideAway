package com.pahwa.rideaway.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pahwa.rideaway.R;
import com.pahwa.rideaway.RideDetailss;
import com.pahwa.rideaway.ridedetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;

import static maes.tech.intentanim.CustomIntent.customType;


public class PastRides extends Fragment {


    RecyclerView recyclerView;

    ArrayList<ridedetails> rides;
    LottieAnimationView progressBar, nodata;
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
                            ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Offered Ride", status, dataSnapshot1.getKey(), dataSnapshot1.child("userid").getValue(String.class));

                            rides.add(ridedetails);
                        } else {
                            status = "Completed";
                            ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Offered Ride", status, dataSnapshot1.getKey(), dataSnapshot1.child("userid").getValue(String.class));

                            rides.add(ridedetails);
                        }

                    } else if (dataSnapshot1.child("Booking Requests").exists()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Requests").getChildren()) {
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                if (dataSnapshot1.child("reason").exists()) {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Cancelled", dataSnapshot1.getKey(), dataSnapshot1.child("userid").getValue(String.class));

                                    rides.add(ridedetails);
                                } else {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Completed", dataSnapshot1.getKey(), dataSnapshot1.child("userid").getValue(String.class));

                                    rides.add(ridedetails);
                                }

                            }
                        }
                    } else if (dataSnapshot1.child("Booking Confirmed").exists()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Booking Confirmed").getChildren()) {
                            if (dataSnapshot2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if (dataSnapshot1.child("reason").exists()) {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Cancelled", dataSnapshot1.getKey(), dataSnapshot1.child("userid").getValue(String.class));

                                    rides.add(ridedetails);
                                } else {
                                    ridedetails ridedetails = new ridedetails(dataSnapshot1.child("pickupname").getValue(String.class), dataSnapshot1.child("dropname").getValue(String.class), dataSnapshot1.child("timeanddate").getValue(String.class), dataSnapshot1.child("seats").getValue(String.class), "Found Ride", "Completed", dataSnapshot1.getKey(), dataSnapshot1.child("userid").getValue(String.class));

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
        public void onBindViewHolder(@NonNull final RideViewHolder holder, int position) {

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

            if (det.getStatus().equals("Completed") && det.getType().equals("Found Ride")) {
                holder.rate.setVisibility(View.VISIBLE);
            }

            FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(det.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("rating").exists()) {
                        holder.rate.setEnabled(false);
                        holder.rate.setText(dataSnapshot.child("rating").getValue(String.class));
                        holder.rate.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.check, null),null, null, null);
                    }
                    else {
                        holder.rate.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.ratedialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    final RatingBar ratingBar;
                    final Button rate;
                    ImageView cross;
                    final ProgressBar progressBar;

                    ratingBar = dialog.findViewById(R.id.ratingBarrate);
                    rate = dialog.findViewById(R.id.ratebuttonrate);
                    cross = dialog.findViewById(R.id.ratecross);
                    progressBar = dialog.findViewById(R.id.rateprogressbar);

                    rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (ratingBar.getRating() == 0) {
                                MDToast.makeText(getActivity(), "Give Some Rating First", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            } else {
                                rate.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                Log.i("uid", det.getUserid());
                                FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(det.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(det.getUid()).child("rating").setValue(String.valueOf(ratingBar.getRating()));

                                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(det.getUserid()).child("Ratings").child(det.getUid()).setValue(String.valueOf(ratingBar.getRating())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(det.getUserid()).child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            float rat=0;

                                                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                                                rat=rat+Float.valueOf(dataSnapshot.child(dataSnapshot1.getKey()).getValue(String.class));
                                                            }

                                                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(det.getUserid()).child("rating").setValue(String.valueOf(rat/dataSnapshot.getChildrenCount()));
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    progressBar.setVisibility(View.GONE);
                                                    rate.setEnabled(true);
                                                    MDToast.makeText(getActivity(), "Rating Submitted!", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                    dialog.dismiss();
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    rate.setEnabled(true);
                                                    MDToast.makeText(getActivity(), "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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
                    });

                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imageView.setTransitionName("thumbnailTransition");

                    Intent intent = new Intent(getActivity(), RideDetailss.class);

                    Pair<View, String> pair1 = Pair.create((View) holder.imageView, holder.imageView.getTransitionName());

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair1);


                    intent.putExtra("uid", det.getUid());
                    intent.putExtra("type", det.getType());
                    intent.putExtra("class", "history");

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
        ConstraintLayout constraintLayout;
        ImageView imageView;
        Button rate;

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
            rate = itemView.findViewById(R.id.ratebutton);

        }
    }
}
