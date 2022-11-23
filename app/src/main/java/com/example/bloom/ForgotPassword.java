package com.example.bloom;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        EditText editText = findViewById(R.id.input_email_forgot);

        Button buttonSend = findViewById(R.id.btnSendEmail);

        buttonSend.setOnClickListener(v -> {
            Log.d("ForgotPassword", editText.getText().toString().trim());
            if (!editText.getText().toString().trim().equals("")) {
                mAuth.sendPasswordResetEmail(editText.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Email reset telahh terkirim", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Email kosong, Mohon isi", Toast.LENGTH_LONG).show();
            }
        });
    }
}