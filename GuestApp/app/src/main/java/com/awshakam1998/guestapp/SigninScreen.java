package com.awshakam1998.guestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.awshakam1998.guestapp.Moudles.Guest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Button login_btn;
    Button signup_btn;
    Button Signin_facebook;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference dbRef;
    ConstraintLayout constraintLayout;
    ProgressDialog progressDialog;
    public static final int Request_Code_Granded_Premission = 1408;
    String currentLocation;
    String _latitude, _longitude;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    String lat;
    String lng;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_screen);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this,MapsMain.class));
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(SigninScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(SigninScreen.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(SigninScreen.this, "You have already granted this permission!",
//                    Toast.LENGTH_SHORT).show();
        } else {
            requestLocationPermission();
        }

        //connect to map

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            //Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

        //end connect


        constraintLayout = (ConstraintLayout) findViewById(R.id.signin_layout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = mFirebaseDatabase.getReference("Guest");
        initView();


    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SigninScreen.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Granded_Premission);
                            ActivityCompat.requestPermissions(SigninScreen.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Request_Code_Granded_Premission);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(SigninScreen.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Granded_Premission);
            ActivityCompat.requestPermissions(SigninScreen.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Request_Code_Granded_Premission);
        }
    }


    private void showWatingDialog(String s) {
        progressDialog = progressDialog.show(this, "", s, true);
        progressDialog.setCancelable(false);
    }

    private void initView() {
        login_btn = (Button) findViewById(R.id.login_btn);
        signup_btn = (Button) findViewById(R.id.signup_btn);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RigesterOperation();
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginOperation();
            }
        });

    }
    private DatabaseReference mDatabase;
    private void loginOperation() {

        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        dialog.setTitle("Log In");
        dialog.setMessage("Enter your information");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.login_layout, null);
        final EditText email = layout_login.findViewById(R.id.EmailSitter_login);
        final EditText password = layout_login.findViewById(R.id.PasswordSitter_login);
        dialog.setView(layout_login);
        dialog.setPositiveButton("Log IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;

                }

                showWatingDialog("Please Watting");
                mFirebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.dismiss();


                                Intent i = new Intent(SigninScreen.this, MapsMain.class);
                                i.putExtra("email",email.getText().toString());
                                i.putExtra("pass",password.getText().toString());
// ...



                                startActivity(i);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SigninScreen.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        dialog.setNegativeButton("Cancle",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    private void RigesterOperation() {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Rigester");
        dialog.setMessage("Enter your information");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.rigester_layout, null);
        final EditText fname = layout_register.findViewById(R.id.FirstNameSitter);
        final EditText lname = layout_register.findViewById(R.id.LastNameSitter);
        final EditText email = layout_register.findViewById(R.id.EmailSitter);
        final EditText phone = layout_register.findViewById(R.id.PhoneSitter);
        final EditText password = layout_register.findViewById(R.id.PasswordSitter);
        final EditText repassword = layout_register.findViewById(R.id.rePasswordSitter);

        dialog.setView(layout_register);
        dialog.setPositiveButton("Sign up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (TextUtils.isEmpty(fname.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(lname.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your phone", Toast.LENGTH_SHORT).show();
                    return;

                }
                if ((phone.getText().toString().length() != 10) && !phone.getText().toString().startsWith("07")) {
                    Toast.makeText(SigninScreen.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (!email.getText().toString().contains("@") || !email.getText().toString().endsWith(".com")) {
                    Toast.makeText(SigninScreen.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;

                }
                if ((password.getText().toString().length() < 6)) {
                    Toast.makeText(SigninScreen.this, "Password should be more thn 6 character", Toast.LENGTH_SHORT).show();

                    return;

                }
                if (TextUtils.isEmpty(repassword.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Re-Enter password", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (!repassword.getText().toString().equals(password.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Re-Password dose not matches", Toast.LENGTH_SHORT).show();

                    return;

                }

                showWatingDialog("Please Watting");

                mFirebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Guest guest = new Guest();
                        guest.setFname(fname.getText().toString());
                        guest.setLname(lname.getText().toString());
                        guest.setEmail(email.getText().toString());
                        guest.setPhone(phone.getText().toString());
                        guest.setPassword(password.getText().toString());
                        guest.setLat(_latitude);
                        guest.setLng(_longitude);

                        dbRef.child(authResult.getUser().getUid()).setValue(guest).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(SigninScreen.this, "Rigesteration Success", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(SigninScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SigninScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
        dialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Request_Code_Granded_Premission) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*Ending the updates for the location service*/
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*Method to get the enable location settings dialog*/
    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SigninScreen.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            /*Getting the location after aquiring location service*/
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {

                _latitude = String.valueOf(mLastLocation.getLatitude());
                _longitude = String.valueOf(mLastLocation.getLongitude());
            } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                 * So we will get the current location.
                 * For this we'll implement Location Listener and override onLocationChanged*/
                Log.i("Current Location", "No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, SigninScreen.this);
            }
        }
    }

    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        _latitude = String.valueOf(mLastLocation.getLatitude());
        _longitude = String.valueOf(mLastLocation.getLongitude());
    }
}