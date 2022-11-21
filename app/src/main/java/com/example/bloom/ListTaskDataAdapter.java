package com.example.bloom;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;


public class ListTaskDataAdapter extends RecyclerView.Adapter<ListTaskDataAdapter.ListViewHolder> {
    private ArrayList<TaskData> listHero;
    String pattern3 = "E, dd MMM yyyy KK:mm a";
    private OnItemClickCallback onItemClickCallback;

    void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    ListTaskDataAdapter(ArrayList<TaskData> list){
        this.listHero = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {

        TaskData hero = listHero.get(position);

        holder.title.setText(hero.nama);
        holder.tag.setText(hero.tag);
        holder.date.setText(hero.tanggal);
        holder.isDone.setChecked(hero.isDone != 0);
        holder.itemView.setOnClickListener(v ->
            onItemClickCallback.onItemClicked(listHero.get(holder.getAdapterPosition()))
        );
        holder.isDone.setOnClickListener(v ->
            onItemClickCallback.onCheckBoxClicked(listHero.get(holder.getAdapterPosition()))
        );
        CardView view = holder.tagType;
        if (Objects.equals(hero.tag, "Important")) {
            view.setCardBackgroundColor(Color.parseColor("#FFFED8B6"));
        } else {
            view.setCardBackgroundColor(Color.parseColor("#FFDEFEB6"));
        }
    }


    @Override
    public int getItemCount() {
        return listHero.size();
    }


    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView tag;
        TextView date;
        CheckBox isDone;
        CardView tagType;

        ListViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_task);
            tag = itemView.findViewById(R.id.tag_text);
            date = itemView.findViewById(R.id.detail_task);
            isDone = itemView.findViewById(R.id.checkbox_data);
            tagType = itemView.findViewById(R.id.tag_type);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(TaskData data);
        void onCheckBoxClicked(TaskData data);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData(ArrayList<TaskData> data) {
        listHero.clear();
        listHero = data;
        notifyDataSetChanged();
    }
}