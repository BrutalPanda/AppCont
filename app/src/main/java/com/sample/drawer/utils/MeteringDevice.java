package com.sample.drawer.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Slava-laptop on 27.07.2015.
 */
public class MeteringDevice implements Parcelable {
    public int id;
    public String name;
    public String service;
    public String place;
    public String type;
    public String factory_num;
    public String accuracy;
    public String next_check;
    public String prev_reading;
    public String type_reading;
    public String date_reading;
    public String cur_reading;
    public String comment;
    public int passed;

    public MeteringDevice(int _id, String _name,String _service,
            String _place,
            String _type,
            String _factory_num,
            String _accuracy,
            String _next_check,
            String _prev_reading,
            String _type_reading,
            String _date_reading,
            String _cur_reading, String _comment, int _passed){
        this.id=_id;
        this.name = _name;
        this.accuracy = _accuracy;
        this.cur_reading = _cur_reading;
        this.date_reading = _date_reading;
        this.factory_num = _factory_num;
        this.next_check = _next_check;
        this.place = _place;
        this.prev_reading = _prev_reading;
        this.service = _service;
        this.type = _type;
        this.type_reading = _type_reading;
        this.comment = _comment;
        this.passed = _passed;
    }

    public MeteringDevice (HashMap <String,String> data){
        this.accuracy = data.get("Accuracy");
        this.cur_reading = data.get("CurReading");
        this.date_reading = data.get("DateReading");
        this.factory_num = data.get("FacNum");
        this.next_check = data.get("NextCheck");
        this.place = data.get("Place");
        this.prev_reading = data.get("PrevReading");
        this.service = data.get("Service");
        this.type =  data.get("Type");
        this.type_reading = data.get("TypeReading");
        this.name = data.get("Name");
    }

    public int describeContents() {
        return 0;
    }

    // упаковываем объект в Parcel
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(type_reading);
        parcel.writeString(type);
        parcel.writeString(service);
        parcel.writeString(prev_reading);
        parcel.writeString(place);
        parcel.writeString(next_check);
        parcel.writeString(factory_num);
        parcel.writeString(date_reading);
        parcel.writeString(cur_reading);
        parcel.writeString(accuracy);
        parcel.writeString(comment);
        parcel.writeInt(passed);

    }

    public static final Parcelable.Creator<MeteringDevice> CREATOR = new Parcelable.Creator<MeteringDevice>() {
        // распаковываем объект из Parcel
        public MeteringDevice createFromParcel(Parcel in) {
            return new MeteringDevice(in);
        }

        public MeteringDevice[] newArray(int size) {
            return new MeteringDevice[size];
        }
    };

    private MeteringDevice (Parcel parcel){
        id = parcel.readInt();
        name = parcel.readString();
        type_reading = parcel.readString();
        type = parcel.readString();
        service = parcel.readString();
        prev_reading = parcel.readString();
        place = parcel.readString();
        next_check = parcel.readString();
        factory_num = parcel.readString();
        date_reading = parcel.readString();
        cur_reading = parcel.readString();
        accuracy = parcel.readString();
        comment = parcel.readString();
        passed = parcel.readInt();
    }
}
