package com.example.bloom;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Timer;


public class ListTimerAdapter extends RecyclerView.Adapter<ListTimerAdapter.ListViewHolder> {
    private ArrayList<TimerPomodoro> listHero;
    private OnItemClickCallback onItemClickCallback;
    private final RvInterface rvInterface;

    void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    ListTimerAdapter(ArrayList<TimerPomodoro> list, RvInterface rvInterface) {
        this.listHero = list;
        this.rvInterface = rvInterface;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timerlist, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {

        TimerPomodoro hero = listHero.get(position);

        holder.nama.setText(hero.nama);
        holder.durasi.setText(String.valueOf(hero.durasi));
        holder.batas.setText(String.valueOf(hero.batas));

        holder.playtimer.setOnClickListener(v -> onItemClickCallback.onItemClicked(listHero.get(holder.getAdapterPosition())));
        holder.itemView.setOnLongClickListener(v -> {
            rvInterface.onItemLongClick(hero.id);
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return listHero.size();
    }


    class ListViewHolder extends RecyclerView.ViewHolder {
        TextView nama;
        TextView batas;
        TextView durasi;
        ImageButton playtimer;

        ListViewHolder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.task_coding);
            batas = itemView.findViewById(R.id.detail_breaks);
            durasi = itemView.findViewById(R.id.durasi_timer);
            playtimer = itemView.findViewById(R.id.playtimer);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(TimerPomodoro data);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData(ArrayList<TimerPomodoro> data) {
        listHero.clear();
        listHero = data;
        notifyDataSetChanged();
    }
}

interface RvInterface {
    void onItemLongClick(String heroID);
}