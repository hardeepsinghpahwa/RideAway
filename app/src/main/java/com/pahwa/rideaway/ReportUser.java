package com.pahwa.rideaway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class ReportUser extends AppCompatActivity {


    TextView reporttitle;
    EditText reportreason;
    Button submit;
    ImageView back;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);


        reportreason=findViewById(R.id.reportreason);
        reporttitle=findViewById(R.id.reporttitle);
        submit=findViewById(R.id.reportsubmit);
        back=findViewById(R.id.reportback);
        progressBar=findViewById(R.id.reportprogressbar);

        reporttitle.setText("Report "+getIntent().getStringExtra("name"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reportreason.getText().toString().equals(""))
                {
                    MDToast.makeText(ReportUser.this,"Enter some reason",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    Map<String,String> det=new HashMap<>();
                    det.put("by", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    det.put("reason",reportreason.getText().toString());

                    FirebaseDatabase.getInstance().getReference().child("Reported Users").child(getIntent().getStringExtra("uid")).child(UUID.randomUUID().toString()).setValue(det).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                finish();
                                customType(ReportUser.this,"fadein-to-fadeout");
                                MDToast.makeText(ReportUser.this,"Thank You for reporting user! We will check onto the user and take necessary actions if needed.",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                            }
                            else {
                                MDToast.makeText(ReportUser.this,"Error Reporting User! Please Try Again.",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                            }
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}