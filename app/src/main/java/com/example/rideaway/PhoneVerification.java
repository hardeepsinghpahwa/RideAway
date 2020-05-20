package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class PhoneVerification extends AppCompatActivity {

    EditText code1, code2, code3, code4, code5, code6;
    TextView verificationtext, sendagain, notrecieved;
    String code;
    String phone, p;
    ImageView sentcheck;
    ProgressBar progressBar,progressBar2;
    int counter = 30,a=0;
    String v_id;
    Button proceed;
    LottieAnimationView animationView;
    ConstraintLayout constraintLayout;
    PhoneAuthProvider.ForceResendingToken resend;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        phone = getIntent().getStringExtra("phone");
        p = phone.replaceAll("\\s", "");
        verificationtext = findViewById(R.id.verificationtext);
        sentcheck = findViewById(R.id.sentcheck);
        sendagain = findViewById(R.id.sendagain);
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        proceed=findViewById(R.id.proceed);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        notrecieved=findViewById(R.id.textView3);
        constraintLayout=findViewById(R.id.cons2);
        animationView=findViewById(R.id.lottie);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                return false;

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed.setEnabled(false);

                progressBar2.bringToFront();
                progressBar2.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists())
                        {
                            Intent intent=new Intent(PhoneVerification.this, Home.class);
                            startActivity(intent);
                            customType(PhoneVerification.this,"left-to-right");
                            finish();
                        }
                        else {
                            Intent intent=new Intent(PhoneVerification.this, SetupProfile.class);
                            intent.putExtra("phone",p);
                            startActivity(intent);
                            customType(PhoneVerification.this,"left-to-right");
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                code = phoneAuthCredential.getSmsCode();

                if (code!=null) {
                    code1.setText(String.valueOf(code.charAt(0)));
                    code2.setText(String.valueOf(code.charAt(1)));
                    code3.setText(String.valueOf(code.charAt(2)));
                    code4.setText(String.valueOf(code.charAt(3)));
                    code5.setText(String.valueOf(code.charAt(4)));
                    code6.setText(String.valueOf(code.charAt(5)));

                    code6.clearFocus();
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                MDToast.makeText(PhoneVerification.this, "Some Error Occured. Please Check Your Number Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                onBackPressed();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.i("token", forceResendingToken.toString());

                v_id=s;
                resend=forceResendingToken;

                progressBar.setVisibility(View.GONE);
                verificationtext.setText("Verification Code Sent");
                sentcheck.setVisibility(View.VISIBLE);

                if(a==0) {
                    new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            sendagain.setText("Send Again In " + counter);
                            counter--;
                        }

                        @Override
                        public void onFinish() {
                            sendagain.setText("Send Code Again");
                            sendagain.setBackgroundResource(R.drawable.button2);
                            sendagain.setTextColor(Color.WHITE);
                            sendagain.setPadding(20, 10, 20, 10);
                            sendagain.setClickable(true);
                            sendagain.setTextSize(20);


                            sendagain.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    a=1;
                                    sendagain.setText("Code Sent Again");
                                    sendagain.setBackgroundColor(Color.WHITE);
                                    sendagain.setTextColor(getResources().getColor(R.color.buttoncolor));
                                    sendagain.setClickable(false);
                                    sendagain.setTextSize(25);
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            p,        // Phone number to verify
                                            1,               // Timeout duration
                                            TimeUnit.MINUTES,   // Unit of timeout
                                            PhoneVerification.this,               // Activity (for callback binding)
                                            mCallBacks,         // OnVerificationStateChangedCallbacks
                                            resend);
                                }
                            });
                        }
                    }.start();
                }
            }
        };

        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code1.clearFocus();
                    code2.requestFocus();
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id,code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code2.clearFocus();
                    code3.requestFocus();
                } else {
                    code2.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length()==0) {
                                code2.clearFocus();
                                code1.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id,code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code3.clearFocus();
                    code4.requestFocus();
                } else {
                    code3.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length()==0) {
                                code3.clearFocus();
                                code2.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id,code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code4.clearFocus();
                    code5.requestFocus();
                } else {
                    code4.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length()==0) {
                                code4.clearFocus();
                                code3.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id,code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code5.clearFocus();
                    code6.requestFocus();
                } else {
                    code5.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length()==0) {
                                code5.clearFocus();
                                code4.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id,code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id,code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
                }

                if (code6.getText().toString().equals("")) {
                    code6.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length()==0) {
                                code6.clearFocus();
                                code5.requestFocus();
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (PhoneAuthProvider.getInstance() != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    p,
                    30,
                    TimeUnit.SECONDS,
                    PhoneVerification.this,
                    mCallBacks);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PhoneVerification.this,MainActivity.class));
        customType(PhoneVerification.this, "right-to-left");

    }


    private void signInWithPhoneAuthCredential(String id,String c) {

        final AlertDialog alertDialog=new SpotsDialog.Builder()
                .setCancelable(false)
                .setContext(PhoneVerification.this)
                .setTheme(R.style.ProgressDialog)
                .setMessage("Verifying Phone")
                .build();

        alertDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, c);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            MDToast.makeText(PhoneVerification.this,"Phone Number Verified",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
                            FirebaseUser user = task.getResult().getUser();

                            verificationtext.setText("Verification Complete");
                            code1.setVisibility(View.GONE);
                            code2.setVisibility(View.GONE);
                            code3.setVisibility(View.GONE);
                            code4.setVisibility(View.GONE);
                            code5.setVisibility(View.GONE);
                            code6.setVisibility(View.GONE);
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);

                            sendagain.setVisibility(View.GONE);
                            notrecieved.setVisibility(View.GONE);
                            proceed.setVisibility(View.VISIBLE);
                            animationView.setVisibility(View.VISIBLE);
                            animationView.playAnimation();



                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            MDToast.makeText(PhoneVerification.this,"Verification Failed",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                            code1.setText("");
                            code2.setText("");
                            code3.setText("");
                            code4.setText("");
                            code5.setText("");
                            code6.setText("");
                            code6.clearFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
