package com.example.bloom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class OnboardActivity extends AppCompatActivity {
    int Counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        getSupportActionBar().hide();
        ImageView gambarutama = (ImageView) findViewById(R.id.imageView5);
        TextView judulawal = (TextView) findViewById(R.id.textView);
        TextView keterangan = (TextView) findViewById(R.id.textView2);
        FrameLayout next = (FrameLayout) findViewById(R.id.tombol);
        TextView skip = (TextView) findViewById(R.id.skip);
        View chooser1 = (View) findViewById(R.id.chooser1);
        View chooser2 = (View) findViewById(R.id.chooser2);
        View chooser3 = (View) findViewById(R.id.chooser3);
        Button button = (Button) findViewById(R.id.button);
        button.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OnboardActivity.this, LoginActivity.class);

                startActivity(i);


                finish();
            }
        });



        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Counter++;
                if (Counter == 0){
                    gambarutama.setImageResource(R.drawable.ic_man_writing_book_on_deskook_2194236_0);
                    judulawal.setText(getString(R.string.plan_your_day_and_n_become_a_better_you));
                    keterangan.setText(getString(R.string.plan_and_organize_your_life_to_n_archive_your_strengths_n_challenge_and_goals));

                } else if(Counter == 1){
                    gambarutama.setImageResource(R.drawable.onboard2);
                    judulawal.setText(getString(R.string.stay_focus_and_nmotivated));
                    keterangan.setText(getString(R.string.stay_focus_while_we_track_your_nday_to_motivated_you_to_reach_nyour_goals));
                    chooser2.setBackgroundResource(R.drawable.chooser2);
                    chooser1.setBackgroundResource(R.drawable.chooser);

                } else if(Counter == 2) {
                    gambarutama.setImageResource(R.drawable.super_hero);
                    judulawal.setText(getString(R.string.reach_your_goals));
                    keterangan.setText(getString(R.string.it_s_now_or_never_there_s_nothing_better_than_achieving_your_goals_whatever_they_might_be));
                    skip.setVisibility(View.GONE);
                    chooser1.setVisibility((View.GONE));
                    chooser2.setVisibility((View.GONE));
                    chooser3.setVisibility((View.GONE));
                    next.setVisibility((View.GONE));
                    button.setVisibility(View.VISIBLE);
                }
            }
        });

    };
}