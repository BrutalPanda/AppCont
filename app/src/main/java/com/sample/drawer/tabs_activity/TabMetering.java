package com.sample.drawer.tabs_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.drawer.R;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Slava-laptop on 27.07.2015.
 */
public class TabMetering extends Activity  {

    HashMap<Integer,HashMap<String,String>> new_data = new HashMap<Integer,HashMap<String,String>>();
    int _position = -1;
    boolean allow_apply = true;
    Unit data = null;
    String _dev_name;

    private void initialize(){

        /*HashMap<String,String> data = new HashMap<String,String>();
        data.put("Place","Место1");
        data.put("Type","Тип2");
        data.put("FacNum","Номер1");
        data.put("Accuracy","Точность1");
        data.put("NextCheck","СлПроверка1");
        data.put("PrevReading","ПредПок1");
        data.put("TypeReading","ТипПок1");
        data.put("DateReading","Дата1");
        data.put("CurReading","000");

        data_by_dev.add(data);
        HashMap<String,String> data2 = new HashMap<String,String>();
        data2.put("Place","Место2");
        data2.put("Type","Тип2");
        data2.put("FacNum","Номер2");
        data2.put("Accuracy","Точность2");
        data2.put("NextCheck","СлПроверка2");
        data2.put("PrevReading","ПредПок2");
        data2.put("TypeReading","ТипПок2");
        data2.put("DateReading","Дата2");
        data2.put("CurReading","0000");

        data_by_dev.add(data2);*/

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_metering);
        // адаптер
        Intent intent = getIntent();
        data = intent.getParcelableExtra("data");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getDeviceNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //initialize();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Приборы учета");
        // выделяем элемент
        spinner.setSelection(0);




        fillTable(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                // Ite iv = (MenuView.ItemView) parent.getSelectedView();
                // Toast.makeText(getBaseContext(), "Position = " + _dev_name, Toast.LENGTH_SHORT).show();
                _position = position;
                fillTable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final Button apply = (Button) findViewById(R.id.ApplyChanges);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allow_apply){return;}
                TextView tv1 = (TextView) findViewById(R.id.metering_place);
                String new_place = tv1.getText().toString();
                TextView tv2 = (TextView) findViewById(R.id.metering_current_reading);
                String cur_reading = tv2.getText().toString();
                TextView tv3 = (TextView) findViewById(R.id.metering_factory_num);
                TextView tv4= (TextView)findViewById(R.id.metering_prev_reading);
                String dev_num = (String) tv3.getText();
                EditText tv5= (EditText)findViewById(R.id.metering_comment);
                String comment =  tv5.getText().toString();

