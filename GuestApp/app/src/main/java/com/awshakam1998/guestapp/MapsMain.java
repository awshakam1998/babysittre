package com.awshakam1998.guestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.awshakam1998.guestapp.Moudles.Guest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MapsMain extends FragmentActivity implements OnMapReadyCallback {
    FirebaseAuth mAuth;
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    Marker marker;
    private static final String TAG = "MapsMain";
    static String email, password;
    static double a, b;
    FusedLocationProviderClient mFusedLocationClient;
    SettingsClient mSettingsClient;
    LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;
    LocationCallback mLocationCallback;
    Location mCurrentLocation;
    ProgressDialog progressDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    Boolean mRequestingLocationUpdates;
    Double lat, lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        Intent iin = getIntent();
        Bundle b1 = iin.getExtras();

        if (b1 != null) {
            email = (String) b1.get("email");
            password = (String) b1.get("pass");

        }
        System.out.println("awsaws" + email);
        System.out.println("awsaws" + password);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        FirebaseDatabase.getInstance().getReference("sitter_online")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            if (d.exists()) {
                                LatLng sitter = new LatLng(d.child("lat").getValue(Double.class),
                                        d.child("lon").getValue(Double.class));
                                mMap.addMarker(new MarkerOptions().title("sitters")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sitter_icon))
                                        .position(sitter)).setTag(d.getKey());
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sitter));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sitter, 15f));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MapsMain.this);
                dialog.setTitle("Sitter information");
                LayoutInflater inflater = LayoutInflater.from(MapsMain.this);
                View view1 = inflater.inflate(R.layout.sitter_info, null);
                final TextView name = view1.findViewById(R.id.name);
                final TextView email = view1.findViewById(R.id.email);
                final TextView phone = view1.findViewById(R.id.phonenum);
                final TextView skills = view1.findViewById(R.id.skills);
                final TextView priceofhour = view1.findViewById(R.id.priceofhour);
                dialog.setView(view1);
                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(marker.getTag().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                name.setText("Name : " + dataSnapshot.child("fname").getValue(String.class) + " " + dataSnapshot.child("lname").getValue(String.class));
                                email.setText("Email : " + dataSnapshot.child("email").getValue(String.class));
                                phone.setText("Phone Number : " + dataSnapshot.child("phone").getValue(String.class));
                                skills.setText("Skills : " + dataSnapshot.child("skills").getValue(String.class));
                                priceofhour.setText("Price Of Hour : " + dataSnapshot.child("priceofhour").getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                dialog.setPositiveButton("Pick Up This Sitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                // location is received
                                mCurrentLocation = locationResult.getLastLocation();
                                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                                updateLocationUI(marker.getTag().toString(),true);
                            }
                        };

                        mRequestingLocationUpdates = false;

                        mLocationRequest = new LocationRequest();
                        mLocationRequest.setInterval(1000);
                        mLocationRequest.setFastestInterval(500);
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                        builder.addLocationRequest(mLocationRequest);
                        mLocationSettingsRequest = builder.build();

                        Dexter.withActivity(MapsMain.this)
                                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        mRequestingLocationUpdates = true;
                                        startLocationUpdates(marker.getTag().toString(),true);
                                        //preparemMap(mMap);
                                    }
                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        if (response.isPermanentlyDenied()) {
                                            // open device settings when the permission is
                                            // denied permanently
                                            openSettings();
                                        }
                                    }
                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();

                return false;
            }
        });
    }
    private void startLocationUpdates(final String uid, final boolean state) {
        if(state) {
            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                            //noinspection MissingPermission
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());
                            updateLocationUI(uid,state);
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    //"location settings ");
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(MapsMain.this, 100);
                                    } catch (IntentSender.SendIntentException sie) {
                                        //Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    //Log.e(TAG, errorMessage);

                                    Toast.makeText(MapsMain.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                            updateLocationUI(uid,state);
                        }
                    });
        }
    }
    private void updateLocationUI(final String uid,boolean state) {
        if(state) {
            if (mCurrentLocation != null) {
                setLat(mCurrentLocation.getLatitude());
                setLng(mCurrentLocation.getLongitude());
                FirebaseDatabase.getInstance().getReference("sitter_request").child(uid)
                        .child("lat_req").setValue(mCurrentLocation.getLatitude());
                FirebaseDatabase.getInstance().getReference("sitter_request").child(uid)
                        .child("lng_req").setValue(mCurrentLocation.getLongitude());
                FirebaseDatabase.getInstance().getReference("sitter_request").child(uid)
                        .child("to").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                showWatingDialog("please Wait the sitter answer");
                startLocationUpdates(uid, false);
                FirebaseDatabase.getInstance().getReference("sitter_request").child(uid).child("status").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.getValue(String.class).equalsIgnoreCase("accepted")) {
                                Toast.makeText(MapsMain.this, "The sitter accepted your request and he in way to you", Toast.LENGTH_LONG).show();
                                progressDialog.cancel();
                            } else if (dataSnapshot.getValue(String.class).equalsIgnoreCase("not_accepted")) {
                                Toast.makeText(MapsMain.this, "Sorry the Sitter not accept your request", Toast.LENGTH_LONG).show();
                                progressDialog.cancel();
                            } else if (dataSnapshot.getValue(String.class).equalsIgnoreCase("start")) {
                                Toast.makeText(MapsMain.this, "The sitter start service for you", Toast.LENGTH_LONG).show();
                                progressDialog.cancel();
                            } else if (dataSnapshot.getValue(String.class).equalsIgnoreCase("end")) {
                                RateActivity ratedialog = new RateActivity(MapsMain.this, uid);
                                progressDialog.cancel();
                                ratedialog.show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showWatingDialog(String s) {
        progressDialog = progressDialog.show(this, "", s, true);
        progressDialog.setCancelable(false);
    }

}




