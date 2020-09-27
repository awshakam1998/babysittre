package com.awshakam1998.sitterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awshakam1998.sitterapp.Constant.Api;
import com.awshakam1998.sitterapp.Constant.Constance;
import com.awshakam1998.sitterapp.Moudles.Sitters;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import taxidriver.nageh.mario.decodepolymario.DecodingpolyMario;
import taxidriver.nageh.mario.decodepolymario.MarioLatLng;

import static com.awshakam1998.sitterapp.R.drawable.car_icon_marker;

public class MapsMain extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog progressDialog;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker marker, carMarker;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    double lat;
    double lng;
    Button setting;
    FusedLocationProviderClient mFusedLocationClient;
    SettingsClient mSettingsClient;
    LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;
    LocationCallback mLocationCallback;
    Location mCurrentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Boolean mRequestingLocationUpdates;
    GeoFire DatabaseGeoFire;
    SwitchCompat switchCompat;
    ImageView img_profile;
    TextView username;
    TextView email;
    EditText fnameinfo;
    EditText lnameinfo;
    EditText phoneinfo;
    EditText skillinfo;
    TextView updateinfo_tv;
    TextView cancleinfo_tv;
    EditText lpass;
    EditText npass;
    EditText renpass;
    TextView tvupdatepass;
    TextView tvcanclepass;
    View update_information;
    View update_pass;
    String currentpass;
    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    int count_time = 0;
    public static final int Request_Code_Granded_Premission = 1408;
    private Api api;
    LatLng CurrentLocation, StartLocation, EndLocation;
    List<LatLng> Polylinelist;
    String destination;
    PolylineOptions BlackOptions, BlueOptions;
    Polyline BlackLine, BlueLine;
    Handler handler;
    float aFloat;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (index < Polylinelist.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < Polylinelist.size() - 1) {
                StartLocation = Polylinelist.get(index);
                EndLocation = Polylinelist.get(next);
            }
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(3000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    aFloat = animation.getAnimatedFraction();
                    double lat = aFloat * EndLocation.latitude + (1 - aFloat * StartLocation.latitude);
                    double lng = aFloat * EndLocation.longitude + (1 - aFloat * StartLocation.longitude);
                    LatLng newPostion = new LatLng(lat, lng);
                    carMarker.setPosition(newPostion);
                    carMarker.setRotation(getRRotation(StartLocation, newPostion));
                    carMarker.setAnchor(.5f, .5f);
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(newPostion).zoom(15.5f).build()));
                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 3000);

        }
    };

    private float getRRotation(LatLng startLocation, LatLng newPostion) {
        Double lat = Math.abs(startLocation.latitude - newPostion.latitude);
        Double lng = Math.abs(startLocation.longitude - newPostion.longitude);

        if (StartLocation.latitude < newPostion.latitude && StartLocation.longitude < newPostion.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        } else if (StartLocation.latitude >= newPostion.latitude && StartLocation.longitude < newPostion.longitude) {
            return (float) (90 - Math.toDegrees(Math.atan(lng / lat)) + 90);
        } else if (StartLocation.latitude >= newPostion.latitude && StartLocation.longitude >= newPostion.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        } else if (StartLocation.latitude < newPostion.latitude && StartLocation.longitude > newPostion.longitude) {
            return (float) (90 - Math.toDegrees(Math.atan(lng / lat)) + 270);
        } else
            return -1;
    }

    int index, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sitter);
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(MapsMain.this);
        final View sheetView = MapsMain.this.getLayoutInflater().inflate(R.layout.bottom_dialog, null);
        final TextView name = sheetView.findViewById(R.id.name);
        final TextView phone = sheetView.findViewById(R.id.phone);
        final Button navigate = sheetView.findViewById(R.id.navigate);
        final Button start = sheetView.findViewById(R.id.start);
        final TextView time = sheetView.findViewById(R.id.time);
        mBottomSheetDialog.setContentView(sheetView);
        FirebaseDatabase.getInstance().getReference("sitter_request")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot d : dataSnapshot.getChildren()) {
                            if (d.exists()) {
                                if (d.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    mBottomSheetDialog.show();
                                    if(d.child("to").getValue(String.class) != null){
                                    FirebaseDatabase.getInstance().getReference("Guest").child(d.child("to").getValue(String.class))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    name.setText("Name : " + dataSnapshot.child("fname").getValue(String.class)+" "+dataSnapshot.child("lname").getValue(String.class));
                                                    phone.setText("Phone : " + dataSnapshot.child("phone").getValue(String.class));
                                                    System.out.println(dataSnapshot.child("name").getValue(String.class));
                                                    System.out.println(dataSnapshot.child("phone").getValue(String.class));
                                                    mBottomSheetDialog.show();

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                    }
                                    navigate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(navigate.getText().toString().equalsIgnoreCase("navigate to coustomer")){
                                            Double lat =  d.child("lat_req").getValue(Double.class);
                                            Double lng = d.child("lng_req").getValue(Double.class);
                                            String url = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng + "&travelmode=driving";
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(intent);}
                                            else if(navigate.getText().toString().equalsIgnoreCase("accept")){
                                                FirebaseDatabase.getInstance().getReference("sitter_request")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("status").setValue("accepted");
                                                start.setVisibility(View.VISIBLE);
                                                start.setText("start sitting");
                                                navigate.setText("navigate to coustomer");
                                            }
                                        }
                                    });

                                    start.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (start.getText().toString().equalsIgnoreCase("cancle")){
                                                FirebaseDatabase.getInstance().getReference("sitter_request")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("status").setValue("not_accepted");
                                                mBottomSheetDialog.dismiss();
                                            }
                                            if(start.getText().toString().equalsIgnoreCase("start sitting")){
                                                FirebaseDatabase.getInstance().getReference("sitter_request")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("status").setValue("start");
                                            start.setText("End sitting");
                                            navigate.setVisibility(View.GONE);
                                            final Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    time.setText("time : "+(count_time++));
                                                    handler.postDelayed(this, 1000);
                                                }
                                            };
                                            handler.postDelayed(runnable, 1000);}

                                            else if(start.getText().toString().equalsIgnoreCase("End sitting")){
                                                FirebaseDatabase.getInstance().getReference("sitter_request")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("status").setValue("end");
                                                FirebaseDatabase.getInstance().getReference("Sitter")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("priceofhour").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        double totalprice = Double.parseDouble(dataSnapshot.getValue(String.class));
                                                        System.out.println(count_time +" /// "+ totalprice);
                                                        System.out.println(((((double) count_time/60)/60)));
                                                        name.setText(((((double)count_time/60)/60)*totalprice)+" JD ");
                                                        phone.setVisibility(View.GONE);
                                                        time.setVisibility(View.GONE);

                                                        start.setText("Collect Cash");
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                            else if (start.getText().toString().equalsIgnoreCase("Collect Cash")){
                                                mBottomSheetDialog.dismiss();
                                                FirebaseDatabase.getInstance().getReference("sitter_request")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .removeValue();
                                                Ratedialog ratedialog = new Ratedialog(MapsMain.this,d.child("to").getValue(String.class));
                                                ratedialog.show();

                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        switchCompat = findViewById(R.id.pin);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        DatabaseGeoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("Sitter_Locations"));
        initView();
        api = Constance.getapi();
    }

    private void initView() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View header = navigationView.getHeaderView(0);
        img_profile = header.findViewById(R.id.imgprofile);
        username = header.findViewById(R.id.username);
        email = header.findViewById(R.id.tvemail);
        LayoutInflater inflater = LayoutInflater.from(this);
        update_information = inflater.inflate(R.layout.update_information, null);
        fnameinfo = update_information.findViewById(R.id.FirstNameinfo);
        lnameinfo = update_information.findViewById(R.id.LastNameinfo);
        phoneinfo = update_information.findViewById(R.id.Phoneinfo);
        skillinfo = update_information.findViewById(R.id.Skillinfo);
        updateinfo_tv = update_information.findViewById(R.id.updateinfo);
        cancleinfo_tv = update_information.findViewById(R.id.cancleinfo);

        LayoutInflater inflater1 = LayoutInflater.from(this);
        update_pass = inflater1.inflate(R.layout.update_paswoord, null);
        lpass = update_pass.findViewById(R.id.lastPass);
        npass = update_pass.findViewById(R.id.newPass);
        renpass = update_pass.findViewById(R.id.renewPass);
        tvupdatepass = update_pass.findViewById(R.id.updatePass);
        tvcanclepass = update_pass.findViewById(R.id.canclePass);

        //    username.setText("aws hakam");
        navigationView.setNavigationItemSelectedListener(MapsMain.this);
        loadInformation();
        handler = new Handler();
        switchCompat = findViewById(R.id.pin);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    StartGittingLocation();
                } else {
                    StopGittingLocation();
                }
            }
        });

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    private void StopGittingLocation() {

        FirebaseDatabase.getInstance().getReference("sitter_online").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        startLocationUpdates(false);
        preparemMap(null,false);
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

        //mMap.setMyLocationEnabled(false);
        Toast.makeText(this, "You are off-line", Toast.LENGTH_SHORT).show();

    }

    private void StartGittingLocation() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
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

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates(true);
                        preparemMap(mMap,true);


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

        Toast.makeText(getApplicationContext(), "Your are on-Line", Toast.LENGTH_LONG).show();


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

    private void startLocationUpdates(boolean state) {
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

                            updateLocationUI();
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

                            updateLocationUI();
                        }
                    });
        }
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            setLat(mCurrentLocation.getLatitude());
            setLng(mCurrentLocation.getLongitude());
        }
    }

    private void Directions() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        String RequeserApi = null;
        destination = "Maadi";
        try {
            RequeserApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin" + CurrentLocation.latitude + "," + CurrentLocation.longitude + "&" +
                    "destination" + destination + "&" + "$key=AIzaSyC8lREnbp7DMB4CD9CqHn4fycuZ8kF9boU" +
                    "mode=sitting&" + "transit_routing_preference=less_walking";
            api.getstringonline(RequeserApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {
                        JSONObject JsonBody = new JSONObject(response.body().toString());
                        JSONArray Routes = JsonBody.getJSONArray("routes");
                        for (int i = 0; i < Routes.length(); i++) {
                            JSONObject route = Routes.getJSONObject(i);
                            JSONObject overview_polyline = route.getJSONObject("overview_polyline");
                            String points = overview_polyline.getString("points");
                            DecodingpolyMario decodingpolyMario = new DecodingpolyMario();
                            List<MarioLatLng> latlngMario = decodingpolyMario.decodePoly(points);
                            Polylinelist = new ArrayList<>();
                            LatLng latlngewforlist;
                            for (MarioLatLng Value : latlngMario) {
                                latlngewforlist = new LatLng(Value.getLat(), Value.getLng());
                                Polylinelist.add(latlngewforlist);
                            }
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng latLng : Polylinelist) {
                                builder.include(latLng);
                            }
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                            mMap.animateCamera(cameraUpdate);
                            BlueOptions = new PolylineOptions();
                            BlueOptions.color(Color.BLUE);
                            BlueOptions.jointType(JointType.ROUND);
                            BlueOptions.width(5);
                            BlueOptions.startCap(new SquareCap());
                            BlueOptions.endCap(new SquareCap());
                            BlueOptions.addAll(Polylinelist);
                            BlueLine = mMap.addPolyline(BlueOptions);
                            BlackOptions = new PolylineOptions();
                            BlackOptions.color(Color.BLACK);
                            BlackOptions.jointType(JointType.ROUND);
                            BlackOptions.width(5);
                            BlackOptions.startCap(new SquareCap());
                            BlackOptions.endCap(new SquareCap());
                            BlackOptions.addAll(Polylinelist);
                            BlackLine = mMap.addPolyline(BlackOptions);

                            mMap.addMarker(new MarkerOptions().position(Polylinelist.get(Polylinelist.size() - 1))
                                    .title("Next Location"));
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                            valueAnimator.setDuration(2000);
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    List<LatLng> points = BlueLine.getPoints();
                                    int pre = (int) animation.getAnimatedValue();
                                    int size = points.size();
                                    int newPoint = (int) (size * (pre / 100f));
                                    List<LatLng> latLngsPoints = points.subList(0, newPoint);
                                    BlackLine.setPoints(latLngsPoints);
                                }
                            });

                            valueAnimator.start();

                            carMarker = mMap.addMarker(new MarkerOptions().position(CurrentLocation).flat(true).
                                    icon(BitmapDescriptorFactory.fromResource(car_icon_marker)));
                            handler = new Handler();
                            index = -1;
                            next = 1;
                            handler.postDelayed(runnable, 3000);


                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);


    }

    public void preparemMap(GoogleMap googleMap , final boolean state) {
        mMap = googleMap;

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (state && mMap != null){
                        mMap.clear();
                    FirebaseDatabase.getInstance().getReference("sitter_online").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("lat").setValue(getLat());
                    FirebaseDatabase.getInstance().getReference("sitter_online").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("lon").setValue(getLng());
                    LatLng sitter = new LatLng(getLat(), getLng());
                    mMap.addMarker(new MarkerOptions().title("me")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.sitter_icon))
                            .position(sitter));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sitter));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sitter, 15f));
                    handler.postDelayed(this, 1000);
                }
                }
            };
            handler.postDelayed(runnable, 1000);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_update_info:
                UpdateInformation();
                break;
            case R.id.nav_change_pwd:
                UpdatePassword();
                break;
            case R.id.nav_sign_out:
                StopGittingLocation();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MapsMain.this, SigninScreen.class));
                break;
            case R.id.nav_change_price:
                change_price();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void change_price() {


        final AlertDialog.Builder dialog = new AlertDialog.Builder(MapsMain.this);
        dialog.setTitle("Change your Price");
        LayoutInflater inflater = LayoutInflater.from(MapsMain.this);
        View view1 = inflater.inflate(R.layout.change_price_of_hour, null);
        final EditText priceofhour = view1.findViewById(R.id.priceofhour);
        dialog.setView(view1);

        FirebaseDatabase.getInstance().getReference("Sitter")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("priceofhour")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            priceofhour.setText(dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        dialog.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("priceofhour")
                        .setValue(priceofhour.getText().toString());
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();


    }
    public void loadInformation() {
//


        FirebaseDatabase.getInstance().getReference("Sitter")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        username.setText(dataSnapshot.child(uid).child("fname").getValue(String.class) + " " + dataSnapshot.child(uid).child("lname").getValue(String.class));
                        email.setText(dataSnapshot.child(uid).child("email").getValue(String.class));
                        //  img_profile.setImageResource(R.drawable.sitter);

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void UpdateInformation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update Information");
        dialog.setMessage("Enter your information");


        dialog.setView(update_information);
        final AlertDialog alertDialog = dialog.create();
        FirebaseDatabase.getInstance().getReference("Sitter")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        fnameinfo.setText(dataSnapshot.child(uid).child("fname").getValue(String.class) );
                        lnameinfo.setText(dataSnapshot.child(uid).child("lname").getValue(String.class));
                        phoneinfo.setText(dataSnapshot.child(uid).child("phone").getValue(String.class));
                        skillinfo.setText(dataSnapshot.child(uid).child("skills").getValue(String.class));


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        alertDialog.show();

        updateinfo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(fnameinfo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please enter your first name", Toast.LENGTH_SHORT).show();
                    fnameinfo.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(lnameinfo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please enter your last name", Toast.LENGTH_SHORT).show();
                    lnameinfo.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(phoneinfo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please enter your phone", Toast.LENGTH_SHORT).show();
                    phoneinfo.requestFocus();
                    return;

                }
                if ((phoneinfo.getText().toString().length() != 10) && !phoneinfo.getText().toString().startsWith("07")) {
                    phoneinfo.requestFocus();
                    return;

                }

                if (TextUtils.isEmpty(skillinfo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter your Skills", Toast.LENGTH_SHORT).show();
                    skillinfo.requestFocus();
                    return;

                }


                showWatingDialog("Please Watting");

                Sitters sitter = new Sitters();
                sitter.setFname(fnameinfo.getText().toString());
                sitter.setLname(lnameinfo.getText().toString());
                sitter.setPhone(phoneinfo.getText().toString());
                sitter.setSkills(skillinfo.getText().toString());


                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(uid).child("fname").setValue(sitter.getFname());
                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(uid).child("lname").setValue(sitter.getLname());
                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(uid).child("phone").setValue(sitter.getPhone());
                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(uid).child("skills").setValue(sitter.getSkills());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Update information Successful", Toast.LENGTH_LONG).show();

                alertDialog.dismiss();

            }
        });

        cancleinfo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });


    }

    private void UpdatePassword(){

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update Password");

        dialog.setView(update_pass);
        final AlertDialog alertDialog = dialog.create();
        FirebaseDatabase.getInstance().getReference("Sitter")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        currentpass=dataSnapshot.child(uid).child("password").getValue(String.class) ;

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        alertDialog.show();

        tvupdatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(lpass.getText().toString().equals(currentpass))) {
                    Toast.makeText(getApplicationContext(), "InValid current Password : is not correct", Toast.LENGTH_SHORT).show();
                    lpass.requestFocus();
                    return;

                }
                if (TextUtils.isEmpty(lpass.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please enter your Current Password", Toast.LENGTH_SHORT).show();
                    lpass.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(npass.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please enter your new Password", Toast.LENGTH_SHORT).show();
                    npass.requestFocus();
                    return;
                }
                if (npass.getText().toString().length()<6) {
                    Toast.makeText(getApplicationContext(), "InValid Password : Password is too short", Toast.LENGTH_SHORT).show();
                    npass.requestFocus();
                    return;

                }
                if (!(renpass.getText().toString().equals(npass.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "InValid Password : passwords not matches", Toast.LENGTH_SHORT).show();
                    renpass.requestFocus();
                    return;

                }




                showWatingDialog("Please Watting");

                Sitters sitter = new Sitters();
                sitter.setPassword(npass.getText().toString());


                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(uid).child("password").setValue(sitter.getPassword());

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Update password Successful", Toast.LENGTH_LONG).show();

                alertDialog.dismiss();

            }
        });

        tvcanclepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });


    }
    private void showWatingDialog(String s) {
        progressDialog = progressDialog.show(this, "", s, true);
        progressDialog.setCancelable(false);
    }
}
