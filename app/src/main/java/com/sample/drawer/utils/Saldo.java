package com.sample.drawer.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Slava-laptop on 04.08.2015.
 */
public class Saldo implements Parcelable {
    public ArrayList<String> cell;
    public Saldo (ArrayList<String> _cell){
        this.cell = _cell;
    }

    public int describeContents() {
        return 0;
    }

    // упаковываем объект в Parcel
    public void writeToParcel(Parcel parcel, int flags) {
        for (int i=0;i<cell.size();i++){
            parcel.writeString(cell.get(i));
        }

    }

    public static final Parcelable.Creator<Saldo> CREATOR = new Parcelable.Creator<Saldo>() {
        // распаковываем объект из Parcel
        public Saldo createFromParcel(Parcel in) {
            return new Saldo(in);
        }

        public Saldo[] newArray(int size) {
            return new Saldo[size];
        }
    };

    private Saldo (Parcel parcel){
        cell = new ArrayList<String>();
        for (int i=0;i<parcel.dataSize();i++){
            cell.add(parcel.readString());
        }
    }

    public int getSize(){return cell.size();}
}
