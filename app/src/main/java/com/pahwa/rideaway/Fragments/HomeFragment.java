package com.pahwa.rideaway.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pahwa.rideaway.FindARide;
import com.pahwa.rideaway.Notification.Data;
import com.pahwa.rideaway.OfferARide;
import com.pahwa.rideaway.R;
import com.pahwa.rideaway.commisiondetails;
import com.valdesekamdem.library.mdtoast.MDToast;

import static maes.tech.intentanim.CustomIntent.customType;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    Button findaride, offeraride;
    FirebaseRecyclerAdapter<commisiondetails, CommisionViewHolder> firebaseRecyclerAdapter2;
    int a=0;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        findaride = v.findViewById(R.id.findaride);
        offeraride = v.findViewById(R.id.offeraride);


        findaride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FindARide.class));
                customType(getActivity(), "left-to-right");
            }
        });

        offeraride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                a=0;
                FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            if(dataSnapshot1.child("userid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            {
                                if(dataSnapshot1.child("status").exists() && dataSnapshot1.child("status").getValue(String.class).equals("Ride Started"))
                                {
                                    a++;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("commision").exists() ) {
                            if (!dataSnapshot.child("commision").getValue(String.class).equals("0")) {

                                MDToast.makeText(getActivity(), "You have pending commision to pay", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();


                                TextView totalcomm;
                                RecyclerView commrecyclerView;
                                Button pay;
                                ImageView cross;

                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.commisiondialog);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);

                                commrecyclerView = dialog.findViewById(R.id.commisionrecyview);
                                cross = dialog.findViewById(R.id.commcross);
                                pay = dialog.findViewById(R.id.paycommision);
                                totalcomm = dialog.findViewById(R.id.totalcommision);

                                cross.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                if (dataSnapshot.child("commision").exists())
                                    totalcomm.setText("₹ " + dataSnapshot.child("commision").getValue(String.class));


                                Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Commisions");

                                FirebaseRecyclerOptions<commisiondetails> options = new FirebaseRecyclerOptions.Builder<commisiondetails>()
                                        .setQuery(query, new SnapshotParser<commisiondetails>() {
                                            @NonNull
                                            @Override
                                            public commisiondetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                                return new commisiondetails(snapshot.child("from").getValue(String.class), snapshot.child("to").getValue(String.class), snapshot.child("date").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.child("seats").getValue(String.class));
                                            }
                                        }).build();


                                firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<commisiondetails, CommisionViewHolder>(options) {
                                    @Override
                                    protected void onBindViewHolder(@NonNull CommisionViewHolder holder, int position, @NonNull commisiondetails model) {
                                        holder.from.setText(model.getFrom());
                                        holder.to.setText(model.getTo());
                                        holder.date.setText(model.getDate());

                                        holder.commision.setText(model.getSeats() + " X " + (0.05 * Float.valueOf(model.getTotalprice())) + " = " + (Float.valueOf(model.getSeats()) * (0.05 * Float.valueOf(model.getTotalprice()))));

                                        holder.price.setText(model.getSeats() + " X " + (model.getTotalprice()) + " = " + (Float.valueOf(model.getSeats()) * Float.valueOf(model.getTotalprice())));
                                    }

                                    @NonNull
                                    @Override
                                    public CommisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commisionitem, parent, false);
                                        return new CommisionViewHolder(view);
                                    }
                                };


                                firebaseRecyclerAdapter2.startListening();

                                commrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                commrecyclerView.setAdapter(firebaseRecyclerAdapter2);

                                dialog.show();
                            }
                            else if(a==0){
                                startActivity(new Intent(getActivity(), OfferARide.class));
                                customType(getActivity(), "left-to-right");
                            }
                            else {
                                MDToast.makeText(getActivity(),"You have imcomplete ride. First complete that and then offer another one",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                            }

                        } else if(a==0){

                            startActivity(new Intent(getActivity(), OfferARide.class));
                            customType(getActivity(), "left-to-right");
                        }
                        else {
                            MDToast.makeText(getActivity(),"You have imcomplete ride. First complete that and then offer another one",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return v;
    }

    private class CommisionViewHolder extends RecyclerView.ViewHolder {

        TextView from, to, date, price, commision;

        public CommisionViewHolder(@NonNull View itemView) {
            super(itemView);

            from = itemView.findViewById(R.id.commfrom);
            to = itemView.findViewById(R.id.commto);
            date = itemView.findViewById(R.id.commdate);
            price = itemView.findViewById(R.id.commtotalprice);
            commision = itemView.findViewById(R.id.commtotalcommision);

        }
    }

}
