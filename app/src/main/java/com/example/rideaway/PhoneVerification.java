package com.example.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.concurrent.TimeUnit;

import static maes.tech.intentanim.CustomIntent.customType;

public class PhoneVerification extends AppCompatActivity {

    EditText code1, code2, code3, code4, code5, code6;
    TextView verificationtext, sendagain;
    String code, phone, p;
    ImageView sentcheck;
    ProgressBar progressBar;
    int counter = 30;
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
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        progressBar = findViewById(R.id.progressBar);

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    code1.setText(Integer.parseInt(String.valueOf(code.charAt(0))));
                    code2.setText(Integer.parseInt(String.valueOf(code.charAt(1))));
                    code3.setText(Integer.parseInt(String.valueOf(code.charAt(2))));
                    code4.setText(Integer.parseInt(String.valueOf(code.charAt(3))));
                    code5.setText(Integer.parseInt(String.valueOf(code.charAt(4))));
                    code6.setText(Integer.parseInt(String.valueOf(code.charAt(5))));
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

                progressBar.setVisibility(View.GONE);
                verificationtext.setText("Verification Code Sent");
                sentcheck.setVisibility(View.VISIBLE);

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

                        sendagain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendagain.setText("Code Sent Again");
                                sendagain.setBackgroundColor(Color.WHITE);
                                sendagain.setTextColor(getResources().getColor(R.color.buttoncolor));
                                sendagain.setClickable(false);
                            }
                        });
                    }
                }.start();
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
                    Toast.makeText(PhoneVerification.this, "Verify", Toast.LENGTH_SHORT).show();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code2.clearFocus();
                    code3.requestFocus();
                } else {
                    code2.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                code2.clearFocus();
                                code1.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    Toast.makeText(PhoneVerification.this, "Verify", Toast.LENGTH_SHORT).show();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code3.clearFocus();
                    code4.requestFocus();
                } else {
                    code3.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                code3.clearFocus();
                                code2.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    Toast.makeText(PhoneVerification.this, "Verify", Toast.LENGTH_SHORT).show();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code4.clearFocus();
                    code5.requestFocus();
                } else {
                    code4.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                code4.clearFocus();
                                code3.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    Toast.makeText(PhoneVerification.this, "Verify", Toast.LENGTH_SHORT).show();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code5.clearFocus();
                    code6.requestFocus();
                } else {
                    code5.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                code5.clearFocus();
                                code4.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    Toast.makeText(PhoneVerification.this, "Verify", Toast.LENGTH_SHORT).show();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    Toast.makeText(PhoneVerification.this, "Verify", Toast.LENGTH_SHORT).show();
                }

                if (code6.getText().toString().equals("")) {
                    code6.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
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
        customType(PhoneVerification.this, "right-to-left");

    }

}
