package com.example.bloom;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskData implements Parcelable {
    public String id;
    public int isDone;
    public String nama;
    public String deskripsi;
    public String tanggal;
    public String tag;
    public String jam;

    TaskData() {

    }

    protected TaskData(Parcel in) {
        id = in.readString();
        isDone = in.readInt();
        nama = in.readString();
        deskripsi = in.readString();
        tanggal = in.readString();
        tag = in.readString();
        jam = in.readString();
    }

    public static final Creator<TaskData> CREATOR = new Creator<TaskData>() {
        @Override
        public TaskData createFromParcel(Parcel in) {
            return new TaskData(in);
        }

        @Override
        public TaskData[] newArray(int size) {
            return new TaskData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(isDone);
        parcel.writeString(nama);
        parcel.writeString(deskripsi);
        parcel.writeString(tanggal);
        parcel.writeString(tag);
        parcel.writeString(jam);
    }
}
