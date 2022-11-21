package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LaunchScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {

            @Override

            public void run() {


                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    Intent i = new Intent(LaunchScreen.this, Dashboard.class);

                    startActivity(i);


                    finish();

                } else{
                    Intent i = new Intent(LaunchScreen.this, OnboardActivity.class);

                    startActivity(i);


                    finish();
                }

            }

        }, 3000);
    }
}