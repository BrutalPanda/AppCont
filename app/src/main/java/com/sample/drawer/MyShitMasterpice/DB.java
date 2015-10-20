package com.sample.drawer.MyShitMasterpice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.sample.drawer.MainActivity;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Saldo;
import com.sample.drawer.utils.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Slava-laptop on 16.10.2015.
 */
public class DB extends SQLiteOpenHelper {

    SQLiteDatabase db;
    String SOURCE_FILE = "file";
    String CONTROLLER = "cont";
    String DISPATCHER = "disp";
    final String INFO_AREA = "area";
    final String INFO_ROOM = "room";
    final String INFO_TEL = "tel";
    final String INFO_EMAIL = "email";
    final String INFO_LIVING = "number_of_living";

    public DB(Context context){
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table abonent (id integer primary key autoincrement,name text, account text, address text, count_of_devs integer, passed integer);");
        db.execSQL("create table cont_info (id integer primary key autoincrement,type text,id_ab integer, value text);");
        db.execSQL("create table saldo (id integer primary key autoincrement,id_ab integer, row integer, col integer, value text);");
        db.execSQL("create table device (id integer primary key autoincrement,id_ab integer, name text, place text, service text, type text, factory_num,  accuracy text, next_check text, prev_reading integer, type_reading text, date_reading text, cur_reading integer, comment text, passed integer);");
        db.execSQL("create table result (id integer primary key autoincrement,id_ab integer, id_dev integer, reading integer, place text, comment text, date text);");
        db.execSQL("create table operators (id integer primary key autoincrement, name text, type text);");

    }

    public void test(){
        db = this.getReadableDatabase();
        Cursor c = db.query("abonent", null,null,null, null, null, null);
        if (c!=null){
            if (c.moveToFirst()) {
                do{
                    Log.d("row","1");
                }while (c.moveToNext());
            }
        }
    }

    public void upgradeDB(){
        this.onUpgrade(db,0,1);
    }

    public boolean setPassed(int id_dev, String comment, String place, int value, String date){
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("place", place);
        cv.put("cur_reading",value);
        cv.put("comment",comment);
        cv.put("passed",1);

        int updCount = db.update("device", cv, "id = ?", new String[] { Integer.toString(id_dev) });
        if(updCount != 1){
            return false;
        }
        int id_ab = 0;
        Cursor c_dev = db.query("device",null,"id = ?", new String[] { Integer.toString(id_dev)},null,null,null);
        if (c_dev!=null){
            if (c_dev.moveToFirst()){
                int id_abIndex = c_dev.getColumnIndex("id_ab");
                do{
                    id_ab = c_dev.getInt(id_abIndex);
                }while (c_dev.moveToNext());
            }
        }
        c_dev = db.query("device",null,"id_ab = ? AND passed = ?", new String[] { Integer.toString(id_ab),Integer.toString(1)},null,null,null);
        int counter = 0;
        if (c_dev!=null){
            if (c_dev.moveToFirst()){
                do{
                   counter++;
                }while (c_dev.moveToNext());
            }
        }
        Cursor c = db.query("abonent",null,"id = ?",new String[] { Integer.toString(id_ab)},null,null,null);
        int count_of_device = 0;
        if (c!=null){
            if (c.moveToFirst()){
                int countDevsIndex = c.getColumnIndex("count_of_devs");
                do{
                    count_of_device = c.getInt(countDevsIndex);
                }while (c.moveToNext());
            }
        }
        if (count_of_device == counter){
            cv.clear();
            cv.put("passed",1);
            updCount = db.update("abonent", cv, "id = ?", new String[] { Integer.toString(id_ab) });
        }
        //db.execSQL("create table result (id integer primary key autoincrement,id_ab integer, id_dev integer, reading integer, place text, comment text, date text);");
        ContentValues res = new ContentValues();
        res.put("id_ab", id_ab);
        res.put("id_dev", id_dev);
        res.put("reading", value);
        res.put("place", place);
        res.put("comment", comment);
        res.put("date", date);
        db.insert("result", null, res);
        return true;
    }

