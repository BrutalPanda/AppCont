package com.sample.drawer.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sample.drawer.MyShitMasterpice.DB;
import com.sample.drawer.R;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Saldo;
import com.sample.drawer.utils.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class Fragment3 extends Fragment {
    Context this_context;
    Fragment RealyThis = this;
    DB dbase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this_context = container.getContext();
        dbase = new DB(this_context);
        final View rootView =
                inflater.inflate(R.layout.fragment_3, container, false);
        Button restoreButton = (Button) rootView.findViewById(R.id.restoreTask);
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbase.clearData();
                Toast.makeText(getActivity(), "Очистка завершена! Приложение будет закрыто", Toast.LENGTH_SHORT).show();
                System.exit(1);
            }
        });

        Button setReadyToUpload = (Button) rootView.findViewById(R.id.setReadyToUpload);
        setReadyToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Подготовка данных.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Подготовка данных..", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Подготовка данных...", Toast.LENGTH_LONG).show();
                try {
                    makeFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Подготовка завершена!", Toast.LENGTH_SHORT).show();

            }
        });


        showDialog();




        return rootView;
    }

    public void showDialog(){
        final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(this_context);
        ratingdialog.setTitle("Администратор");

        View linearlayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_requestpassword, null);
        ratingdialog.setView(linearlayout);
        ratingdialog.setPositiveButton("Готово",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //txtView.setText(String.valueOf(rating.getRating()));
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().getSupportFragmentManager().beginTransaction().remove(RealyThis).commit();
                                //getActivity().finish();
                                dialog.cancel();
                            }
                        });
        ratingdialog.setCancelable(false);



        ratingdialog.create();
        ratingdialog.show();
    }



    private void makeFile() throws  JSONException{
        String gps_data = readFile("GPSTrack");
        String json_data = readFile("file");
        String cont = "";
        String disp = "";
        try {
            JSONArray jarray = new JSONArray(json_data);
            JSONObject c = jarray.getJSONObject(0);
            cont = c.getString("FIO_cont");
            disp = c.getString("FIO_disp");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (gps_data.trim()==null || gps_data.trim().equals("")){
            gps_data = "[\n" +
                    "        {\n" +
                    "            \"lon\": 0.0,\n" +
                    "            \"lat\": 0.0\n" +
                    "        }]";
        }
        String to_file = "{\n" +
                "    \"gps\":" + gps_data +"," + "\"maindata\":"+makeMainData().toString()+",\"ops\":{\"FIO_cont\":"+ "\"" + cont + "\","+
                                                                                                "\"FIO_disp\":"+ "\"" + disp + "\"}"+ "}";
        writeFile(to_file);
        Log.d("BackTrack",to_file);

    }

    private JSONArray makeMainData () throws JSONException{
        dbase = new DB(this_context);
        return dbase.makeResult();
    }

    private ArrayList<HashMap<String,String>> loadReadings() throws JSONException{
        String json_data = readFile("out_file").replaceAll("\"\"","\",\"");
        JSONArray jarray = new JSONArray(json_data);
        ArrayList<HashMap<String,String>> ret = new ArrayList<HashMap<String,String>>();
        JSONObject c = null;
        for (int i=0;i<jarray.length();i++) {
            c = jarray.getJSONObject(i);
            HashMap<String,String> rec = new HashMap<String,String>();
            rec.put ("fiols",c.getString("fiols"));
            rec.put ("place",c.getString("place"));
            rec.put ("comment",c.getString("comment"));
            rec.put ("cur_reading",c.getString("cur_reading"));
            rec.put ("num",c.getString("num"));
            rec.put ("date",c.getString("date"));
            ret.add(rec);
        }
        return ret;

    }

    private ArrayList<Unit> loadBaseData() throws JSONException {

        String json_data = readFile("file");

        JSONArray jarray = new JSONArray(json_data);

        JSONObject c = null;
        ArrayList<Unit> ret = new ArrayList<Unit>();
        for (int i=1;i<jarray.length();i++) { // от 1 ибо первый элемент - инфа о контролере
            c = jarray.getJSONObject(i);
            Unit unit = new Unit();
            unit.FIO = c.getString("FIO");
            unit.account = c.getString("account");
            unit.address = c.getString("address");
            unit.area = c.getString("area");
            unit.room = c.getString("room");
            unit.number_of_living = c.getString("number_of_living");
            unit.tel1 = c.getString("tel1");
            unit.tel2 = c.getString("tel2");
            unit.tel3 = c.getString("tel3");
            unit.email = c.getString("email");

            JSONArray root_devs_json = c.getJSONArray("devices");
            ArrayList<MeteringDevice> devs = new ArrayList<MeteringDevice>();
            for (int j=0;j<root_devs_json.length();j++){
                JSONObject dev_json = root_devs_json.getJSONObject(j);
                MeteringDevice md = new MeteringDevice(0,dev_json.getString("name"),dev_json.getString("service"),
                        dev_json.getString("place"),dev_json.getString("type"),dev_json.getString("factory_num"),
                        dev_json.getString("accuracy"),dev_json.getString("next_check"),dev_json.getString("prev_reading"),
                        dev_json.getString("type_reading"),dev_json.getString("date_reading"),"","",0);
                devs.add(md);
            }
            unit.devices = devs;
            ret.add(unit);

        }


        return ret;



    }

    private String readFile(String file) {
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getActivity().openFileInput(file)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;
                //Log.d(LOG_TAG, "Readed: " + str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void writeFile(String what){
        try {
            // отрываем поток для записи
            File dest = new File("/mnt/sdcard","out_file");
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));

                // пишем данные
            bw.write(what);
            // закрываем поток
            bw.close();
            // Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            Toast.makeText(this_context,e.getMessage().toString(),Toast.LENGTH_LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // отрываем поток для записи
            File dest = new File("/mnt/sdcard1","out_file");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));

            // пишем данные
            bw.write(what);
            // закрываем поток
            bw.close();
            // Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // отрываем поток для записи
            File dest = new File("/mnt/sdcard0","out_file");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));

            // пишем данные
            bw.write(what);
            // закрываем поток
            bw.close();
            // Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // отрываем поток для записи
            File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"out_file");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));

            // пишем данные
            bw.write(what);
            // закрываем поток
            bw.close();
            // Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
