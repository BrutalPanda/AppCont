package com.sample.drawer.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Slava-laptop on 27.07.2015.
 */
public class Unit implements Parcelable {
    //Главная инфа
    public String FIO;
    public String area;
    public String room;
    public String number_of_living;
    public String account;
    public String address;
    public String tel1;
    public String tel2;
    public String tel3;
    public String email;
    //Инфа по Сальдо!
    public ArrayList<Saldo> saldo;
    //Инфа по приборам учета
    public ArrayList<MeteringDevice> devices;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(FIO);
        parcel.writeString(area);
        parcel.writeString(room);
        parcel.writeString(number_of_living);
        parcel.writeString(account);
        parcel.writeString(address);
        parcel.writeString(tel1);
        parcel.writeString(tel2);
        parcel.writeString(tel3);
        parcel.writeString(email);
        parcel.writeTypedList(devices);
        parcel.writeTypedList(saldo);


    }

    public static final Parcelable.Creator<Unit> CREATOR = new Parcelable.Creator<Unit>() {
        // распаковываем объект из Parcel
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    private Unit (Parcel parcel){
        devices = new ArrayList<MeteringDevice>();
        saldo = new ArrayList<Saldo>();
        FIO = parcel.readString();
        area = parcel.readString();
        room = parcel.readString();
        number_of_living = parcel.readString();
        account = parcel.readString();
        address = parcel.readString();
        tel1 = parcel.readString();
        tel2 = parcel.readString();
        tel3 = parcel.readString();
        email = parcel.readString();
        parcel.readTypedList(devices, MeteringDevice.CREATOR);
        parcel.readTypedList(saldo, Saldo.CREATOR);

    }

    public Unit(){}



}
