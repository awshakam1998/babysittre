package com.awshakam1998.guestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RateActivity extends Dialog {
    RatingBar ratingBar;
    Button submit;
    EditText comments;
    String uid;
    public RateActivity(Context context, String uid) {
        super(context);
        this.uid = uid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        ratingBar = findViewById(R.id.ratingBar);
        submit = findViewById(R.id.btnSubmit);
        comments = findViewById(R.id.etComment);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Sitter")
                        .child(uid)
                        .child("rating").setValue(ratingBar.getRating()+"");
                comments.setText("");
                RateActivity.this.cancel();
            }
        });
    }
}
