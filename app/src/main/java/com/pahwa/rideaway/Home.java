package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahwa.rideaway.Fragments.HomeFragment;
import com.pahwa.rideaway.Fragments.Profile;
import com.pahwa.rideaway.Fragments.Rides;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.pahwa.rideaway.Notification.NotiDatabase;
import com.pahwa.rideaway.Notification.NotiDetails;
import com.pahwa.rideaway.Notification.SendNoti;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static maes.tech.intentanim.CustomIntent.customType;

public class Home extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    ConstraintLayout constraintLayout;
    ImageView clear, cross;
    TextView markasread, title;
    RecyclerView recyclerView;
    NotiDatabase database;
    NetworkBroadcast networkBroadcast;
    SimpleDateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        df = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

        chipNavigationBar = findViewById(R.id.bottomnav);
        constraintLayout = findViewById(R.id.cons3);

        chipNavigationBar.setItemSelected(R.id.home, true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cons3, new HomeFragment())
                .commit();
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.myrides:
                        fragment = new Rides();
                        break;
                    case R.id.profile:
                        fragment = new Profile();
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_to_right, R.anim.right_to_left)
                        .replace(R.id.cons3, fragment)
                        .commit();

            }
        });


        final Dialog dialog = new Dialog(Home.this);
        dialog.setContentView(R.layout.notificationdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);


        recyclerView = dialog.findViewById(R.id.notificationrecyclerview);
        clear = dialog.findViewById(R.id.clearnotis);
        markasread = dialog.findViewById(R.id.markasread);
        cross = dialog.findViewById(R.id.notificatiocross);
        title = dialog.findViewById(R.id.textView19);

        title.setText("You have unread Notis");
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        new AsyncTask<Void, Void, List<NotiDetails>>() {

            @Override
            protected List<NotiDetails> doInBackground(Void... voids) {
                database = NotiDatabase.getDatabase(Home.this);

                return database.notiDao().getunread1();
            }

            @Override
            protected void onPostExecute(List<NotiDetails> notiDetails) {
                super.onPostExecute(notiDetails);
                if (notiDetails != null) {
                    if (notiDetails.size() != 0) {
                        dialog.show();
                        recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                        recyclerView.setAdapter(new NotificationAdapter(notiDetails));

                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        }.execute();


        markasread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        database.notiDao().markallasread();
                        dialog.dismiss();
                        return null;
                    }
                }.execute();
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        database.notiDao().deleteall();

                        return null;
                    }
                }.execute();
            }
        });


    }


    class NotificationAdapter extends RecyclerView.Adapter<NotiViewHolder> {

        List<NotiDetails> data;

        public NotificationAdapter(List<NotiDetails> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notiitem, parent, false);
            return new NotiViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final NotiViewHolder holder, final int position) {

            final NotiDetails notiDetails = data.get(position);
            holder.body.setText(notiDetails.getBody());
            holder.title.setText(notiDetails.getTitle());

            if (notiDetails.getRead() != 1) {
                holder.unread.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotiDetails notiDetails1 = data.get(holder.getAdapterPosition());
                            NotiDatabase.getDatabase(getApplicationContext()).notiDao().markasread(notiDetails1.getUid());

                            return null;
                        }
                    }.execute();
                }
            });

            Date date, date1;

            SimpleDateFormat ymdFormat = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");

            SimpleDateFormat ymdFormat2 = new SimpleDateFormat("dd MMMM yyyy");

            SimpleDateFormat ymdFormat3 = new SimpleDateFormat("hh:mm aa");

            try {
                date = ymdFormat.parse(notiDetails.getTime());
                date1 = new Date();


                if (ymdFormat2.format(date).equals(ymdFormat2.format(date1))) {
                    holder.time.setText("Today, " + ymdFormat3.format(Calendar.getInstance().getTime()));
                } else if (getCountOfDays(date, date1).equals("1")) {
                    holder.time.setText("Yesterday, " + ymdFormat3.format(Calendar.getInstance().getTime()));
                } else {
                    holder.time.setText(notiDetails.getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class NotiViewHolder extends RecyclerView.ViewHolder {

        TextView title, body, time;
        CardView unread;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notititle);
            body = itemView.findViewById(R.id.notibody);
            unread = itemView.findViewById(R.id.unreadnoti);
            time = itemView.findViewById(R.id.notitime);
        }

    }

    public String getCountOfDays(Date createdDateString, Date expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = createdDateString;
            expireCovertedDate = expireDateString;

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        networkBroadcast = new NetworkBroadcast();
        this.registerReceiver(networkBroadcast, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkBroadcast);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Date date = null;
                    try {
                        date = df.parse(dataSnapshot1.child("timeanddate").getValue(String.class));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date date1 = new Date();

                    if (date1.after(date) && !dataSnapshot1.child("Booking Confirmed").exists()) {
                        final DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").child(dataSnapshot1.getKey());
                        final DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Rides").child("History").child(dataSnapshot1.getKey());
                        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot1) {
                                toPath.setValue(dataSnapshot1.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@androidx.annotation.Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            System.out.println("Copy failed");
                                        } else {
                                            toPath.child("reason").setValue("Auto Cancelled");

                                            fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        SendNoti sendNoti = new SendNoti();
                                                        sendNoti.sendNotification(getApplicationContext(), dataSnapshot1.child("userid").getValue(String.class), "Ride Auto Cancelled!", "Your ride has been auto cancelled due to no bookings.");

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

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Rides").child("Active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