                //TextView tv4 = (TextView)findViewById(R.id.account_text);
                //String FIO_LS = tv4.getText().toString();
                String FIO_LS = data.FIO + " " + data.account;
                JSONObject tofile = new JSONObject();
                HashMap<String,String> nd = new HashMap<String, String>();
                int cur = 0;
                int last = 0;
                try{last=Integer.parseInt(tv4.getText().toString().trim());}catch(Exception ex){}
                try {
                    cur = Integer.parseInt(tv2.getText().toString().trim());
                } catch (Exception ex){
                    Toast.makeText(getBaseContext(), "Возможен ввод только цифр!", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (cur<last){
                    Toast.makeText(getBaseContext(), "Внимание! Текущее значение меньше предыдущего!", Toast.LENGTH_SHORT).show();
                    tv2.setTextColor(Color.RED);
                }else{
                    tv2.setTextColor(Color.GREEN);
                }

                try {
                    tofile.put("place", new_place);
                    tofile.put("cur_reading", cur_reading);
                    tofile.put("comment", comment);
                    nd.put("place", new_place);
                    nd.put("cur_reading", cur_reading);
                    nd.put("comment", comment);
                    new_data.put(_position, nd);
                    apply.setEnabled(false);
                    tv1.setFocusable(false);
                    tv2.setFocusable(false);
                    tv5.setFocusable(false);
                    tofile.put("num", dev_num);

                    Date d = new Date();
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
                    tofile.put("date",format1.format(d));

                    tofile.put("fiols", FIO_LS);
                    writeFile(tofile.toString());
                    readFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(), "Запись успешно зафиксирована!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void fillTable(int index){
        MeteringDevice md = data.devices.get(index);
        TextView tv1 = (TextView)findViewById(R.id.metering_place);
        TextView tv2= (TextView)findViewById(R.id.metering_type);
        TextView tv3= (TextView)findViewById(R.id.metering_factory_num);
        TextView tv4= (TextView)findViewById(R.id.metering_accuracy);
        TextView tv5= (TextView)findViewById(R.id.metering_next_chek);
        TextView tv6= (TextView)findViewById(R.id.metering_prev_reading);
        TextView tv7= (TextView)findViewById(R.id.metering_prev_reading_date);
        TextView tv8= (TextView)findViewById(R.id.metering_type_reading);
        TextView tv9= (TextView)findViewById(R.id.metering_current_reading);
        TextView tv10= (TextView)findViewById(R.id.metering_comment);
        tv9.setTextColor(Color.BLACK);

        tv1.setText(md.place);
        tv2.setText(md.type);
        tv3.setText(md.factory_num);
        tv4.setText(md.accuracy);
        tv5.setText(md.next_check);
        tv6.setText(md.prev_reading);
        tv7.setText(md.date_reading);
        tv8.setText(md.type_reading);
        tv9.setText(md.cur_reading);
        Button apply = (Button) findViewById(R.id.ApplyChanges);


        try {
            tv1.setFocusable(false);
            tv9.setFocusable(false);
            tv10.setFocusable(false);
            apply.setEnabled(false);
            tv9.setText(new_data.get(index).get("cur_reading"));
            tv1.setText(new_data.get(index).get("place"));
            tv10.setText(new_data.get(index).get("comment"));

            int cur = 0;
            int last = 0;
            try{last=Integer.parseInt(tv6.getText().toString().trim());}catch(Exception ex){}
            try {cur = Integer.parseInt(tv9.getText().toString().trim());} catch (Exception ex){}
            if (cur<last){
                tv9.setTextColor(Color.RED);
            }else{
                tv9.setTextColor(Color.GREEN);
            }
        }catch (Exception ex){
            tv1.setFocusable(true);
            tv1.setFocusableInTouchMode(true);
            tv9.setFocusable(true);
            tv9.setFocusableInTouchMode(true);
            tv10.setFocusable(true);
            tv10.setFocusableInTouchMode(true);
            apply.setEnabled(true);

        }



    }

    private String[] getDeviceNames(){
        ArrayList<MeteringDevice> mds = data.devices;
        String out_data = readOutFile();
        String out[] = out_data.split("\\{");
        ArrayList<String> namesList = new ArrayList<String>();

        for (int i=0;i<mds.size();i++){
            boolean contain = false;
            for (int j=0;j<out.length;j++){
                if (out[j].contains(data.FIO+" "+data.account)){
                    if (out[j].contains(mds.get(i).factory_num)){
                        contain = true;
                    }
                }
            }
            if (!contain) {
                namesList.add( mds.get(i).name);
            }
        }
        String[] names = new String[namesList.size()];
        for (int i=0;i<namesList.size();i++){
            names[i] = namesList.get(i);
        }
        return names;
    }

    void writeFile(String to) throws IOException {
        String str=readFile();
        str = str.replaceAll("]", "").replaceAll("\\[", "");
       // str = new StringBuffer(str).reverse().toString();
        str = str.replaceFirst(",", "");
       // str = new StringBuffer(str).reverse().toString();



        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput("out_file", MODE_PRIVATE)));
            // пишем данные
            bw.write("["+str+","+to+"]");
            // закрываем поток
            bw.close();
           // Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            File file = new File("out_file");
            file.createNewFile();
            writeFile(to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readFile() {
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput("out_file")));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;

            }
            Log.d("FromFileReaded:::::", ret);
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String readOutFile(){
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                   openFileInput("out_file")));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;

            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
