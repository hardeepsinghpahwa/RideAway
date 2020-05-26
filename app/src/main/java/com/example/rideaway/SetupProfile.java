package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class SetupProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText name, phone;
    TextView dateofbirth,gotdetails;
    CheckBox male, female, other;
    TextView gendertext, filldetailstext;
    Button save;
    Animation shake;
    LinearLayout genderlayout;
    ImageView profilepicture;
    Uri image;
    String number = "";
    CardView cardView;
    LottieAnimationView lottieAnimationView;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        shake = AnimationUtils.loadAnimation(SetupProfile.this, R.anim.shake);

        number = getIntent().getStringExtra("phone");
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phonenumber);
        dateofbirth = findViewById(R.id.dateofbirth);
        male = findViewById(R.id.malecheck);
        female = findViewById(R.id.femalecheck);
        gendertext = findViewById(R.id.gendertext);
        save = findViewById(R.id.savebutton);
        other = findViewById(R.id.othercheck);
        cardView = findViewById(R.id.cardView);
        filldetailstext = findViewById(R.id.textView2);
        genderlayout = findViewById(R.id.linearLayout);
        profilepicture = findViewById(R.id.profilepicture);
        constraintLayout = findViewById(R.id.cons3);
        lottieAnimationView = findViewById(R.id.completelottie);
        gotdetails=findViewById(R.id.gotdetailstext);


        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;

            }
        });
        phone.setInputType(InputType.TYPE_NULL);
        phone.setText(number);
        dateofbirth.setInputType(InputType.TYPE_NULL);

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(SetupProfile.this)
                        .crop(6f, 6f)
                        .compress(1024)
                        .maxResultSize(540, 540)
                        .start();
            }
        });

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                hidekeyboard();
                name.clearFocus();
                if (isChecked && (female.isChecked() || other.isChecked())) {
                    female.setChecked(false);
                    other.setChecked(false);
                    male.setTextColor(Color.WHITE);
                    female.setTextColor(getColor(R.color.whitelite));
                    other.setTextColor(getColor(R.color.whitelite));

                } else if (isChecked) {
                    male.setTextColor(Color.WHITE);
                } else if (!isChecked) {
                    male.setTextColor(getColor(R.color.whitelite));
                }
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hidekeyboard();
                name.clearFocus();

                if (isChecked && (male.isChecked() || other.isChecked())) {
                    male.setChecked(false);
                    other.setChecked(false);
                    female.setTextColor(Color.WHITE);
                    male.setTextColor(getColor(R.color.whitelite));
                    other.setTextColor(getColor(R.color.whitelite));
                } else if (isChecked) {
                    female.setTextColor(Color.WHITE);
                } else if (!isChecked) {
                    female.setTextColor(getColor(R.color.whitelite));
                }
            }
        });

        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hidekeyboard();
                name.clearFocus();

                if (isChecked && (female.isChecked() || male.isChecked())) {
                    female.setChecked(false);
                    male.setChecked(false);
                    other.setTextColor(Color.WHITE);
                    female.setTextColor(getColor(R.color.whitelite));
                    male.setTextColor(getColor(R.color.whitelite));

                } else if (isChecked) {
                    other.setTextColor(Color.WHITE);
                } else if (!isChecked) {
                    other.setTextColor(getColor(R.color.whitelite));
                }
            }
        });

        dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (image == null) {
                    profilepicture.startAnimation(shake);
                    MDToast.makeText(SetupProfile.this, "Select Profile Picture", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (name.getText().toString().equals("")) {
                    name.startAnimation(shake);
                    MDToast.makeText(SetupProfile.this, "Enter Your Name", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (!male.isChecked() && !female.isChecked() && !other.isChecked()) {
                    gendertext.startAnimation(shake);
                    MDToast.makeText(SetupProfile.this, "Select Gender", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if (dateofbirth.getText().toString().equals("")) {
                    dateofbirth.startAnimation(shake);
                    MDToast.makeText(SetupProfile.this, "Select Date Of Birth", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else {
                    final AlertDialog alertDialog = new SpotsDialog.Builder()
                            .setCancelable(false)
                            .setContext(SetupProfile.this)
                            .setTheme(R.style.ProgressDialog)
                            .setMessage("Saving Your Details")
                            .build();

                    alertDialog.show();

                    final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    UploadTask uploadTask;
                    uploadTask = ref.putFile(image);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                String gender = "";
                                if (male.isChecked()) {
                                    gender = "Male";
                                } else if (female.isChecked()) {
                                    gender = "Female";
                                } else if (other.isChecked()) {
                                    gender = "Others";
                                }

                                profiledetails profiledetails = new profiledetails(name.getText().toString(), number, gender, dateofbirth.getText().toString(), downloadUri.toString());

                                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profiledetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        alertDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            name.setVisibility(View.GONE);
                                            phone.setVisibility(View.GONE);
                                            dateofbirth.setVisibility(View.GONE);
                                            genderlayout.setVisibility(View.GONE);
                                            cardView.setVisibility(View.GONE);

                                            filldetailstext.setVisibility(View.GONE);

                                            gotdetails.setVisibility(View.VISIBLE);

                                            YoYo.with(Techniques.FlipInX)
                                                    .duration(1000)
                                                    .playOn(save);

                                            save.setText("Proceed");

                                            lottieAnimationView.setVisibility(View.VISIBLE);
                                            lottieAnimationView.playAnimation();
                                            constraintLayout.setBackgroundColor(Color.WHITE);



                                            save.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(SetupProfile.this, Home.class));
                                                    customType(SetupProfile.this,"fadein-to-fadeout");
                                                }
                                            });
                                        } else {
                                            MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                        }
                                    }
                                });

                            } else {
                                alertDialog.dismiss();
                                MDToast.makeText(SetupProfile.this, "Some Error Occured. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dateofbirth.setText(currentDateString);
        dateofbirth.setTextColor(Color.WHITE);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                image = data.getData();
                profilepicture.setImageURI(image);
            }
        }
    }

    private void hidekeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
    }
}
