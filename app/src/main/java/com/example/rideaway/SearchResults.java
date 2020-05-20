package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class SearchResults extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView from,to;
    ArrayList<offerdetails> items;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        items= (ArrayList<offerdetails>) getIntent().getSerializableExtra("results");

        recyclerView=findViewById(R.id.searchrecyclerview);

        from=findViewById(R.id.searchfrom);
        to=findViewById(R.id.searchto);
        back=findViewById(R.id.searchresultsback);

        from.setText(getIntent().getStringExtra("from"));
        to.setText(getIntent().getStringExtra("to"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.setAdapter(new SearchAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResults.this));
    }

    class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>
    {

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.searchresultitem,parent,false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SearchViewHolder holder, int position) {

            offerdetails offerdetails=items.get(0);

            holder.seats.setText(offerdetails.getSeats()+" seats");
            holder.time.setText(offerdetails.getTimeanddate());
            holder.price.setText("₹ "+offerdetails.getPrice());
            holder.from.setText(offerdetails.getPickupname());
            holder.to.setText(offerdetails.getDropname());

            FirebaseDatabase.getInstance().getReference().child("Profiles").child(offerdetails.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.name.setText(dataSnapshot.child("name").getValue(String.class));
                    Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).resize(80,80).into(holder.pro);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView from,to,price,name,time,seats;
        ImageView pro;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            from=itemView.findViewById(R.id.itemfrom);
            to=itemView.findViewById(R.id.itemto);
            price=itemView.findViewById(R.id.itemprice);
            name=itemView.findViewById(R.id.personoffered);
            time=itemView.findViewById(R.id.itemtime);
            seats=itemView.findViewById(R.id.itemseats);
            pro=itemView.findViewById(R.id.personofferedpro);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SearchResults.this,"right-to-left");
    }
}
