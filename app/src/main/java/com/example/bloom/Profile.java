package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class Profile extends AppCompatActivity {

    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        Button logout = (Button) findViewById(R.id.logout);
        TextView countTaskC = (TextView) findViewById(R.id.task_completed);
        TextView hourTimer = findViewById(R.id.focus_timer);
        Button back = findViewById(R.id.back_profile);
        TextView textName = findViewById(R.id.textView25);
        ImageView photoprofile = findViewById(R.id.photoprofile);
        TextView editButton = findViewById(R.id.button2);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        textName.setText(auth.getCurrentUser().getDisplayName());
        if (!Uri.EMPTY.equals(auth.getCurrentUser().getPhotoUrl())) {
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(photoprofile);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent i = new Intent(Profile.this, LoginActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        editButton.setOnClickListener(v -> {
            Intent i = new Intent(Profile.this, EditProfile.class);
            startActivity(i);
        });

        SQLiteDatabase db = (new DBHelper(this)).getReadableDatabase();
        String[] params = new String[]{ "1" };
        Cursor result = db.rawQuery("SELECT * FROM tbl_Task WHERE isDone = ?", params);
        countTaskC.setText(String.valueOf(result.getCount()));
        result.close();

        sharedpreferences = getSharedPreferences("HOURS", Context.MODE_PRIVATE);
        //editor.putString("HOURS", );
        hourTimer.setText(sharedpreferences.getString("HOURS", "0") + "h");
    }
}