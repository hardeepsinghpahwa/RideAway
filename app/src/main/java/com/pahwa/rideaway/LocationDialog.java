package com.pahwa.rideaway;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationDialog extends DialogFragment implements OnMapReadyCallback, DialogInterface.OnDismissListener {

    public static final String TAG = "FullScreenDialog";
    ImageView back;
    CustomEditText search;
    TextView selectlocation;
    ConstraintLayout constraintLayout;
    GoogleMap map;
    View view;
    ImageView marker, searchbutton;
    private FusedLocationProviderClient fusedLocationClient;
    ImageView mylocation;
    SupportMapFragment mapFragment;
    LatLng latLng;
    FloatingActionButton select;
    private RecyclerView recyclerView;
    ProgressBar progressBar, progressBar2;
    ArrayList<String> places, placeids;
    PlacesAdapter placesAdapter;
    double lat;
    double longi;
    int no = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);
        }
    }

    public interface LocationDialogListener {
        void onFinishEditDialog(double l1, double l2, String n);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getArguments().getString("ac").equals("findaride")) {
            if (getArguments().getString("msg").equals("pickup")) {
                TextView textView = getActivity().findViewById(R.id.pickuplocation);
                textView.setText(search.getText().toString());
                textView.setEnabled(true);
            } else {
                TextView textView = getActivity().findViewById(R.id.droplocation);
                textView.setText(search.getText().toString());
                textView.setEnabled(true);
            }
        } else {
            if (getArguments().getString("msg").equals("pickup")) {
                TextView textView = getActivity().findViewById(R.id.pickuplocationofferaride);
                textView.setText(search.getText().toString());
                textView.setEnabled(true);
            } else {
                TextView textView = getActivity().findViewById(R.id.droplocationofferaride);
                textView.setText(search.getText().toString());
                textView.setEnabled(true);
            }
        }
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        LocationDialogListener listener = (LocationDialogListener) getActivity();
        listener.onFinishEditDialog(lat, longi, getArguments().getString("msg"));
        getDialog().dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, final Bundle state) {
        super.onCreateView(inflater, parent, state);

        view = inflater.inflate(R.layout.locationdialog, parent, false);

        places = new ArrayList<>();
        placeids = new ArrayList<>();

        back = view.findViewById(R.id.dialogback);
        search = view.findViewById(R.id.searchlocation);
        selectlocation = view.findViewById(R.id.selectlocationtext);
        constraintLayout = view.findViewById(R.id.cons4);
        mylocation = view.findViewById(R.id.currentlocation);
        marker = view.findViewById(R.id.marker);
        progressBar = view.findViewById(R.id.locationprogressbar);
        select = view.findViewById(R.id.select);
        searchbutton = view.findViewById(R.id.searchbutton);
        progressBar2 = view.findViewById(R.id.locationprogressbar2);

        back.bringToFront();
        selectlocation.bringToFront();
        search.bringToFront();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                search.clearFocus();
                return false;

            }
        });


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getArguments().getString("ac").equals("findaride")) {
                    if (getArguments().getString("msg").equals("pickup")) {
                        TextView textView = getActivity().findViewById(R.id.pickuplocation);
                        textView.setText(search.getText().toString());
                        sendBackResult();
                        textView.setEnabled(true);
                    } else {
                        TextView textView = getActivity().findViewById(R.id.droplocation);
                        textView.setText(search.getText().toString());
                        sendBackResult();
                        textView.setEnabled(true);
                    }
                } else {
                    if (getArguments().getString("msg").equals("pickup")) {
                        TextView textView = getActivity().findViewById(R.id.pickuplocationofferaride);
                        textView.setText(search.getText().toString());
                        sendBackResult();
                        textView.setEnabled(true);
                    } else {
                        TextView textView = getActivity().findViewById(R.id.droplocationofferaride);
                        textView.setText(search.getText().toString());
                        sendBackResult();
                        textView.setEnabled(true);
                    }
                }
            }
        });

        Places.initialize(getActivity(), getResources().getString(R.string.google_maps_key));

        recyclerView = view.findViewById(R.id.recyview);

        placesAdapter = new PlacesAdapter(places);
        recyclerView.setAdapter(placesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        search.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {

                switch (target) {
                    case RIGHT:

                        search.setText("");
                        lat = 200;
                        longi = 200;
                        mapFragment.getView().setVisibility(View.GONE);
                        marker.setVisibility(View.GONE);
                        search.setInputType(InputType.TYPE_CLASS_TEXT);
                        select.setVisibility(View.GONE);

                        break;

                    default:
                        break;
                }

            }
        });


        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no = 0;


                if (!search.getText().toString().equals("")) {
                    progressBar.setVisibility(View.VISIBLE);

                    searchbutton.setVisibility(View.INVISIBLE);

                    mapFragment.getView().setVisibility(View.GONE);
                    marker.setVisibility(View.GONE);
                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                    final FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(search.getText().toString())
                            .setSessionToken(token)
                            .setTypeFilter(TypeFilter.REGIONS)
                            .setCountry("IN")
                            .build();

                    PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(getActivity());

                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                        @Override
                        public void onSuccess(FindAutocompletePredictionsResponse response) {

                            progressBar2.setVisibility(View.GONE);
                            places.clear();
                            placeids.clear();
                            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                Log.i(TAG, prediction.getFullText(null).toString());
                                places.add(prediction.getFullText(null).toString());
                                placeids.add(prediction.getPlaceId());


                            }
                            placesAdapter = new PlacesAdapter(places);
                            recyclerView.setAdapter(placesAdapter);
                            placesAdapter.notifyDataSetChanged();

                            if (recyclerView.getAdapter().getItemCount() > 1) {
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            mylocation.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            searchbutton.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressBar2.setVisibility(View.GONE);

                            MDToast.makeText(getActivity(), "Cant Find Results. Please Try Again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                            mylocation.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            searchbutton.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);

                        }
                    });
                } else {
                    progressBar2.setVisibility(View.GONE);
                    MDToast.makeText(getActivity(), "Enter Some Text First", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
                }
            }
        });


        getDialog().setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    getDialog().dismiss();
                }
                return true;
            }
        });

        search.requestFocus();

        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        mapFragment.getView().setVisibility(View.GONE);

        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mylocation.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                search.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);


                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),

                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,

                                    Manifest.permission.ACCESS_COARSE_LOCATION,

                            }, 123);

                    progressBar2.setVisibility(View.GONE);
                    mylocation.setVisibility(View.VISIBLE);

                } else {

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {

                                    if (location != null && map != null) {
                                        latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                        lat = location.getLatitude();
                                        longi = location.getLongitude();

                                        CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(
                                                latLng, 15);
                                        map.animateCamera(loc);
                                        recyclerView.setVisibility(View.GONE);
                                        marker.setVisibility(View.VISIBLE);
                                        mapFragment.getView().setVisibility(View.VISIBLE);

                                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                        List<Address> addresses = null;
                                        try {
                                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        String cityName = null;
                                        String stateName = null;
                                        if (addresses != null) {
                                            cityName = addresses.get(0).getFeatureName();
                                            stateName = addresses.get(0).getLocality();
                                        }

                                        if (cityName != null && stateName != null) {
                                            if (cityName.equals("null") || cityName.equals(stateName)) {
                                                search.setText(stateName);
                                            } else if (stateName.equals("null") || cityName.equals(stateName)) {
                                                search.setText(cityName);
                                            } else {
                                                search.setText(cityName + ", " + stateName);
                                            }
                                        }
                                        mylocation.setVisibility(View.VISIBLE);
                                        progressBar2.setVisibility(View.GONE);

                                        if (!search.getText().toString().equals("")) {
                                            select.setVisibility(View.VISIBLE);
                                            search.setInputType(InputType.TYPE_NULL);

                                        } else {
                                            MDToast.makeText(getActivity(), "Can't Get Location Name", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mylocation.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.GONE);
                            MDToast.makeText(getActivity(), "Cant Find Your Location", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);

                        }
                    });

                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 123) {

            MDToast.makeText(getActivity(), "ok Granted", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();


            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MDToast.makeText(getActivity(), "Permission Granted", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null && map != null) {
                                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                    CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(
                                            latLng, 15);
                                    map.animateCamera(loc);
                                    mapFragment.getView().setVisibility(View.VISIBLE);
                                    mylocation.setVisibility(View.VISIBLE);
                                    progressBar2.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mylocation.setVisibility(View.VISIBLE);
                        progressBar2.setVisibility(View.GONE);
                        MDToast.makeText(getActivity(), "Cant Find Your Location", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }
                });


            } else {
                MDToast.makeText(getActivity(), "Permission Denied", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                searchbutton.setVisibility(View.VISIBLE);
                mylocation.setVisibility(View.VISIBLE);
            }
            return;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                    }
                });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (map != null) {

                    mylocation.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.GONE);

                    latLng = map.getCameraPosition().target;

                    lat = latLng.latitude;
                    longi = latLng.longitude;

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addresses != null) {
                        if (addresses.size() > 0) {

                            String cityName = null;
                            String stateName = null;
                            if (addresses != null) {
                                cityName = addresses.get(0).getFeatureName();
                                stateName = addresses.get(0).getLocality();
                            }

                            if (cityName != null && stateName != null) {
                                if (cityName.equals("null") || cityName.equals(stateName)) {
                                    search.setText(stateName);
                                } else if (stateName.equals("null") || cityName.equals(stateName)) {
                                    search.setText(cityName);
                                } else {
                                    search.setText(cityName + ", " + stateName);
                                }
                            }

                        }
                    }
                }
            }
        });

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mylocation.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                marker.bringToFront();
                ObjectAnimator animator = ObjectAnimator.ofFloat(marker, "translationY", 0, 25, 0);
                animator.setInterpolator(new BounceInterpolator());
                animator.setDuration(1000);
                animator.start();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        super.onDestroyView();
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.maps));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();

    }


    private class PlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

        ArrayList<String> areas;

        public PlacesAdapter(ArrayList<String> areas) {
            this.areas = areas;
        }

        @NonNull
        @Override
        public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_recycler_item_layout, parent, false);
            return new PlacesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlacesViewHolder holder, final int position) {
            holder.area.setText(areas.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    searchbutton.setVisibility(View.INVISIBLE);
                    search.setText(areas.get(position));
                    recyclerView.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    search.clearFocus();

                    if (!search.getText().toString().equals("")) {
                        select.setVisibility(View.VISIBLE);
                        search.setInputType(InputType.TYPE_NULL);
                    } else {
                        MDToast.makeText(getActivity(), "Can't Get Location Name", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }

                    PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(getActivity());

                    String placeId = placeids.get(position);

                    Log.i("place", placeids.get(position));

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG);

                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse response) {
                            Place place = response.getPlace();
                            CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(
                                    place.getLatLng(), 15);
                            map.animateCamera(loc);
                            mapFragment.getView().setVisibility(View.VISIBLE);
                            marker.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            searchbutton.setVisibility(View.VISIBLE);

                            lat = place.getLatLng().latitude;
                            longi = place.getLatLng().longitude;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                int statusCode = apiException.getStatusCode();
                                // Handle error with given status code.
                                Log.e(TAG, "Place not found: " + exception.getMessage());

                            }
                            searchbutton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }
            });
        }


        @Override
        public int getItemCount() {
            return areas.size();
        }
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {

        TextView area;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);

            area = itemView.findViewById(R.id.place_area);
        }
    }

}