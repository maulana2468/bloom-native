package com.example.bloom;

import android.os.Parcel;
import android.os.Parcelable;

public class TimerPomodoro implements Parcelable {
    public String id;
    public String nama;
    public int batas;
    public int durasi;

    public TimerPomodoro() {

    }

    protected TimerPomodoro(Parcel in) {
        id = in.readString();
        nama = in.readString();
        batas = in.readInt();
        durasi = in.readInt();
    }

    public static final Creator<TimerPomodoro> CREATOR = new Creator<TimerPomodoro>() {
        @Override
        public TimerPomodoro createFromParcel(Parcel in) {
            return new TimerPomodoro(in);
        }

        @Override
        public TimerPomodoro[] newArray(int size) {
            return new TimerPomodoro[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nama);
        parcel.writeInt(batas);
        parcel.writeInt(durasi);
    }
}
