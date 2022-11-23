package com.example.bloom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloom.databinding.ActivityCreateToDoListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.noowenz.customdatetimepicker.CustomDateTimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateToDoList extends AppCompatActivity{

    private SQLiteDatabase db;
    //private Boolean isTime = false;
    private String tagTemp = "Basic";

    private ActivityCreateToDoListBinding binding;
    private AlarmManager alarmManager;
    String pattern = "E, dd MMM yyyy";
    String pattern2 = "KK:mm a";
    String pattern3 = "E, dd MMM yyyy KK:mm a";
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleDateFormat2;
    private PendingIntent pendingIntent;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateToDoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button back = findViewById(R.id.back_create_todolist);

        getSupportActionBar().hide();

        db = (new DBHelper(this)).getWritableDatabase();

        //binding.linearLayout3.setVisibility(View.GONE);

        ConstraintLayout constraintLayout = findViewById(R.id.actdl);
        ConstraintSet constraintSet = new ConstraintSet();

        binding.switchTask.setChecked(true);
        binding.switchTask.setClickable(false);

        simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
        binding.tanggalTarget.setText(simpleDateFormat.format(new Date()));
        binding.tanggalTarget.setOnClickListener(view -> {
            showDialogTime();
        });

        simpleDateFormat2 = new SimpleDateFormat(pattern2, Locale.US);
        binding.jamTarget.setText(simpleDateFormat2.format(new Date()));
        binding.jamTarget.setOnClickListener(view -> {
            showDialogTime();
        });

        binding.tags.setText("Basic");

        binding.tags.setOnClickListener(view -> {
            if (String.valueOf(binding.tags.getText()).equals("Basic")) {
                binding.tags.setText("Important");
            } else {
                binding.tags.setText("Basic");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        binding.switchTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                binding.switchTask.setChecked(b);
//                if (b) {
//                    binding.linearLayout3.setVisibility(View.VISIBLE);
//                    constraintSet.clone(constraintLayout);
//                    constraintSet.connect(R.id.linearLayout5,ConstraintSet.TOP,R.id.linearLayout3,ConstraintSet.BOTTOM,0);
//                    constraintSet.applyTo(constraintLayout);
//                } else {
////                    binding.jamTarget.setText("Set Jam");
////                    binding.tanggalTarget.setText("Set Tanggal");
//                    constraintSet.clone(constraintLayout);
//                    constraintSet.connect(R.id.linearLayout5,ConstraintSet.TOP,R.id.switchTask,ConstraintSet.BOTTOM,0);
//                    constraintSet.applyTo(constraintLayout);
//                    binding.linearLayout3.setVisibility(View.GONE);
//
//                }
//            }
//        });

        binding.save.setOnClickListener(view -> {
            addTask();
        });
    }

    private void addTask() {
        if (String.valueOf(binding.editTitle.getText()).trim().equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Mohon isi judul", Snackbar.LENGTH_SHORT).show();
        } else {
            try {
                String tanggalString = convertTanggal();
                String jamString = convertJam();
                Long idTarget = System.currentTimeMillis();

                ContentValues values = new ContentValues();
                values.put("_id", idTarget);
                values.put("isDone", 0);
                values.put("nama", String.valueOf(binding.editTitle.getText()).trim());
                values.put("deskripsi", (String.valueOf(binding.editDesc.getText()).trim().equals("")) ? "Tidak ada deskripsi" : String.valueOf(binding.editDesc.getText()).trim());
                values.put("tanggal", tanggalString);
                values.put("jam", jamString);
                values.put("tag", String.valueOf(binding.tags.getText()).trim());

                db.insert("tbl_Task","_id", values);

                Date tanggalJam = new SimpleDateFormat(pattern3, Locale.US).parse(tanggalString + " " + jamString);

                calendar = Calendar.getInstance();
                assert tanggalJam != null;
                calendar.setTime(tanggalJam);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("TITLE_NO", String.valueOf(binding.editTitle.getText()).trim());
                intent.putExtra("DATE_NO", jamString);
                pendingIntent = PendingIntent.getBroadcast(this, (int) Math.round((double) idTarget / 143551L), intent, PendingIntent.FLAG_IMMUTABLE);

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                finish();
                Toast.makeText(getApplicationContext(), "Data disimpan", Toast.LENGTH_SHORT).show();
            } catch (SQLException | ParseException e) {
                Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private String convertTanggal() throws ParseException {
        String tanggal = String.valueOf(binding.tanggalTarget.getText()).trim();
        return tanggal;
    }

    private String convertJam() throws ParseException {
        String jam = String.valueOf(binding.jamTarget.getText()).trim();
        return jam;
    }

    private void showDialogTime() {
        new CustomDateTimePicker(this, new CustomDateTimePicker.ICustomDateTimeListener() {

            @Override
            public void onSet(@NonNull Dialog dialog, @NonNull Calendar calendar, @NonNull Date date, int i, @NonNull String s, @NonNull String s1, int i1, int i2, @NonNull String s2, @NonNull String s3, int i3, int i4, int i5, int i6, @NonNull String s4) {
                binding.tanggalTarget.setText(new SimpleDateFormat(pattern, Locale.US).format(date));
                binding.jamTarget.setText(new SimpleDateFormat(pattern2, Locale.US).format(date));
            }

            @Override
            public void onCancel() {

            }
        }).showDialog();
    }
}