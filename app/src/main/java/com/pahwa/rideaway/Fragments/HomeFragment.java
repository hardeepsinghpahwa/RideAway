package com.pahwa.rideaway.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pahwa.rideaway.FindARide;
import com.pahwa.rideaway.OfferARide;
import com.pahwa.rideaway.R;

import static maes.tech.intentanim.CustomIntent.customType;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    Button findaride,offeraride;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);

        findaride=v.findViewById(R.id.findaride);
        offeraride=v.findViewById(R.id.offeraride);



        findaride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FindARide.class));
                customType(getActivity(),"left-to-right");
            }
        });

        offeraride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OfferARide.class));
                customType(getActivity(),"left-to-right");
            }
        });

        return v;
    }
}
