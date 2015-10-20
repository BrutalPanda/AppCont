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

import com.sample.drawer.MyShitMasterpice.DB;
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
    DB dbase;
    private void initialize(){



    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_metering);
        // адаптер
        Intent intent = getIntent();
        data = intent.getParcelableExtra("data");
        if (data.devices==null){
            Toast.makeText(this.getBaseContext(),"У текущего ЛС нет приборов учета!",Toast.LENGTH_SHORT).show();
            //setResult(RESULT_OK);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getDeviceNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //initialize();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Приборы учета");
        // выделяем элемент
        spinner.setSelection(0);
        dbase = new DB(this);



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
                TextView tv4= (TextView) findViewById(R.id.metering_prev_reading);
                String dev_num = (String) tv3.getText();
                EditText tv5= (EditText)findViewById(R.id.metering_comment);
                String comment =  tv5.getText().toString();


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

                apply.setEnabled(false);
                tv1.setFocusable(false);
                tv2.setFocusable(false);
                tv5.setFocusable(false);

                Date d = new Date();
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
                if (dbase.setPassed(data.id,comment,new_place,Integer.parseInt(cur_reading),format1.format(d))) {
                    Toast.makeText(getBaseContext(), "Запись успешно зафиксирована!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Ошибка! Запись не зафиксирована!", Toast.LENGTH_SHORT).show();
                }

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
        String names[] = new String[data.devices.size()];
        for (int i=0;i<data.devices.size();i++){
            names[i] = data.devices.get(i).name;
        }
        return names;
    }




}
