package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Random;

public class TimePlay extends AppCompatActivity {
    private ProgressBar progressBar;
    private TimerPomodoro dataTimer;
    private TextView time;
    private double currentTime;
    private CountDownTimer timer;
    private ImageButton toogle;
    private Boolean isPlay;
    private int currentBreaks;
    private int totalBreaksSession;
    private double sessionInMinutes;
    private TextView textBreaks;
    private Long sessionCount = 1000L;
    private String TAG = "TimePlay";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_play);
        getSupportActionBar().hide();
        TextView judul = findViewById(R.id.judul) ;
        time = findViewById(R.id.timerlaknat);
        dataTimer = getIntent().getParcelableExtra("TIMER");

        textBreaks = findViewById(R.id.breaksText);
        Button back = findViewById(R.id.back_time_play);

        judul.setText(dataTimer.nama);
        //currentTime = 0L;
        currentBreaks = 0;

        if (dataTimer.batas > 0) {
            currentBreaks = 1;
            totalBreaksSession = dataTimer.batas + 1;
            textBreaks.setText(currentBreaks + " of "+ totalBreaksSession);
            sessionInMinutes = dataTimer.durasi * 1.0 / totalBreaksSession;
        }
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        toogle = (ImageButton) findViewById(R.id.toogle);

        startTimer((long) dataTimer.durasi);
        toogle.setOnClickListener(view -> {
            if (isPlay){
                stopTimer();
            } else {
                startTimer(currentTime);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void startTimer(double count){
        toogle.setImageResource(R.drawable.pause);
        isPlay = true;

        timer = new CountDownTimer((long) (count * 60L * 1000L), 200) {

            @Override
            public void onTick(long millisUntilFinished) {
                currentTime = millisUntilFinished / 1000.0 / 60.0;
                int a = (int) (millisUntilFinished / 1000);
                sessionCount = sessionCount + 200L;
                int minutes = (a % 3600) / 60;
                int seconds = a % 60;

                String timeString = String.format("%02d:%02d", minutes, seconds);
                time.setText(timeString);
                update(a);
//                Log.d(TAG, String.valueOf(sessionCount));
//                Log.d(TAG, String.valueOf(sessionInMinutes * 60L * 1000L));
                if (dataTimer.batas > 0) {
                    if (sessionCount > sessionInMinutes * 60L * 1000L){
                        if (currentBreaks + 1 > totalBreaksSession) {
                            timer.cancel();
                            isPlay = false;
                            toogle.setVisibility(View.GONE);

                            setNotification(currentBreaks + " of "+ totalBreaksSession + " finished");

                            final DecimalFormat df = new DecimalFormat("0.0");
                            sharedPreferences = getSharedPreferences("HOURS", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("HOURS", df.format(dataTimer.durasi / 60.0));
                            editor.apply();
                        } else {
                            sessionCount = 0L;
                            stopTimer();
                            setNotification(currentBreaks + " of "+ totalBreaksSession + " finished");
                            currentBreaks = currentBreaks + 1;
                            textBreaks.setText(currentBreaks + " of "+ totalBreaksSession);
                        }
                    }
                }
            }

            @Override
            public void onFinish() {}
        };
        timer.start();
    }
    void stopTimer(){
        timer.cancel();
        isPlay = false;
        toogle.setImageResource(R.drawable.ic_play);
    }

    void update(int a) {

        progressBar.setProgress((int) ((a / (dataTimer.durasi*60.0)) * 100000),true);

    }


    private void setNotification(String detail) {
        createNotificationDetail();

        Notification.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this, "channel01");
        } else {
            notificationBuilder = new Notification.Builder(this);
        }

        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        notificationBuilder.setContentTitle("Bloom - Timer");
        notificationBuilder.setContentText(detail);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setAutoCancel(true);
        //notificationBuilder.setContentIntent(mainPendingIntent);
        notificationBuilder.setStyle(
                new Notification.BigTextStyle().setBigContentTitle("Bloom - Timer")
                        .bigText(detail)
        );
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(new Random().nextInt(), notificationBuilder.build());
    }

    private void createNotificationDetail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikasi Timer";
            String description = "Notifikasi setelah berhenti";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("channel01", name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}