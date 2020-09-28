package com.pahwa.rideaway.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pahwa.rideaway.MainActivity;
import com.pahwa.rideaway.Notification.NotiDatabase;
import com.pahwa.rideaway.Notification.NotiDetails;
import com.pahwa.rideaway.R;
import com.pahwa.rideaway.SetupProfile;
import com.pahwa.rideaway.commisiondetails;
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

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    Button logout;
    TextView offered, found, age, gender, rating, phone, name, occupation, verifytext, ratingnum, commisionbalance;
    ImageView vehicles, profilepic, edit;
    ProgressBar progressBar;
    FirebaseRecyclerAdapter<vehicledetails, VehicleViewHolder> firebaseRecyclerAdapter;
    Button verify;
    CardView cardView;
    Uri file;
    TextView walletbalance, paymentoptions;
    Button paycommision;
    ImageView notifications;
    TextView noticount;
    Dialog dialog;
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    NestedScrollView nestedScrollView;
    NotiDatabase database;
    List<NotiDetails> details;
    RecyclerView recyclerView;
    Button addorchange;
    FirebaseRecyclerAdapter<commisiondetails, CommisionViewHolder> firebaseRecyclerAdapter2;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);


         logout = v.findViewById(R.id.logout);
        offered = v.findViewById(R.id.profileoffered);
        found = v.findViewById(R.id.profilefound);
        age = v.findViewById(R.id.profileage);
        gender = v.findViewById(R.id.profilegender);
        rating = v.findViewById(R.id.profilerating);
        phone = v.findViewById(R.id.profilephone);
        vehicles = v.findViewById(R.id.profilevehicles);
        profilepic = v.findViewById(R.id.profilepicture);
        name = v.findViewById(R.id.profilename);
        progressBar = v.findViewById(R.id.profileprogressbar);
        occupation = v.findViewById(R.id.profileoccupation);
        edit = v.findViewById(R.id.editprofile);
        cardView = v.findViewById(R.id.card20);
        verifytext = v.findViewById(R.id.verifytext);
        verify = v.findViewById(R.id.verifyprofile);
        ratingnum = v.findViewById(R.id.profileratingnum);
        nestedScrollView = v.findViewById(R.id.nestedScrollViewprofile);
        notifications = v.findViewById(R.id.notifications);
        noticount = v.findViewById(R.id.noticount);
        walletbalance = v.findViewById(R.id.commisionbalance);
        paycommision = v.findViewById(R.id.paybutton);
        addorchange = v.findViewById(R.id.profileaddchange);
        paymentoptions = v.findViewById(R.id.paymentoptions);
        commisionbalance = v.findViewById(R.id.commisionbalance);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        database = NotiDatabase.getDatabase(getActivity());

                        database.notiDao().getunread().observe(getActivity(), new Observer<List<NotiDetails>>() {
                            @Override
                            public void onChanged(List<NotiDetails> notiDetails) {

                                if (notiDetails != null) {
                                    noticount.setText(String.valueOf(notiDetails.size()));
                                } else {
                                    noticount.setText("0");
                                }
                            }
                        });

                    }
                });
            }
        });

        thread.start();


        addorchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText phone, upiid;
                final CheckBox paytm, googlepay, phonepe;
                Button save;
                ImageView cross;

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.setpaymentsdialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_rightleft);

                phone = dialog.findViewById(R.id.addpaymentphone);
                upiid = dialog.findViewById(R.id.addpaymentupi);
                paytm = dialog.findViewById(R.id.paytmcheckBox);
                googlepay = dialog.findViewById(R.id.googlepaycheckBox);
                phonepe = dialog.findViewById(R.id.phonepecheckbox);
                save = dialog.findViewById(R.id.setpaymentsavebutton);
                cross = dialog.findViewById(R.id.setpaymentcross);

                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        phone.setText((dataSnapshot.child("phone").getValue(String.class)).substring(3));

                        if (dataSnapshot.child("payment options").exists()) {
                            dataSnapshot.child("payment options").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    upiid.setText(dataSnapshot.child("upiid").getValue(String.class));
                                    if (dataSnapshot.child("Paytm").getValue(String.class).equals("1")) {
                                        paytm.setChecked(true);
                                    }

                                    if (dataSnapshot.child("PhonePe").getValue(String.class).equals("1")) {
                                        phonepe.setChecked(true);
                                    }

                                    if (dataSnapshot.child("GooglePay").getValue(String.class).equals("1")) {
                                        googlepay.setChecked(true);
                                    }

                                    upiid.setText(dataSnapshot.child("upi_id").getValue(String.class));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (phone.getText().toString().length() != 10) {
                            MDToast.makeText(getActivity(), "Phone Not Correct", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                        } else {

                            final String paytmm, phonepee, googlepayy, upi;

                            if (paytm.isChecked()) {
                                paytmm = "1";
                            } else paytmm = "0";

                            if (phonepe.isChecked()) {
                                phonepee = "1";
                            } else phonepee = "0";

                            if (googlepay.isChecked()) {
                                googlepayy = "1";
                            } else googlepayy = "0";

                            upi = upiid.getText().toString();

                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("payment options").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Map<String, Object> det = new HashMap<>();
                                    det.put("Paytm", paytmm);
                                    det.put("PhonePe", phonepee);
                                    det.put("GooglePay", googlepayy);
                                    det.put("upi_id", upi);

                                    dataSnapshot.getRef().updateChildren(det).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                MDToast.makeText(getActivity(), "Payment Details Updated", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                dialog.dismiss();
                                            } else {
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


                dialog.show();
            }
        });


        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView clear, cross;
                TextView markasread;

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.notificationdialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);


                recyclerView = dialog.findViewById(R.id.notificationrecyclerview);
                clear = dialog.findViewById(R.id.clearnotis);
                markasread = dialog.findViewById(R.id.markasread);
                cross = dialog.findViewById(R.id.notificatiocross);

                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                database = NotiDatabase.getDatabase(getActivity());

                                database.notiDao().getNotifications().observe(getActivity(), new Observer<List<NotiDetails>>() {
                                    @Override
                                    public void onChanged(List<NotiDetails> notiDetails) {
                                        details = notiDetails;
                                        if (details != null) {

                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                            linearLayoutManager.setReverseLayout(true);
                                            linearLayoutManager.setStackFromEnd(true);
                                            recyclerView.setLayoutManager(linearLayoutManager);

                                            recyclerView.setAdapter(new NotificationAdapter(details));

                                        } else {
                                            Log.i("noti", "null");
                                        }
                                    }
                                });

                            }
                        });
                    }
                });

                thread.start();


                markasread.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                database.notiDao().markallasread();

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

                dialog.show();
            }
        });

        nestedScrollView.setSmoothScrollingEnabled(true);

        progressBar.bringToFront();

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                profiledetails profiledetails = dataSnapshot.getValue(com.pahwa.rideaway.profiledetails.class);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SetupProfile.class);
                        intent.putExtra("ac", "edit");
                        startActivity(intent);
                        customType(getActivity(), "left-to-right");
                    }
                });

                if (dataSnapshot.child("verified").exists()) {
                    if (dataSnapshot.child("verified").getValue(String.class).equals("verified")) {
                        verify.setVisibility(View.GONE);
                        verifytext.setText("Verified User");
                        verifytext.setTextColor(Color.parseColor("#2196F3"));
                        verifytext.setTextSize(18);
                        verifytext.setVisibility(View.VISIBLE);

                    } else {
                        verifytext.setText("Verfication Request Sent!");
                        verify.setVisibility(View.GONE);
                        verifytext.setTextSize(18);
                        verifytext.setVisibility(View.VISIBLE);

                    }
                } else {
                    verifytext.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.VISIBLE);
                }

                if (dataSnapshot.child("walletbalance").exists()) {
                    walletbalance.setText("₹ " + dataSnapshot.child("walletbalance").getValue(String.class));
                } else {
                    walletbalance.setText("₹ 0");
                }


                if (dataSnapshot.child("payment options").exists()) {
                    dataSnapshot.child("payment options").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child("Paytm").getValue(String.class).equals("1")) {
                                paymentoptions.setText(paymentoptions.getText() + ", Paytm");
                            }

                            if (dataSnapshot.child("PhonePe").getValue(String.class).equals("1")) {
                                paymentoptions.setText(paymentoptions.getText() + ", PhonePe");
                            }

                            if (dataSnapshot.child("GooglePay").getValue(String.class).equals("1")) {
                                paymentoptions.setText(paymentoptions.getText() + ", GooglePay");
                            }

                            if (!dataSnapshot.child("upi_id").getValue(String.class).equals("")) {
                                paymentoptions.setText(paymentoptions.getText() + ", UPI");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                paycommision.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*final EditText money;
                        Button add;

                        Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.addmoneydialog);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setWindowAnimations(R.style.AppTheme_rightleft);

                        money = dialog.findViewById(R.id.moneytoadd);
                        add = dialog.findViewById(R.id.addmoneybutton);

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (money.getText().toString().equals("")) {
                                    MDToast.makeText(getActivity(), "Enter some amount first", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                } else if (Integer.parseInt(money.getText().toString()) < 10) {
                                    MDToast.makeText(getActivity(), "Minimum 10 can be added", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                } else {

                                }
                            }
                        });*/

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


                        pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                
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
                });


                gender.setText(profiledetails.getGender());
                Picasso.get().load(profiledetails.getImage()).resize(300, 300).into(profilepic);
                phone.setText(profiledetails.getPhone().substring(3));
                name.setText(profiledetails.getName());
                occupation.setText(profiledetails.getOccupation());
                if (dataSnapshot.child("commision").exists()) {
                    commisionbalance.setText("₹ " + dataSnapshot.child("commision").getValue(String.class));
                } else {
                    commisionbalance.setText("₹ 0");
                }

                if (dataSnapshot.child("offered").exists()) {
                    offered.setText(dataSnapshot.child("offered").getValue(String.class));
                } else {
                    offered.setText("0");
                }

                if (dataSnapshot.child("found").exists()) {
                    found.setText(dataSnapshot.child("found").getValue(String.class));
                } else {
                    found.setText("0");
                }

                DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);

                progressBar.setVisibility(View.GONE);
                try {

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(dataSnapshot.child("birthday").getValue(String.class)));

                    String a = getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                    age.setText(a + " years");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dataSnapshot.child("Ratings").exists()) {
                    if (dataSnapshot.child("Ratings").getChildrenCount() == 1) {
                        ratingnum.setText("( " + dataSnapshot.child("Ratings").getChildrenCount() + " rating )");
                    } else {
                        ratingnum.setText("( " + dataSnapshot.child("Ratings").getChildrenCount() + " ratings )");
                    }
                    String s = String.format("%.1f", Float.valueOf(dataSnapshot.child("rating").getValue(String.class)));

                    rating.setText(s);
                } else {
                    rating.setText("0");
                    ratingnum.setText("( No ratings )");
                }

                vehicles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vehicles.setEnabled(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vehicles.setEnabled(true);
                            }
                        }, 1000);
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
                        dialog.getWindow().setWindowAnimations(R.style.AppTheme_rightleft);

                        name = dialog.findViewById(R.id.addvehiname);
                        num = dialog.findViewById(R.id.addvehinumber);
                        add = dialog.findViewById(R.id.addvehicle);
                        no = dialog.findViewById(R.id.novehicles);
                        progressBar = dialog.findViewById(R.id.recyclerprogress);
                        back = dialog.findViewById(R.id.vehicleback);

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
                                if (!dataSnapshot.child("Vehicles").exists()) {
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
                                                if (task.isSuccessful()) {
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();
                                                    MDToast.makeText(getActivity(), "Vehicle Removed", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                    if (firebaseRecyclerAdapter.getItemCount() == 0) {
                                                        no.setVisibility(View.VISIBLE);
                                                        no.bringToFront();
                                                    }

                                                } else {
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

                                    final AlertDialog alertDialog = new SpotsDialog.Builder()
                                            .setCancelable(false)
                                            .setContext(getActivity())
                                            .setTheme(R.style.ProgressDialog)
                                            .setMessage("Adding Vehicle")
                                            .build();
                                    alertDialog.show();

                                    Map<String, String> vehicle = new HashMap<>();
                                    vehicle.put("vehiclename", name.getText().toString());
                                    vehicle.put("vehiclenumber", num.getText().toString());


                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Vehicles").child(UUID.randomUUID().toString()).setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            add.setEnabled(true);
                                            alertDialog.dismiss();

                                            recyclerView.smoothScrollToPosition(0);

                                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(add.getWindowToken(), 0);

                                            if (task.isSuccessful()) {
                                                no.setVisibility(View.GONE);

                                                MDToast.makeText(getActivity(), "Vehicle Added", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                name.setText("");
                                                num.setText("");
                                                name.clearFocus();
                                                num.clearFocus();
                                            } else {
                                                MDToast.makeText(getActivity(), "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                            }
                                        }
                                    });
                                } else {
                                    add.setEnabled(true);
                                    MDToast.makeText(getActivity(), "Enter Details First", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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

                FirebaseDatabase.getInstance().getReference().child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child("token").getRef().setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    getActivity().finish();
                                    customType(getActivity(), "left-to-right");

                                    MDToast.makeText(getActivity(), "Loggod Out Successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                                } else {
                                    MDToast.makeText(getActivity(), "Error Logging Out", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button submit;
                final TextView select;
                ImageView cross;
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.verificationdialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_rightleft);

                submit = dialog.findViewById(R.id.verifysend);
                select = dialog.findViewById(R.id.verifychoose);
                cross = dialog.findViewById(R.id.verifiycross);

                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(getFileChooserIntent(), 121);
                    }
                });

                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (file != null) {

                            final AlertDialog alertDialog = new SpotsDialog.Builder()
                                    .setCancelable(false)
                                    .setContext(getActivity())
                                    .setTheme(R.style.ProgressDialog)
                                    .setMessage("Uploading File")
                                    .build();

                            alertDialog.show();

                            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Driving Licences").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            UploadTask uploadTask;
                            uploadTask = ref.putFile(file);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        alertDialog.dismiss();
                                        MDToast.makeText(getActivity(), "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull final Task<Uri> task) {

                                    if (task.isSuccessful()) {

                                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                dataSnapshot.child("verified").getRef().setValue("requested");
                                                dataSnapshot.child("file").getRef().setValue(task.getResult().toString());
                                                alertDialog.dismiss();
                                                MDToast.makeText(getActivity(), "Verification Request Sent", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        MDToast.makeText(getActivity(), "Some Error Occured", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                    }
                                }
                            });
                        } else {
                            MDToast.makeText(getActivity(), "Choose A File First", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                        }


                    }
                });
                dialog.show();

            }
        });


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121) {
            if (data != null) {
                file = data.getData();

                TextView textView = dialog.findViewById(R.id.verifychoose);
                if (textView != null) {
                    textView.setText(getFileName(file));
                }
            }
        }
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    private class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView vehiclename, vehiclenumber;
        Button select, delete;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            vehiclename = itemView.findViewById(R.id.vehiname);
            vehiclenumber = itemView.findViewById(R.id.vehinumber);
            select = itemView.findViewById(R.id.selectvehicle);
            delete = itemView.findViewById(R.id.deletevehicle);
        }

    }

    class NotificationAdapter extends RecyclerView.Adapter<NotiViewHolder> {

        List<NotiDetails> data;

        public NotificationAdapter(List<NotiDetails> data) {
            this.data = data;
            Log.i("size", String.valueOf(data.size()));
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
                            NotiDatabase.getDatabase(getActivity()).notiDao().markasread(notiDetails1.getUid());

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
                    holder.time.setText("Today, " + ymdFormat3.format(date.getTime()));
                } else if (getCountOfDays(date, date1).equals("1")) {
                    holder.time.setText("Yesterday, " + ymdFormat3.format(date.getTime()));
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

    private Intent getFileChooserIntent() {
        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        return intent;
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


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

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

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
    }
}
