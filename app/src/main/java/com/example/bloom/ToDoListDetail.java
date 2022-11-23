package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bloom.databinding.ActivityToDoListDetailBinding;

import java.util.Objects;

public class ToDoListDetail extends AppCompatActivity {

    private ActivityToDoListDetailBinding binding;
    private SQLiteDatabase db;
    private TaskData taskData;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToDoListDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button back = findViewById(R.id.back_todolist_detail);

        db = (new DBHelper(this)).getWritableDatabase();

        getSupportActionBar().hide();

        taskData = getIntent().getParcelableExtra("TASK_DATA");

        if (Objects.equals(taskData.tag, "Important")) {
            binding.tagType.setCardBackgroundColor(Color.parseColor("#FFFED8B6"));
        } else {
            binding.tagType.setCardBackgroundColor(Color.parseColor("#FFDEFEB6"));
        }

        binding.tagText.setText(taskData.tag);
        binding.title.setText(taskData.nama);
        binding.tanggal.setText(taskData.tanggal);
        binding.jam.setText(taskData.jam);
        binding.desc.setText(taskData.deskripsi);
        binding.buttondelete.setOnClickListener(view -> {
            String[] params = new String[]{ taskData.id };
            Cursor result = db.rawQuery("DELETE FROM tbl_Task WHERE _id = ?", params);
            result.getCount();
            result.close();

            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, (int) Math.round((double) (Long.parseLong(taskData.id) / 143551L)), intent, 0);

            if (alarmManager == null) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }

            alarmManager.cancel(pendingIntent);

            finish();
            Toast.makeText(getApplicationContext(), "Task \"" + taskData.nama + "\" telah terhapus", Toast.LENGTH_SHORT).show();
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}