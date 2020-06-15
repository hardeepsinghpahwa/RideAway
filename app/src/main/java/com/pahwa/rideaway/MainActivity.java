package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;

import static maes.tech.intentanim.CustomIntent.customType;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

public class MainActivity extends AppCompatActivity{

    EditText phone;
    ConstraintLayout constraintLayout;
    ImageView imageView;
    Button getverificationcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = findViewById(R.id.phonenumber);
        imageView = findViewById(R.id.back);
        getverificationcode = findViewById(R.id.getverificationcode);


        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phone.setHint("");
                    phone.setText("+91 ");
                    Selection.setSelection(phone.getText(), phone.getText().length());
                } else
                    phone.setHint("Enter Phone Number");
            }
        });


        constraintLayout = findViewById(R.id.cons);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                phone.clearFocus();
                return false;

            }
        });


        phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91 ")) {
                    phone.setText("+91 ");
                    Selection.setSelection(phone.getText(), phone.getText().length());
                }
            }
        });

        ProgressButtonHolderKt.bindProgressButton(this, getverificationcode);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(getverificationcode);


        getverificationcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phone.getText().length() == 14) {

                    Intent intent = new Intent(MainActivity.this, PhoneVerification.class);
                    intent.putExtra("phone", phone.getText().toString());
                    startActivity(intent);
                    customType(MainActivity.this, "left-to-right");
                    finish();

                } else {
                    MDToast.makeText(MainActivity.this, "Enter a valid phone number", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });


    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKeyboard(MainActivity.this, phone);
        phone.clearFocus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(new NetworkBroadcast(), filter);
    }
}
