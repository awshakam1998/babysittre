package com.awshakam1998.sitterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awshakam1998.sitterapp.Constant.Api;
import com.awshakam1998.sitterapp.Constant.Constance;
import com.awshakam1998.sitterapp.Moudles.Sitters;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SigninScreen extends AppCompatActivity {
    Button login_btn;
    Button signup_btn;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference dbRef;
    ConstraintLayout constraintLayout;
    ProgressDialog progressDialog;
    public static final int Request_Code_Granded_Premission = 1408;
    private Api api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_screen);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this,MapsMain.class));
        }

        if (ContextCompat.checkSelfPermission(SigninScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(SigninScreen.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(SigninScreen.this, "You have already granted this permission!",
//                    Toast.LENGTH_SHORT).show();
        } else {
            requestLocationPermission();
        }

        constraintLayout = (ConstraintLayout) findViewById(R.id.signin_layout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = mFirebaseDatabase.getReference("Sitter");
        initView();

    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION )&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SigninScreen.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Granded_Premission);
                            ActivityCompat.requestPermissions(SigninScreen.this,
                                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, Request_Code_Granded_Premission);
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
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Granded_Premission);
            ActivityCompat.requestPermissions(SigninScreen.this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, Request_Code_Granded_Premission);
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

    private void loginOperation() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
        dialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    private void RigesterOperation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
        final EditText skill = layout_register.findViewById(R.id.SkillSitter);
        final EditText priceofhour = layout_register.findViewById(R.id.priceofhour);
        final TextView signup = layout_register.findViewById(R.id.signup);
        final TextView cancle = layout_register.findViewById(R.id.cancle);
        dialog.setView(layout_register);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(fname.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
                if (TextUtils.isEmpty(lname.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your phone", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }
                if ((phone.getText().toString().length() != 10) && !phone.getText().toString().startsWith("07")) {
                    Toast.makeText(SigninScreen.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }
                if (!email.getText().toString().contains("@") || !email.getText().toString().endsWith(".com")) {
                    Toast.makeText(SigninScreen.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }
                if ((password.getText().toString().length() < 6)) {
                    Toast.makeText(SigninScreen.this, "Password should be more thn 6 character", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }
                if (TextUtils.isEmpty(repassword.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Re-Enter password", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }
                if (!repassword.getText().toString().equals(password.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Re-Password dose not matches", Toast.LENGTH_SHORT).show();

                    alertDialog.dismiss();

                }
                if (TextUtils.isEmpty(skill.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Enter your Skills", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                }

                if (TextUtils.isEmpty(priceofhour.getText().toString())) {
                    Toast.makeText(SigninScreen.this, "Please enter Price Of Hour", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

                showWatingDialog("Please Watting");

                mFirebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Sitters sitter = new Sitters();
                        sitter.setFname(fname.getText().toString());
                        sitter.setLname(lname.getText().toString());
                        sitter.setEmail(email.getText().toString());
                        sitter.setPhone(phone.getText().toString());
                        sitter.setPassword(password.getText().toString());
                        sitter.setSkills(skill.getText().toString());
                        sitter.setPriceofhour(priceofhour.getText().toString());

                        dbRef.child(authResult.getUser().getUid()).setValue(sitter).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(SigninScreen.this, "Rigesteration Success", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();

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


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Request_Code_Granded_Premission)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
