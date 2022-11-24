package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {

    private Cursor cursor;
    private SQLiteDatabase db;
    private ListTaskDataAdapter listTaskDataAdapter;
    private RecyclerView rvListDashboard;

    int isChecked(int a) {
        return (a == 0) ? 1 : 0;
    }

    public void toastError(String e) {
        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataTask();
    }

    private void getDataTask() {
        try {
            ArrayList<TaskData> data = new ArrayList<>();

            cursor = db.rawQuery("SELECT * FROM " + DBHelper.TBL_TASK, null);

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                TaskData task = new TaskData();
                task.id = cursor.getString(0);
                task.isDone = cursor.getInt(1);
                task.nama = cursor.getString(2);
                task.tag = cursor.getString(3);
                task.deskripsi = cursor.getString(4);
                task.tanggal = cursor.getString(5);
                task.jam = cursor.getString(6);

                data.add(task);
            }

            listTaskDataAdapter.refreshData(data);

            listTaskDataAdapter.setOnItemClickCallback(new ListTaskDataAdapter.OnItemClickCallback() {
                @Override
                public void onItemClicked(TaskData data) {
                    Intent intent = new Intent(getApplicationContext(), ToDoListDetail.class);
                    intent.putExtra("TASK_DATA", data);
                    startActivity(intent);
                }

                @Override
                public void onCheckBoxClicked(TaskData data) {
                    String[] params = new String[]{ String.valueOf(isChecked(data.isDone)), data.id };
                    Cursor result = db.rawQuery("UPDATE tbl_Task SET isDone = ? WHERE _id = ?", params);
                    Log.d("ToDoListMainScreen", String.valueOf(result.getCount()));
                    result.getCount();
                    result.close();
                    getDataTask();
                }
            });
        }
        catch (SQLException message){
            toastError(message.getMessage());
        } finally {
            cursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        createNotificationDetail();

        getSupportActionBar().hide();

        db = (new DBHelper(this)).getWritableDatabase();
        LinearLayout TimerLayout = (LinearLayout) findViewById(R.id.timerLayout);
        TextView username = (TextView) findViewById(R.id.username);
        TextView email = (TextView) findViewById(R.id.email);
        ImageButton setting = (ImageButton) findViewById(R.id.setting);
        LinearLayout toDoListLayout  = (LinearLayout) findViewById(R.id.todolistLayout);
        rvListDashboard = findViewById(R.id.rv_task_dashboard);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        username.setText(auth.getCurrentUser().getDisplayName());
        email.setText(auth.getCurrentUser().getEmail());


        rvListDashboard.setLayoutManager(new LinearLayoutManager(this));
        rvListDashboard.setHasFixedSize(false);
        listTaskDataAdapter = new ListTaskDataAdapter(new ArrayList<>());

        rvListDashboard.setAdapter(listTaskDataAdapter);

        TimerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, TimePlayScreen.class);

                startActivity(i);

            }
        });

        toDoListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, ToDoListMainScreen.class);

                startActivity(i);

            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, Profile.class);

                startActivity(i);

            }
        });

        getDataTask();
    }

    private void createNotificationDetail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikasi Timer";
            String description = "Notifikasi setelah berhenti";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("channel02", name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}