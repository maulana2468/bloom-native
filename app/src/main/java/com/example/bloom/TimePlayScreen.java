package com.example.bloom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class TimePlayScreen extends AppCompatActivity implements RvInterface {
    private RecyclerView rvTimer;
    private ListTimerAdapter listTimerAdapter;
    private SQLiteDatabase db;
    private Cursor cursor;
    ArrayList<TimerPomodoro> dataTimer = new ArrayList<>();
    private ImageButton plus;
    private ImageButton minus;
    private int[] minutesInstant = new int[1];
    private int[] breaksInstant = new int[1];
    private CheckBox skipBreaks;
    private TextView timerInstant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_play_sceen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        db = (new DBHelper(this)).getWritableDatabase();
        Button back = findViewById(R.id.back_timeplay_screen);
        //timer atas
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        skipBreaks = findViewById(R.id.skip_breaks);
        timerInstant = findViewById(R.id.time_value);
        minutesInstant[0] = 15;
        timerInstant.setText(String.valueOf(15));
        skipBreaks.setChecked(false);
        skipBreaks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    skipBreaks.setChecked(true);

                } else {
                    skipBreaks.setChecked(false);

                }
                minutesInstant[0] = 15;
                timerInstant.setText(String.valueOf(15));
                breaksInstant[0] = 0;
            }
        });

        plus.setOnClickListener(view -> {
            if(minutesInstant[0] < 240){
                minutesInstant[0] = minutesInstant[0] + 15;
                setBreaks();
            }
        });
        minus.setOnClickListener(view -> {
            if(minutesInstant[0] > 15){
                minutesInstant[0] = minutesInstant[0] - 15;
                setBreaks();
            }
        });
        LinearLayout startQuick = findViewById(R.id.start_Quick);
        startQuick.setOnClickListener(view -> {
            Intent i = new Intent(TimePlayScreen.this, TimePlay.class);
            TimerPomodoro data = new TimerPomodoro();
            data.id = "0";
            data.batas = breaksInstant[0];
            data.durasi = minutesInstant[0];
            data.nama = "Instant Timer";
            i.putExtra("TIMER", data);

            startActivity(i);
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        rvTimer = findViewById(R.id.rv_timerlist);
        rvTimer.setLayoutManager(new LinearLayoutManager(this));
        rvTimer.setHasFixedSize(false);

        listTimerAdapter = new ListTimerAdapter(dataTimer,this);
        rvTimer.setAdapter(listTimerAdapter);

        FloatingActionButton tambah_timer = (FloatingActionButton) findViewById(R.id.tambah_timer);
        getDataTimer();

        tambah_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialogPopup();

            }
        });

        listTimerAdapter.setOnItemClickCallback(new ListTimerAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(TimerPomodoro data) {
                Intent i = new Intent(TimePlayScreen.this, TimePlay.class);
                i.putExtra("TIMER", data);

                startActivity(i);
            }
        });
    }
    private void setBreaks(){
        if (minutesInstant[0] <= 240 ){
            timerInstant.setText(String.valueOf(minutesInstant[0]));
            if (skipBreaks.isChecked()){
                if (minutesInstant[0] == 15|| minutesInstant[0] == 30){
                    breaksInstant[0] = 0;

                } else if (minutesInstant[0] == 45|| minutesInstant[0] == 60){
                    breaksInstant[0] = 1;

                } else if (minutesInstant[0] == 75|| minutesInstant[0] == 90){
                    breaksInstant[0] = 2;

                } else if (minutesInstant[0] == 105|| minutesInstant[0] == 120) {
                    breaksInstant[0] = 3;

                } else if (minutesInstant[0] == 135) {
                    breaksInstant[0] = 4;

                } else if (minutesInstant[0] == 150|| minutesInstant[0] == 165 ) {
                    breaksInstant[0] = 5;

                } else if (minutesInstant[0] == 180|| minutesInstant[0] == 195 ) {
                    breaksInstant[0] = 6;

                } else if (minutesInstant[0] == 210) {
                    breaksInstant[0] = 7;

                } else if (minutesInstant[0] == 225|| minutesInstant[0] == 240 ) {
                    breaksInstant[0] = 8;

                }
            }
        } else {
            minutesInstant[0] = 15;
            timerInstant.setText(String.valueOf(15));
            breaksInstant[0] = 0;

        }
    }

    private void getDataTimer(){
        try {
            ArrayList<TimerPomodoro> newData = new ArrayList<>();

            cursor = db.rawQuery("SELECT * FROM " + DBHelper.TBL_TIMER,null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
               cursor.moveToPosition(i);
               TimerPomodoro timer = new TimerPomodoro();
               timer.id = cursor.getString(0);
               timer.nama = cursor.getString(1);
               timer.batas = cursor.getInt(2);
               timer.durasi = cursor.getInt(3);

               newData.add(timer);
            }

            listTimerAdapter.refreshData(newData);
        }
        catch (SQLException message){
            toastError(message.getMessage());
        }
        finally {
            cursor.close();
        }
    }

    private void showDialogPopup(){
        final int[] breaks = {0};
        final int[] minutes = {15};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.bg_popup);

        Button setTimer = dialogView.findViewById(R.id.batas_waktu);
        Switch isBreaksOn = dialogView.findViewById(R.id.switchpopup);
        Button cancel = dialogView.findViewById(R.id.cancel_popup);
        EditText title = dialogView.findViewById(R.id.inputTitle);
        Button save = dialogView.findViewById(R.id.save_popup);
        TextView breaksButton = dialogView.findViewById(R.id.breaks);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        isBreaksOn.setChecked(false);

        isBreaksOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isBreaksOn.setChecked(b);
                minutes[0] = 15;
                setTimer.setText(minutes[0] + " minutes");
                breaks[0] = 0;
                breaksButton.setText(breaks[0] + " breaks");
            }
        });


        setTimer.setOnClickListener(view -> {
            if (minutes[0] <= 240 ){
                minutes[0] = minutes[0] + 15;
                setTimer.setText(minutes[0] + " minutes");
                if (isBreaksOn.isChecked()){
                    if (minutes[0] == 15|| minutes[0] == 30){
                        breaks[0] = 0;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 45|| minutes[0] == 60){
                        breaks[0] = 1;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 75|| minutes[0] == 90){
                        breaks[0] = 2;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 105|| minutes[0] == 120) {
                        breaks[0] = 3;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 135) {
                        breaks[0] = 4;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 150|| minutes[0] == 165 ) {
                        breaks[0] = 5;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 180|| minutes[0] == 195 ) {
                        breaks[0] = 6;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 210) {
                        breaks[0] = 7;
                        breaksButton.setText(breaks[0] + " breaks");
                    } else if (minutes[0] == 225|| minutes[0] == 240 ) {
                        breaks[0] = 8;
                        breaksButton.setText(breaks[0] + " breaks");
                    }
                }
            } else {
                minutes[0] = 15;
                setTimer.setText(minutes[0] + " minutes");
                breaks[0] = 0;
                breaksButton.setText(breaks[0] + " breaks");

            }
        });
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar =  Calendar.getInstance();
                    ContentValues values = new ContentValues();
                    values.put("_id",calendar.getTimeInMillis());
                    values.put("nama",title.getText().toString());
                    values.put("batas", breaks[0]);
                    values.put("durasi",minutes[0]);
                    db.insert("tbl_Timer","_id", values);

                    getDataTimer();
                }catch (SQLException message){

                }finally {
                    dialog.dismiss();

                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    private void showBottomSheetDialog(String heroID) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_timer_delete,null);
        TextView deleteButton = view.findViewById(R.id.timer_delete_button);

        bottomSheetDialog.setContentView(view);
        deleteButton.setOnClickListener(view1 -> {
            try{
                Log.d("TimePlayScreen", "DATA TO DELETED: " + heroID);

                String table = DBHelper.TBL_TIMER;
                String whereClause = "_id=?";
                String[] whereArgs = new String[] {heroID};
                db.delete(table, whereClause, whereArgs);

                getDataTimer();
            } catch (SQLException message) {
                toastError(message.getMessage());
            } finally {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onItemLongClick(String heroID) {
        showBottomSheetDialog(heroID);
    }

    public void toastError(String e) {
        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        db.close();
    }
}