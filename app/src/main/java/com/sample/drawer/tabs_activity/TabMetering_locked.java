package com.sample.drawer.tabs_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Slava-laptop on 05.08.2015.
 */
public class TabMetering_locked extends Activity {
    Unit data = null;
    DB dbase ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_metering_locked);
        // �������
        Intent intent = getIntent();
        data = intent.getParcelableExtra("data");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getDeviceNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //initialize();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // ���������
        spinner.setPrompt("������� �����");
        // �������� �������
        spinner.setSelection(0);
        dbase = new DB(this);

        fillTable(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // ���������� ������� �������� ��������
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                // Ite iv = (MenuView.ItemView) parent.getSelectedView();
                // Toast.makeText(getBaseContext(), "Position = " + _dev_name, Toast.LENGTH_SHORT).show();

                fillTable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        findViewById(R.id.metering_comment).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText tv5 = (EditText) findViewById(R.id.metering_comment);
                    String comment = tv5.getText().toString();
                    TextView tv1 = (TextView) findViewById(R.id.metering_place);
                    String new_place = tv1.getText().toString();
                    Date d = new Date();
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
                    dbase.setCommentAndPlace(data.id, comment, new_place, format1.format(d));
                }
            }
        });
        findViewById(R.id.metering_place).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText tv5 = (EditText) findViewById(R.id.metering_comment);
                    String comment = tv5.getText().toString();
                    TextView tv1 = (TextView) findViewById(R.id.metering_place);
                    String new_place = tv1.getText().toString();
                    Date d = new Date();
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
                    dbase.setCommentAndPlace(data.id, comment, new_place, format1.format(d));
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
        tv10.setText(md.comment);


        try {
            int cur = 0;
            int last = 0;
            try{last=Integer.parseInt(tv6.getText().toString().trim());}catch(Exception ex){}
            try {cur = Integer.parseInt(tv9.getText().toString().trim());} catch (Exception ex){}
            if (cur<last){
                tv9.setTextColor(Color.RED);
            }else{
                tv9.setTextColor(Color.GREEN);
            }
        }catch (Exception ex){}



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
