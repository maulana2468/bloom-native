package com.example.bloom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Date> data;
    private Calendar currentDate;
    private Calendar changeMonth;
    private @Nullable OnItemClickListener mListener;
    private int index = -1;
    private boolean selectCurrentDate = true;
    private int currentMonth;
    private int currentYear;
    private int currentDay;
//    private int selectedDay = sd();
//    private int selectedMonth = sm();
//    private int selectedYear = sy();

    public CalendarAdapter(Context context, ArrayList<Date> data, Calendar currentDate, Calendar changeMonth) {
        this.context = context;
        this.data = data;
        this.currentDate = currentDate;
        this.changeMonth = changeMonth;
        currentMonth = currentDate.get(Calendar.MONTH);
        currentYear = currentDate.get(Calendar.YEAR);
        currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateDay;
        OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);
            dateDay = itemView.findViewById(R.id.date_card);
            listener = mListener;
        }
    }

    int sd() {
        if (changeMonth != null) {
            return changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH);
        } else {
            return currentDay;
        }
    }

    int sm() {
        if (changeMonth != null) {
            return changeMonth.get(Calendar.MONTH);
        } else {
            return currentMonth;
        }
    }

    int sy() {
        if (changeMonth != null) {
            return changeMonth.get(Calendar.YEAR);
        } else {
            return currentYear;
        }
    }

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.date_card, parent, false), mListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        cal.setTime(data.get(position));

        int displayMonth = cal.get(Calendar.MONTH);
        int displayYear= cal.get(Calendar.YEAR);
        int displayDay = cal.get(Calendar.DAY_OF_MONTH);

        String hari = "";

        hari = String.valueOf(data.get(position)).split(" ")[0];
//        try {
//            //Date dayInWeek = sdf.parse(String.valueOf(cal.getTime()));
//            //sdf.applyPattern("EEE");
//            hari = String.valueOf(data.get(position)).split(" ")[0];
//            //Log.d("ToDoListMainScreen", String.valueOf(dayInWeek));
//        } catch (ParseException e) {
//            Log.v("Exception", e.getLocalizedMessage());
//        }

        String tanggal  = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

        String result = hari + "\n" + tanggal;

        holder.dateDay.setText(result);

        if (displayYear >= currentYear)
            if (displayMonth >= currentMonth || displayYear > currentYear)
                if (displayDay >= currentDay || displayMonth > currentMonth || displayYear > currentYear) {
                    holder.dateDay.setOnClickListener(view -> {
                        index = position;
                        selectCurrentDate = false;
                        holder.listener.onItemClick(position);
                        notifyDataSetChanged();
                    });

                    if (index == position)
                        makeItemSelected(holder);
                    else {
                        if (displayDay == sd()
                                && displayMonth == sm()
                                && displayYear == sy()
                                && selectCurrentDate) {
                            makeItemSelected(holder);
                            holder.listener.onItemClick(position);
                        } else {
                            makeItemDefault(holder);
                        }
                    }
                } else makeItemDisabled(holder);
            else makeItemDisabled(holder);
        else makeItemDisabled(holder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    void  makeItemDisabled(ViewHolder holder) {
        holder.dateDay.setTextColor(Color.BLACK);
        holder.dateDay.setBackgroundResource(R.drawable.rectangle1_copy);
        holder.dateDay.setClickable(false);
    }

    void makeItemSelected(ViewHolder holder) {
        holder.dateDay.setTextColor(Color.WHITE);
        holder.dateDay.setBackgroundResource(R.drawable.rectangle1_copy);
        holder.dateDay.setClickable(false);
    }

    void  makeItemDefault(ViewHolder holder) {
        holder.dateDay.setTextColor(Color.BLACK);
        holder.dateDay.setBackgroundResource(R.drawable.rectangle1);
        holder.dateDay.setClickable(true);
    }
}