    public ArrayList<Unit> makeUnits(int passed){
        db = this.getWritableDatabase();
        ArrayList<Unit> Units = new ArrayList<Unit>();
        Cursor c = db.query("abonent", null, "passed = "+Integer.toString(passed),null, null, null, null);
        if (c!=null){
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex("id");
                int FIOIndex = c.getColumnIndex("name");
                int AccountIndex = c.getColumnIndex("account");
                int AddressIndex = c.getColumnIndex("address");
                int CountDevsIndex = c.getColumnIndex("count_of_devs");
                int PassedIndex = c.getColumnIndex("passed");
                do{
                    Unit Enity = new Unit();
                    Enity.account = c.getString(AccountIndex);
                    Enity.FIO = c.getString(FIOIndex);
                    Enity.id = c.getInt(idIndex);
                    Enity.address = c.getString(AddressIndex);
                    Enity.passed = c.getInt(PassedIndex);
                    Cursor c_info = db.query("cont_info", null, "id_ab = ?", new String[]{Integer.toString(c.getInt(idIndex))}, null, null, null);
                    if (c_info!=null){
                        if(c_info.moveToFirst()){
                            int type = c_info.getColumnIndex("type");
                            int value = c_info.getColumnIndex("value");
                            int telcnt = 0;
                            do{
                                switch (c_info.getString(type)){
                                    case INFO_TEL:
                                        if (telcnt == 0){
                                            Enity.tel1 = c_info.getString(value);
                                        }else if (telcnt == 1){
                                            Enity.tel2 = c_info.getString(value);
                                        }else if (telcnt == 2){
                                            Enity.tel3 = c_info.getString(value);
                                        }else if (telcnt == 3){
                                            Enity.tel4 = c_info.getString(value);
                                        }
                                        telcnt++;
                                        break;
                                    case INFO_EMAIL:
                                        Enity.email = c_info.getString(value);
                                        break;
                                    case INFO_AREA:
                                        Enity.area = c_info.getString(value);
                                        break;
                                    case INFO_LIVING:
                                        Enity.number_of_living = c_info.getString(value);
                                        break;
                                    case INFO_ROOM:
                                        Enity.room = c_info.getString(value);
                                        break;
                                }
                            }while(c_info.moveToNext());
                        }
                    }
                    if (c.getInt(CountDevsIndex)>0){
                        ArrayList<MeteringDevice> mds = new ArrayList<MeteringDevice>();
                        Cursor c_devs = db.query("device", null, "id_ab = ?", new String[]{Integer.toString(c.getInt(idIndex))}, null, null, null);
                        if (c_devs!=null){
                            if(c_devs.moveToFirst()){
                                int idDevIndex = c_devs.getColumnIndex("id");
                                int nameIndex = c_devs.getColumnIndex("name");
                                int placeIndex = c_devs.getColumnIndex("place");
                                int serviceIndex = c_devs.getColumnIndex("service");
                                int typeIndex = c_devs.getColumnIndex("type");
                                int fact_numIndex = c_devs.getColumnIndex("factory_num");
                                int accuracyIndex = c_devs.getColumnIndex("accuracy");
                                int next_checkIndex = c_devs.getColumnIndex("next_check");
                                int prev_readingIndex = c_devs.getColumnIndex("prev_reading");
                                int type_readingIndex = c_devs.getColumnIndex("type_reading");
                                int date_readingIndex = c_devs.getColumnIndex("date_reading");
                                int cur_readingIndex = c_devs.getColumnIndex("cur_reading");
                                int commentIndex = c_devs.getColumnIndex("comment");
                                int passedDevIndex = c_devs.getColumnIndex("passed");
                                do{
                                    MeteringDevice md = new MeteringDevice(
                                            c_devs.getInt(idDevIndex), c_devs.getString(nameIndex), c_devs.getString(serviceIndex),
                                            c_devs.getString(placeIndex), c_devs.getString(typeIndex), c_devs.getString(fact_numIndex),
                                            c_devs.getString(accuracyIndex),c_devs.getString(next_checkIndex),c_devs.getString(prev_readingIndex),
                                            c_devs.getString(type_readingIndex),c_devs.getString(date_readingIndex),c_devs.getString(cur_readingIndex),
                                            c_devs.getString(commentIndex), c_devs.getInt(passedDevIndex)
                                    );
                                    mds.add(md);
                                }while(c_devs.moveToNext());
                                Enity.devices = mds;
                            }
                        }
                    }
                    String orderBy = "row, col DESC";
                    Cursor c_saldo =  db.query("saldo", null, "id_ab = ?", new String[]{Integer.toString(c.getInt(idIndex))}, null, null, orderBy);
                    Enity.saldo = new ArrayList<Saldo>();
                    if(c_saldo!=null){
                        if(c_saldo.moveToFirst()){
                            int rowIndex = c_saldo.getColumnIndex("row");
                            int colIndex = c_saldo.getColumnIndex("col");
                            int valueIndex = c_saldo.getColumnIndex("value");
                            boolean firstIn = true;
                            int rowCount = 0;
                            int colCount = 0;
                            int position = 0;
                            ArrayList<Saldo> saldo = new ArrayList<Saldo>();
                            ArrayList<String> sal = new ArrayList<String>();
                            do {
                                if (firstIn){
                                    rowCount = c_saldo.getInt(rowIndex);
                                    colCount = c_saldo.getInt(colIndex);
                                    position = colCount;
                                    firstIn = false;
                                }
                               /* if (position > 0) {
                                    String value = c_saldo.getString(valueIndex);
                                    sal.add(value);
                                    position --;
                                }else{
                                    String value = c_saldo.getString(valueIndex);
                                    sal.add(value);
                                    ArrayList<String> s_tmp = sal;
                                    Enity.saldo.add(new Saldo(s_tmp));
                                    sal.clear();
                                    position = colCount-1;
                                }*/
                                sal.add(c_saldo.getString(valueIndex));
                            } while (c_saldo.moveToNext());
                            Enity.saldo = makeSaldo(sal,colCount);
                        }
                    }

                Units.add(Enity);
                }while(c.moveToNext());

            }
        }
        return Units;
    }


    public Unit getUnit(int id){
        db = this.getWritableDatabase();
        Unit Enity = new Unit();
        Cursor c = db.query("abonent", null, "id = "+Integer.toString(id),null, null, null, null);
        if (c!=null){
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex("id");
                int FIOIndex = c.getColumnIndex("name");
                int AccountIndex = c.getColumnIndex("account");
                int AddressIndex = c.getColumnIndex("address");
                int CountDevsIndex = c.getColumnIndex("count_of_devs");
                int PassedIndex = c.getColumnIndex("passed");
                do{
                    Enity = new Unit();
                    Enity.account = c.getString(AccountIndex);
                    Enity.FIO = c.getString(FIOIndex);
                    Enity.id = c.getInt(idIndex);
                    Enity.passed = c.getInt(PassedIndex);
                    Enity.address = c.getString(AddressIndex);
                    Cursor c_info = db.query("cont_info", null, "id_ab = ?", new String[]{Integer.toString(c.getInt(idIndex))}, null, null, null);
                    if (c_info!=null){
                        if(c_info.moveToFirst()){
                            int type = c_info.getColumnIndex("type");
                            int value = c_info.getColumnIndex("value");
                            int telcnt = 0;
                            do{
                                switch (c_info.getString(type)){
                                    case INFO_TEL:
                                        if (telcnt == 0){
                                            Enity.tel1 = c_info.getString(value);
                                        }else if (telcnt == 1){
                                            Enity.tel2 = c_info.getString(value);
                                        }else if (telcnt == 2){
                                            Enity.tel3 = c_info.getString(value);
                                        }else if (telcnt == 3){
                                            Enity.tel4 = c_info.getString(value);
                                        }
                                        telcnt++;
                                        break;
                                    case INFO_EMAIL:
                                        Enity.email = c_info.getString(value);
                                        break;
                                    case INFO_AREA:
                                        Enity.area = c_info.getString(value);
                                        break;
                                    case INFO_LIVING:
                                        Enity.number_of_living = c_info.getString(value);
                                        break;
                                    case INFO_ROOM:
                                        Enity.room = c_info.getString(value);
                                        break;
                                }
                            }while(c_info.moveToNext());
                        }
                    }
                    if (c.getInt(CountDevsIndex)>0){
                        ArrayList<MeteringDevice> mds = new ArrayList<MeteringDevice>();
                        Cursor c_devs = db.query("device", null, "id_ab = ?", new String[]{Integer.toString(c.getInt(idIndex))}, null, null, null);
                        if (c_devs!=null){
                            if(c_devs.moveToFirst()){
                                int idDevIndex = c_devs.getColumnIndex("id");
                                int nameIndex = c_devs.getColumnIndex("name");
                                int placeIndex = c_devs.getColumnIndex("place");
                                int serviceIndex = c_devs.getColumnIndex("service");
                                int typeIndex = c_devs.getColumnIndex("type");
                                int fact_numIndex = c_devs.getColumnIndex("factory_num");
                                int accuracyIndex = c_devs.getColumnIndex("accuracy");
                                int next_checkIndex = c_devs.getColumnIndex("next_check");
                                int prev_readingIndex = c_devs.getColumnIndex("prev_reading");
                                int type_readingIndex = c_devs.getColumnIndex("type_reading");
                                int date_readingIndex = c_devs.getColumnIndex("date_reading");
                                int cur_readingIndex = c_devs.getColumnIndex("cur_reading");
                                int commentIndex = c_devs.getColumnIndex("comment");
                                int passedDevIndex = c_devs.getColumnIndex("passed");
                                do{
                                    MeteringDevice md = new MeteringDevice(
                                            c_devs.getInt(idDevIndex), c_devs.getString(nameIndex), c_devs.getString(serviceIndex),
                                            c_devs.getString(placeIndex), c_devs.getString(typeIndex), c_devs.getString(fact_numIndex),
                                            c_devs.getString(accuracyIndex),c_devs.getString(next_checkIndex),c_devs.getString(prev_readingIndex),
                                            c_devs.getString(type_readingIndex),c_devs.getString(date_readingIndex),c_devs.getString(cur_readingIndex),
                                            c_devs.getString(commentIndex), c_devs.getInt(passedDevIndex)
                                    );
                                    mds.add(md);
                                }while(c_devs.moveToNext());
                                Enity.devices = mds;
                            }
                        }
                    }
                    String orderBy = "row, col DESC";
                    Cursor c_saldo =  db.query("saldo", null, "id_ab = ?", new String[]{Integer.toString(c.getInt(idIndex))}, null, null, orderBy);
                    Enity.saldo = new ArrayList<Saldo>();
                    if(c_saldo!=null){
                        if(c_saldo.moveToFirst()){
                            int rowIndex = c_saldo.getColumnIndex("row");
                            int colIndex = c_saldo.getColumnIndex("col");
                            int valueIndex = c_saldo.getColumnIndex("value");
                            boolean firstIn = true;
                            int rowCount = 0;
                            int colCount = 0;
                            int position = 0;
                            ArrayList<Saldo> saldo = new ArrayList<Saldo>();
                            ArrayList<String> sal = new ArrayList<String>();
                            do {
                                if (firstIn){
                                    rowCount = c_saldo.getInt(rowIndex);
                                    colCount = c_saldo.getInt(colIndex);
                                    position = colCount;
                                    firstIn = false;
                                }
                               /* if (position > 0) {
                                    String value = c_saldo.getString(valueIndex);
                                    sal.add(value);
                                    position --;
                                }else{
                                    String value = c_saldo.getString(valueIndex);
                                    sal.add(value);
                                    ArrayList<String> s_tmp = sal;
                                    Enity.saldo.add(new Saldo(s_tmp));
                                    sal.clear();
                                    position = colCount-1;
                                }*/
                                sal.add(c_saldo.getString(valueIndex));
                            } while (c_saldo.moveToNext());
                            Enity.saldo = makeSaldo(sal,colCount);
                        }
                    }

                 break;
                }while(c.moveToNext());

            }
        }
        return Enity;
    }

    public boolean isEmpty(){
        db = this.getWritableDatabase();
        Cursor c = db.query("abonent", null, null,null, null, null, null);
        int iter = 0;
        if (c!=null){
            if (c.moveToFirst()) {
                do{
                    iter++;
                }while(c.moveToNext());
            }
        }
        if(iter>0){ return false;}
        return true;
    }

    public boolean fillTables(String json_data) throws Exception{
        try {

            db = this.getWritableDatabase();
            JSONArray jarray = new JSONArray(json_data);
            Log.d("array:::","Количество элементов = "+jarray.length());
            ArrayList<HashMap<String, String>> enities = new ArrayList<HashMap<String, String>>();
            JSONObject ops = jarray.getJSONObject(0);
            ContentValues cont = new ContentValues();
            cont.put("name", ops.getString("FIO_cont"));
            cont.put("type", this.CONTROLLER);
            db.insert("operators", null, cont);
            ContentValues disp = new ContentValues();
            disp.put("name", ops.getString("FIO_disp"));
            disp.put("type", this.DISPATCHER);
            db.insert("operators", null, disp);
            ContentValues pass = new ContentValues();
            pass.put("type", "password");
            pass.put("id_ab", "-1");
            pass.put("value", ops.getString("password"));
            db.insert("cont_info", null, pass);

        /*   db.execSQL("create table device (id integer primary key autoincrement,id_ab integer, name text, place text, service text, type text" +
                "fact_num text,accuracy text, next_check text, prev_reading integer, type_reading text, cur_reading integer, comment text, passed integer);");*/
            for (int i = 1; i < jarray.length(); i++) {
                Log.d("array:::","Элемент = "+i);
                ContentValues enity = new ContentValues();
                JSONObject c = jarray.getJSONObject(i);
               // enity.put("id", Integer.toString(i));
                enity.put("name", c.getString("FIO"));
                enity.put("account", c.getString("account"));
                enity.put("address", c.getString("address"));
                JSONArray devs = c.getJSONArray("devices");
                enity.put("count_of_devs", Integer.toString(devs.length()));
                enity.put("passed", "0");
                long id = db.insert("abonent", null, enity);
                for (int j = 0; j < devs.length(); j++) {
                    JSONObject dev = devs.getJSONObject(j);
                    if (isEmptyDevice(dev)) {continue;}
                    ContentValues dev_val = new ContentValues();
                    dev_val.put("name", dev.getString("name"));
                    dev_val.put("id_ab", Long.toString(id));
                    dev_val.put("place", dev.getString("place"));
                    dev_val.put("service", dev.getString("service"));
                    dev_val.put("type", dev.getString("type"));
                    dev_val.put("factory_num", dev.getString("factory_num"));
                    dev_val.put("accuracy", dev.getString("accuracy"));
                    dev_val.put("next_check", dev.getString("next_check"));
                    dev_val.put("prev_reading", dev.getString("prev_reading"));
                    dev_val.put("type_reading", dev.getString("type_reading"));
                    dev_val.put("date_reading", dev.getString("date_reading"));
                    dev_val.put("comment", "");
                    dev_val.put("passed", "0");
                    db.insert("device", null, dev_val);
                }
                // db.execSQL("create table cont_info (id integer primary key autoincrement,type text,id_ab integer, value text);");
                ContentValues info = new ContentValues();
                info.put("id_ab",Long.toString(id));
                info.put("type", this.INFO_AREA);
                info.put("value", c.getString("area"));
                db.insert("cont_info", null, info);
                info.clear();

                info.put("id_ab", Long.toString(id));
                info.put("type", this.INFO_ROOM);
                info.put("value", c.getString("room"));
                db.insert("cont_info", null, info);
                info.clear();

                for (int k = 1; k < 10; k++) {
                    try {
                        info.put("id_ab",Long.toString(id));
                        info.put("type", this.INFO_TEL);
                        info.put("value", c.getString("tel" + Integer.toString(k)));
                        db.insert("cont_info", null, info);
                        info.clear();
                    } catch (Exception ex) {
                        break;
                    }
                }

                info.put("id_ab", Long.toString(id));
                info.put("type", this.INFO_EMAIL);
                info.put("value", c.getString("email"));
                db.insert("cont_info", null, info);
                info.clear();

                info.put("id_ab",Long.toString(id));
                info.put("type", this.INFO_LIVING);
                info.put("value", c.getString("number_of_living"));
                db.insert("cont_info", null, info);
                info.clear();

                JSONObject s = c.getJSONObject("saldo");
                //  db.execSQL("create table saldo (id integer primary key autoincrement,id_ab integer, row integer, col integer);");
                int cols = Integer.parseInt(s.getJSONObject("info").getString("count_cols"));
                int rows = Integer.parseInt(s.getJSONObject("info").getString("count_rows"));
                JSONObject header = s.getJSONObject("header");
                for (int k = 0; k < cols; k++) {
                    ContentValues cell = new ContentValues();
                    cell.put("row", "0");
                    cell.put("col", Integer.toString(k));
                    cell.put("id_ab", Long.toString(id));
                    cell.put("value", header.getString("i" + Integer.toString(k)));
                    db.insert("saldo", null, cell);
                }
                for (int l = 0; l < rows; l++) {
                    JSONObject row = s.getJSONObject("i" + Integer.toString(l));
                    for (int k = 0; k < cols; k++) {
                        ContentValues cell = new ContentValues();
                        cell.put("row", Integer.toString(l + 1));
                        cell.put("col", Integer.toString(k));
                        cell.put("id_ab", Long.toString(id));
                        cell.put("value", row.getString("i" + Integer.toString(k)));
                        db.insert("saldo", null, cell);
                    }
                }


            }
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public String getPassword(){
        db = this.getReadableDatabase();
        Cursor c = db.query("cont_info",null,"type = ?",new String[] {"password"} ,null,null,null);
        String pass = "";
        if (c!=null){
            if (c.moveToFirst()){
                int valueIndex = c.getColumnIndex("count_of_devs");
                do{
                    pass = c.getString(valueIndex);
                }while (c.moveToNext());
            }
        }
        return pass;
    }

    public JSONArray makeResult() throws JSONException {
        db = getReadableDatabase();
        JSONArray jarr = new JSONArray();
        Cursor c = db.rawQuery("select distinct id_ab from result",null);
        if (c!=null){
            if(c.moveToFirst()){
                int id_abIndex = c.getColumnIndex("id_ab");
                do{
                    int id = c.getInt(id_abIndex);
                    Cursor c_ab = db.query("abonent", null, "id = ?", new String[]{Integer.toString(id)} ,null,null,null);
                    if(c_ab!=null){
                        if(c_ab.moveToFirst()){
                            int accIndex = c_ab.getColumnIndex("account");
                            int fioIndex = c_ab.getColumnIndex("name");
                            JSONObject jobj = new JSONObject();
                            do{
                                jobj.put("fio", c_ab.getString(fioIndex));
                                jobj.put("ls", c_ab.getString(accIndex));
                                Cursor c_res = db.rawQuery("select DEV.id_ab as id_ab, DEV.factory_num as num, DEV.place as place, RES.reading as reading, RES.date as date, RES.comment as comment" +
                                        " from device as DEV " +
                                        "inner join result as RES " +
                                        "on DEV.id = RES.id_dev " +
                                        "where DEV.id_ab = ?",new String[]{Integer.toString(id)});
                                if (c_res!=null){
                                    if(c_res.moveToFirst()){
                                        int numIndex = c_res.getColumnIndex("num");
                                        int placeIndex = c_res.getColumnIndex("place");
                                        int readingIndex = c_res.getColumnIndex("reading");
                                        int dateIndex = c_res.getColumnIndex("date");
                                        int commentIndex = c_res.getColumnIndex("comment");
                                        JSONArray devs = new JSONArray();
                                        do{
                                            JSONObject dev = new JSONObject();
                                            dev.put("num", c_res.getString(numIndex).replaceAll("\"", "\\\""));
                                            dev.put("place",c_res.getString(placeIndex).replaceAll("\"", "\\\""));
                                            dev.put("comment",c_res.getString(commentIndex).replaceAll("\"", "\\\""));
                                            dev.put("cur_reading",c_res.getString(readingIndex).replaceAll("\"", "\\\""));
                                            dev.put("date", c_res.getString(dateIndex).replaceAll("\"","\\\""));
                                            devs.put(dev);
                                        }while(c_res.moveToNext());
                                        jobj.put("devices",devs);
                                    }
                                }
                                break;
                            }while(c_ab.moveToNext());
                            jarr.put(jobj);
                        }
                    }

                }while(c.moveToNext());
            }
        }
        return jarr;
    }

    public void clearData(){
        onUpgrade(db, 1, 1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersiod, int newVersion){
        db = this.getWritableDatabase();
        db.execSQL("drop table device");
        db.execSQL("drop table abonent");
        db.execSQL("drop table cont_info");
        db.execSQL("drop table saldo");
        db.execSQL("drop table result");
        db.execSQL("drop table operators");
        db.execSQL("create table abonent (id integer primary key autoincrement,name text, account text, address text, count_of_devs integer, passed integer);");
        db.execSQL("create table cont_info (id integer primary key autoincrement,type text,id_ab integer, value text);");
        db.execSQL("create table saldo (id integer primary key autoincrement,id_ab integer, row integer, col integer, value text);");
        db.execSQL("create table device (id integer primary key autoincrement,id_ab integer, name text, place text, service text, type text, factory_num,  accuracy text, next_check text, prev_reading integer, type_reading text, date_reading text, cur_reading integer, comment text, passed integer);");
        db.execSQL("create table result (id integer primary key autoincrement,id_ab integer, id_dev integer, reading integer, place text, comment text, date text);");
        db.execSQL("create table operators (id integer primary key autoincrement, name text, type text);");

    }

    private ArrayList<Saldo> makeSaldo(ArrayList<String> data, int colums){
        ArrayList<Saldo> main = new ArrayList<Saldo>();
        Saldo tmp = new Saldo(new ArrayList<String>());
        colums ++;
        for(int i=1;i<data.size()+1;i++){
            tmp.cell.add(data.get(i-1));
            if (i % colums == 0) {
                main.add(tmp);
                tmp = new Saldo(new ArrayList<String>());
            }

        }

        return main;
    }

    private boolean isEmptyDevice (JSONObject dev) throws Exception{
        boolean u1,u2,u3,u4,u4_1,u5,u6,u7,u8;
        u1 = dev.getString("name").equals("");
        u2 = dev.getString("place").equals("");
        u3 = dev.getString("type").equals("");
        u4 = dev.getString("factory_num").equals("null");
        u5 = dev.getString("accuracy").equals("null");
        u6 = dev.getString("prev_reading").equals("null");
        u7 = dev.getString("type_reading").equals("");
        u8 = dev.getString("date_reading").equals("null");

        if (dev.getString("name").equals("") && dev.getString("place").equals("") && dev.getString("type").equals("") &&
                dev.getString("factory_num").equals("null") && dev.getString("accuracy").equals("null") &&
                dev.getString("prev_reading").equals("null") && dev.getString("type_reading").equals("") && dev.getString("date_reading").equals("null")){
            return true;
        }
        return false;
    }


}
