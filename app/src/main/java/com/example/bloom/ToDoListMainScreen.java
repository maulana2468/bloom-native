package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

public class ToDoListMainScreen extends AppCompatActivity {

    private Calendar lastDayInCalendar = Calendar.getInstance(Locale.ENGLISH);
    private SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);

    private Calendar currentDate = Calendar.getInstance(Locale.ENGLISH);
    private int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
    private int currentMonth = currentDate.get(Calendar.MONTH);
    private int currentYear = currentDate.get(Calendar.YEAR);
    private SQLiteDatabase db;

    private int selectedDay = currentDay;
    private int selectedMonth = currentMonth;
    private int selectedYear = currentYear;
    private FloatingActionButton create_Task;
    private ListTaskDataAdapter listTaskDataIAdapter;
    private ListTaskDataAdapter listTaskDataBAdapter;

    private ArrayList<Date> dates = new ArrayList();

    private RecyclerView rvCalendar;
    private RecyclerView rvImportant;
    private RecyclerView rvBasic;
    private TextView bulanText;
    String pattern2 = "E, dd MMM yyyy";

    private Date selectedDateNow = new Date();
    private Cursor cursor;

    @Override
    protected void onResume() {
        super.onResume();
        getDataByDate();
    }

    private void getDataByDate() {
        try {
            ArrayList<TaskData> importantData = new ArrayList<>();
            ArrayList<TaskData> basicData = new ArrayList<>();

            String[] params = new String[]{ new SimpleDateFormat(pattern2, Locale.US).format(selectedDateNow) };
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.TBL_TASK + " WHERE tanggal = ?", params);

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

                if (task.tag.equals("Important")) {
                    importantData.add(task);
                } else {
                    basicData.add(task);
                }
            }

            Log.d("ToDoListMainScreen", importantData.toString());
            Log.d("ToDoListMainScreen", basicData.toString());

            listTaskDataIAdapter.refreshData(importantData);
            listTaskDataBAdapter.refreshData(basicData);

            Intent intent = new Intent(this, ToDoListDetail.class);

            listTaskDataBAdapter.setOnItemClickCallback(new ListTaskDataAdapter.OnItemClickCallback() {
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
                    result.getCount();
                    result.close();
                    getDataByDate();
                }
            });

            listTaskDataIAdapter.setOnItemClickCallback(new ListTaskDataAdapter.OnItemClickCallback() {
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
                    getDataByDate();
                }
            });
        }
        catch (SQLException message){
            toastError(message.getMessage());
        } finally {
            cursor.close();
        }
    }

    int isChecked(int a) {
        return (a == 0) ? 1 : 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_main_screen);
        getSupportActionBar().hide();

        db = (new DBHelper(this)).getWritableDatabase();

        rvImportant = findViewById(R.id.rv_task_important);
        rvBasic = findViewById(R.id.rv_task_basic);

        rvImportant.setLayoutManager(new LinearLayoutManager(this));
        rvBasic.setLayoutManager(new LinearLayoutManager(this));

        rvImportant.setHasFixedSize(false);
        rvBasic.setHasFixedSize(false);

        listTaskDataIAdapter = new ListTaskDataAdapter(new ArrayList<>());
        listTaskDataBAdapter = new ListTaskDataAdapter(new ArrayList<>());

        rvImportant.setAdapter(listTaskDataIAdapter);
        rvBasic.setAdapter(listTaskDataBAdapter);

        rvCalendar = findViewById(R.id.calendar_recycler_view);
        bulanText = findViewById(R.id.textView2);
        Button prev = findViewById(R.id.prevB);
        Button next = findViewById(R.id.nextB);
        create_Task = findViewById(R.id.create_Task);

        ConstraintLayout back = findViewById(R.id.back_todolist_mainscreen);


        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvCalendar);

        lastDayInCalendar.add(Calendar.MONTH, 6);

        setUpCalendar(null);

        prev.setOnClickListener(view -> {
            if (cal.after(currentDate)) {
                cal.add(Calendar.MONTH, -1);
                if (cal == currentDate)
                    setUpCalendar(null);
                else
                    setUpCalendar(cal);
            }
        });

        next.setOnClickListener(view -> {
            if (cal.before(lastDayInCalendar)) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendar(cal);
            }
        });

        create_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ToDoListMainScreen.this, CreateToDoList.class);

                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    void setUpCalendar(@Nullable Calendar changeMonth) {
        // first part
        bulanText.setText(sdf.format(cal.getTime()));
        //txt_current_month!!.text = sdf.format(cal.time)
        Calendar monthCalendar = (Calendar) cal.clone();
        int maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (changeMonth != null) {
            selectedDay = changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else {
            selectedDay = currentDay;
        }

        if (changeMonth != null) {
            selectedMonth = changeMonth.get(Calendar.MONTH);
        } else {
            selectedMonth = currentMonth;
        }

        if (changeMonth != null) {
            selectedYear = changeMonth.get(Calendar.YEAR);
        } else {
            selectedYear = currentYear;
        }

        // second part
        int currentPosition = 0;
        dates.clear();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        while (dates.size() < maxDaysInMonth) {
            if (monthCalendar.get(Calendar.DAY_OF_MONTH) == selectedDay)
                currentPosition = dates.size();
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //third part
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCalendar.setLayoutManager(layoutManager);
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, dates, currentDate, changeMonth);
        rvCalendar.setAdapter(calendarAdapter);

        if (currentPosition > 2) {
            rvCalendar.scrollToPosition(currentPosition - 3);
        } else if (maxDaysInMonth - currentPosition < 2) {
            rvCalendar.scrollToPosition(currentPosition);
        } else {
            rvCalendar.scrollToPosition(currentPosition);
        }

        calendarAdapter.setOnItemClickListener(new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Calendar clickCalendar = Calendar.getInstance();
                clickCalendar.setTime(dates.get(position));
                selectedDay = clickCalendar.get(Calendar.DAY_OF_MONTH);
                selectedDateNow = dates.get(position);
                getDataByDate();
            }
        });


    }

    public void toastError(String e) {
        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
    }
}