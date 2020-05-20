package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static maes.tech.intentanim.CustomIntent.customType;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButton;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.DrawableParams;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.github.razir.progressbutton.ProgressParams;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity{

    EditText phone;
    ConstraintLayout constraintLayout;
    ImageView imageView;
    Button getverificationcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,Home.class));
            finish();
        }

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
}
