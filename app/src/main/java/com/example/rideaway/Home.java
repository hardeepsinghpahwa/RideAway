package com.example.rideaway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.os.Bundle;

import com.example.rideaway.Fragments.HomeFragment;
import com.example.rideaway.Fragments.Profile;
import com.example.rideaway.Fragments.Rides;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class Home extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chipNavigationBar=findViewById(R.id.bottomnav);
        constraintLayout=findViewById(R.id.cons3);

        chipNavigationBar.setItemSelected(R.id.home,true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cons3, new HomeFragment())
                .commit();
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch(i) {
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
                        .replace(R.id.cons3, fragment)
                        .commit();
            }
        });
    }
}