package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static maes.tech.intentanim.CustomIntent.customType;

public class SearchResults extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView from, to, results;
    ArrayList<offerdetails> items;
    ArrayList<String> uids;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        items = (ArrayList<offerdetails>) getIntent().getSerializableExtra("results");
        uids=getIntent().getStringArrayListExtra("uids");

        recyclerView = findViewById(R.id.searchrecyclerview);

        from = findViewById(R.id.searchfrom);
        to = findViewById(R.id.searchto);
        back = findViewById(R.id.searchresultsback);
        results = findViewById(R.id.results);

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

    class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

        @Override
        public void onViewAttachedToWindow(@NonNull SearchViewHolder holder) {
            super.onViewAttachedToWindow(holder);

            if(recyclerView.getAdapter().getItemCount()==1) {
                results.setText(recyclerView.getAdapter().getItemCount() + " Ride Found");
            }
            else {
                results.setText(recyclerView.getAdapter().getItemCount() + " Rides Found");
            }
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchresultitem, parent, false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position) {

            offerdetails offerdetails = items.get(position);

            if(offerdetails.getSeats().equals("1"))
            {
                holder.seats.setText(offerdetails.getSeats() + " seat");
            }
            else {
                holder.seats.setText(offerdetails.getSeats() + " seats");
            }
            holder.time.setText(offerdetails.getTimeanddate());
            holder.price.setText("₹ " + offerdetails.getPrice());
            holder.from.setText(offerdetails.getPickupname());
            holder.to.setText(offerdetails.getDropname());

            FirebaseDatabase.getInstance().getReference().child("Profiles").child(offerdetails.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.name.setText(dataSnapshot.child("name").getValue(String.class));
                    Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).resize(100, 100).into(holder.pro);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imageView.setTransitionName("thumbnailTransition");
                    Pair<View, String> pair1 = Pair.create((View) holder.imageView, holder.imageView.getTransitionName());

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchResults.this, pair1);
                    Intent intent=new Intent(SearchResults.this,SearchResultDetails.class);
                    intent.putExtra("uid",uids.get(position));
                    startActivity(intent,optionsCompat.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView from, to, price, name, time, seats;
        ImageView pro, imageView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            from = itemView.findViewById(R.id.itemfrom);
            to = itemView.findViewById(R.id.itemto);
            price = itemView.findViewById(R.id.itemprice);
            name = itemView.findViewById(R.id.personoffered);
            time = itemView.findViewById(R.id.itemtime);
            seats = itemView.findViewById(R.id.itemseats);
            pro = itemView.findViewById(R.id.personofferedpro);
            imageView=itemView.findViewById(R.id.imageView4);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SearchResults.this, "right-to-left");
    }
}
